#!/usr/bin/env coffee
#
#   Cluster startup script. Experimental!
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

kRoleName       = 'hqz-node'
kSpotPrice      = "0.10"            # Maximum price per instance-hour
kImageId        = "ami-2efa9d47"    # Ubuntu 12.04 LTS, x64, us-east-1
kInstanceType   = "c1.xlarge"       # High-CPU instance

# Number of instances that will stay around to wait for the queue to fully empty.
# The rest of them will start exiting when they're less than 50% utilized.
kMaxStickyInstances = 1

kBucketName = process.env.HQZ_BUCKET

kInstanceTags   = [ {
    Key: "com.zenphoton.hqz"
    Value: "node"
} ]

######################################################################

AWS = require 'aws-sdk'
async = require 'async'
util = require 'util'

iam = new AWS.IAM().client
ec2 = new AWS.EC2().client
log = (msg) -> console.log "[#{ (new Date).toJSON() }] #{msg}"

if process.argv.length != 3
    console.log "usage: cluster-start <number of instances>"
    process.exit 1

numInstances = process.argv[2] | 0
process.exit 0 if numInstances <= 0

numStickyInstances = Math.min numInstances, kMaxStickyInstances
numBurstInstances = Math.max 0, numInstances - numStickyInstances

script = (minCPU) -> 
    # Set up queue-runner and run it in a loop until it
    # gracefully powers off due to having less than 'minCPU'
    # core utilization.
    """
    #!/bin/sh

    apt-get update
    apt-get install -y nodejs npm make gcc g++ git
    npm install -g coffee-script aws-sdk async async-cache

    ln -s /usr/bin/nodejs /usr/bin/node
    export NODE_PATH=/usr/local/lib/node_modules
    export AWS_REGION=#{ AWS.config.region }
    export HQZ_MIN_CPU=#{ minCPU }

    git clone https://github.com/scanlime/zenphoton.git
    cd zenphoton/hqz
    make

    until ./queue-runner.coffee; do true; done
    poweroff
    """

requestInstances = (minCPU, num, cb) ->
    return cb null, {} if num <= 0

    ec2.requestSpotInstances
        SpotPrice: kSpotPrice
        InstanceCount: num
        Type: "one-time"

        LaunchSpecification:
            ImageId: kImageId
            InstanceType: kInstanceType
            IamInstanceProfile:
                Name: "#{kRoleName}-instance"
            UserData:
                Buffer(script(minCPU)).toString('base64')

        # Tag each spot request
        (error, data) ->
            return cb error if error
            requests = ( spot.SpotInstanceRequestId for spot in data.SpotInstanceRequests )
            log '  ' + JSON.stringify requests
            ec2.createTags
                Resources: requests
                Tags: kInstanceTags
                cb

assumeRolePolicy =
    Version: "2012-10-17"
    Statement: [
        Effect: "Allow"
        Principal:
            Service: [ "ec2.amazonaws.com" ]
        Action: [ "sts:AssumeRole" ]
    ]

policyDocument =
    Statement: [
        {
            Action: [ "s3:*" ]
            Effect: "Allow"
            Resource: [ "arn:aws:s3:::#{kBucketName}/*" ]
        }, {
            Action: [
                "sqs:ChangeMessageVisibility"
                "sqs:CreateQueue"
                "sqs:DeleteMessage"
                "sqs:ReceiveMessage"
                "sqs:SendMessage"
            ]
            Effect: "Allow"
            Resource: [ "*" ]
        }
    ]

async.waterfall [

    # Create a security role for our new instances, if it doesn't already exist
    (cb) ->
        log "Setting up security role with S3 bucket #{kBucketName}"
        iam.createRole
            RoleName: kRoleName
            AssumeRolePolicyDocument: JSON.stringify assumeRolePolicy
            (error, data) ->
                return cb null, {} if error and error.code == 'EntityAlreadyExists'
                cb error, data

    (data, cb) ->
        log "Setting up role policy"
        iam.putRolePolicy
            RoleName: kRoleName
            PolicyName: "#{kRoleName}-policy"
            PolicyDocument: JSON.stringify policyDocument
            cb

    (data, cb) ->
        log "Creating instance profile"
        iam.createInstanceProfile
            InstanceProfileName: "#{kRoleName}-instance"
            (error, data) ->
                return cb null, {} if error and error.code == 'EntityAlreadyExists'
                cb error, data

    (data, cb) ->
        log "Adding role to instance profile"
        iam.addRoleToInstanceProfile
            InstanceProfileName: "#{kRoleName}-instance"
            RoleName: kRoleName
            (error, data) ->
                return cb null, {} if error and error.code == 'LimitExceeded'
                cb error, data

    (data, cb) -> 
        log "Requesting sticky spot instances"
        requestInstances 0.001, numStickyInstances, cb

    (data, cb) -> 
        log "Requesting burst spot instances"
        requestInstances 0.5, numBurstInstances, cb

], (error, data) ->
    return log util.inspect error if error
    log "Cluster starting up!"
