#!/usr/bin/env coffee
#
#   Encode video on the EC2 cluster. Since we're encoding individual
#   frames from S3, the bottleneck is network rather than CPU. Here
#   we spin up a small EC2 spot instance just to do the encode. It will
#   power off when done. Logs and final video are both uploaded to
#   public files on S3.
#
#   AWS configuration comes from the environment:
#
#      AWS_ACCESS_KEY_ID
#      AWS_SECRET_ACCESS_KEY
#      AWS_REGION
#      HQZ_BUCKET
#
#   Required Node modules:
#
#      npm install aws-sdk coffee-script async
#
######################################################################
#
#   This file is part of HQZ, the batch renderer for Zen Photon Garden.
#
#   Copyright (c) 2013 Micah Elizabeth Scott <micah@scanlime.org>
#
#   Permission is hereby granted, free of charge, to any person
#   obtaining a copy of this software and associated documentation
#   files (the "Software"), to deal in the Software without
#   restriction, including without limitation the rights to use,
#   copy, modify, merge, publish, distribute, sublicense, and/or sell
#   copies of the Software, and to permit persons to whom the
#   Software is furnished to do so, subject to the following
#   conditions:
#
#   The above copyright notice and this permission notice shall be
#   included in all copies or substantial portions of the Software.
#
#   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
#   EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
#   OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
#   NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
#   HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
#   WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
#   FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
#   OTHER DEALINGS IN THE SOFTWARE.
#

# Tweakables

kRoleName       = 'hqz-node'        # We expect cluster-start has already set up the role
kSpotPrice      = "0.25"            # Maximum price per hour
kImageId        = "ami-2efa9d47"    # Ubuntu 12.04 LTS, x64, us-east-1
kInstanceType   = "m1.medium"       # Biggest single-vCPU instance

kBucketName = process.env.HQZ_BUCKET

######################################################################

AWS = require 'aws-sdk'
async = require 'async'
util = require 'util'

ec2 = new AWS.EC2().client
log = console.log

if process.argv.length != 3
    console.log "usage: cluster-encode <job>/<hash>"
    process.exit 1
kJobKey = process.argv[2]

# Files on the EC2 instance
kLogFile = "/tmp/encode.log"
kVideoFile = "/tmp/video.mp4"
kS3PutFile = "/tmp/s3put.js"

# Snapshot log to avoid MD5 mismatch errors
updateLog = "cp #{kLogFile} #{kLogFile}-snapshot &&
    node #{kS3PutFile} #{kLogFile}-snapshot #{kBucketName} text/plain #{kJobKey}.log"

# Redirection command to send output to our log
logRedirect = ">> #{kLogFile} 2>&1"

# Encode with libavcodec and libx264
encodeCommand = "avconv -y -r 30 -i http://#{kBucketName}.s3.amazonaws.com/#{kJobKey}-%04d.png
    -c:v libx264 -preset slow -crf 18 -pix_fmt yuv420p #{kVideoFile}"

# Very tiny node.js S3 uploader. This is preferable to s3cmd, since the node.js aws-sdk
# knows how to retrieve IAM credentials over the metadata socket automatically.
s3put = "
    aws = require('aws-sdk');
    s3 = new aws.S3({ apiVersion: '2006-03-01' }).client;

    cb = function (e) { if (e) {
        console.log(JSON.stringify(e));
        process.exit(1);
    }};

    require('fs').readFile( process.argv[2], function (error, data) {
        cb(error);
        s3.putObject({
            Bucket: process.argv[3],
            ContentType: process.argv[4],
            Key: process.argv[5],
            ACL: 'public-read',
            Body: data
        }, cb);
    });
    "

script = """
    #!/bin/sh

    echo deb http://us-east-1.ec2.archive.ubuntu.com/ubuntu/ precise multiverse >> /etc/apt/sources.list
    echo deb http://us-east-1.ec2.archive.ubuntu.com/ubuntu/ precise-updates multiverse >> /etc/apt/sources.list

    apt-get update
    apt-get install -y libavcodec-extra-53 libav-tools nodejs npm
    npm install -g aws-sdk

    ln -s /usr/bin/nodejs /usr/bin/node
    export NODE_PATH=/usr/local/lib/node_modules
    export AWS_REGION=#{ AWS.config.region }
    echo "#{s3put}" > #{kS3PutFile}

    echo [`date`] Starting encode job #{kJobKey} #{logRedirect}
    echo #{logRedirect}
    #{updateLog}

    #{encodeCommand} #{logRedirect} &

    # Periodically upload log as long as the encoder is running
    sleep 5
    while pidof avconv > /dev/null; do
        #{updateLog}
        sleep 5
    done

    echo #{logRedirect}
    echo [`date`] Encode finished, uploading #{logRedirect}
    #{updateLog}

    node #{kS3PutFile} #{kVideoFile} #{kBucketName} video/mp4 #{kJobKey}.mp4 #{logRedirect}

    echo #{logRedirect}
    echo [`date`] Upload finished, done. #{logRedirect}
    #{updateLog}

    poweroff
    """

log "Requesting spot instance"
ec2.requestSpotInstances
    SpotPrice: kSpotPrice
    InstanceCount: 1
    Type: "one-time"
    LaunchSpecification:
        ImageId: kImageId
        InstanceType: kInstanceType
        IamInstanceProfile:
            Name: "#{kRoleName}-instance"
        UserData:
            Buffer(script).toString('base64')
    (error, data) ->
        return log util.inspect error if error
        log "Instance requested: #{ data.SpotInstanceRequests[0].SpotInstanceRequestId }"
        log "The encoder will be logging to:"
        log "    http://#{kBucketName}.s3.amazonaws.com/#{kJobKey}.log"
        log "When finished, the encoded video will be at:"
        log "    http://#{kBucketName}.s3.amazonaws.com/#{kJobKey}.mp4"
