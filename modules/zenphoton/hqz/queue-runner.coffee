#!/usr/bin/env coffee
#
#   Queue runner: This is a daemon that sits on any machine with
#   CPU power and a connection to the internet. It waits on render
#   jobs to arrive on an Amazon SQS message queue.
#
#   AWS configuration comes from the environment:
#
#      AWS_ACCESS_KEY_ID
#      AWS_SECRET_ACCESS_KEY
#      AWS_REGION
#
#   Required Node modules:
#
#      npm install aws-sdk coffee-script async async-cache
#
#   This accepts JSON messages on an SQS queue. These messages are
#   objects with the following members:
#
#      SceneBucket:     S3 bucket for scene data
#      SceneKey:        S3 key for scene data
#      SceneIndex:      Optional line index in scene JSON data
#      OutputBucket:    S3 bucket for output data
#      OutputKey:       S3 key for output data
#      OutputQueueUrl:  SQS QueueUrl to post completion messages to
#
#   On completion, a superset of the above JSON will be sent back
#   to the output queue.
#
#   Auto-shutdown is optional. Normally queue-runner will keep running
#   indefinitely. But to help in building clusters that automatically
#   scale down, an HQZ_MIN_CPU environment variable can
#   be set. When the proportion of in-use CPUs to available CPUs stays
#   below this value for 10 minutes, we exit.
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

AWS = require 'aws-sdk'
async = require 'async'
AsyncCache = require 'async-cache'
util = require 'util'
child_process = require 'child_process'
os = require 'os'
zlib = require 'zlib'

AWS.config.maxRetries = 50
sqs = new AWS.SQS({ apiVersion: '2012-11-05' }).client
s3 = new AWS.S3({ apiVersion: '2006-03-01' }).client
numCPUs = require('os').cpus().length
minUtilization = +(process.env.HQZ_MIN_CPU or 0)

kHeartbeatSeconds = 30
kHQZ = './hqz'


class Runner
    run: (queueName, cb) ->
        sqs.createQueue
            QueueName: queueName
            (error, data) =>
                if error
                    cb error
                if data
                    @queue = data.QueueUrl
                    @numRunning = 0
                    @numRequested = 0
                    @lookForWork()

    lookForWork: ->
        # How much work do we need? We never want to run more jobs than we have CPU cores.
        # Keep track of how many jobs we're actually running as well as all of the potential
        # jobs represented by outstanding sqs.receiveMessage() requests. Only issue more requests
        # if we have the capacity to handle what we get back.

        return if @numRequested
        count = Math.min 10, numCPUs - @numRunning
        msg = "[ #{ @numRunning } of #{ numCPUs } processes running ]"
        return log msg if count <= 0
        log msg + " -- Looking for work..."

        if @numRunning / numCPUs >= minUtilization
            @shutdownDeadline = null
        else
            now = (new Date).getTime()
            if !@shutdownDeadline 
                @shutdownDeadline = now + (10 * 60 * 1000)
            delta = @shutdownDeadline - now
            log "CPU Utilization below minimum of #{minUtilization}."
            if delta < 0
                log "Exiting"
                process.exit 0
            else
                log "Exiting in #{ delta / 1000 } seconds."

        @numRequested += count

        sqs.receiveMessage
            QueueUrl: @queue
            MaxNumberOfMessages: count
            VisibilityTimeout: kHeartbeatSeconds * 2
            WaitTimeSeconds: 10

            (error, data) =>
                @numRequested -= count

                return log "Error reading queue: " + util.inspect error if error
                if data and data.Messages

                    # Process incoming messages in parallel, report errors to console.
                    # Keep track of how many CPU cores are in use.

                    for m in data.Messages
                        do (m) =>
                            @numRunning += 1
                            m._running = true
                            @handleMessage m, (error) => @messageComplete(error, m)

                # Keep looking for work as long as we have idle CPU cores
                @lookForWork()

    handleMessage: (m, cb) ->
        try
            handler = new MessageHandler @queue, m, JSON.parse m.Body
            handler.start cb
        catch error
            cb error

    messageComplete: (error, m) ->
        log "Error processing message: " + util.inspect error if error
        if m._running
            # Only decrement after the first error.
            @numRunning -= 1
            m._running = false        
        @lookForWork()


class MessageHandler
    sceneMemo = {}

    constructor: (@queue, @envelope, @msg) ->
        @msg.Hostname = os.hostname()
        @msg.ReceivedTime = (new Date).toJSON()
        @msg.State = 'received'

    start: (asyncCb) ->
        # Start handling the message. Callback reports errors, and it reports
        # completion of the CPU-hungry portion of the message. Once the rendering
        # is done and we're uploading results, this continues on asynchronously
        # to make this handler's slot available to another message.

        async.waterfall [

            (cb) =>
                # Download scene data if we need it.
                s3cache.get "#{@msg.SceneBucket}/#{@msg.SceneKey}", cb

            (data, cb) =>
                # Decode the frame we're interested in
                try
                    if @msg.SceneIndex >= 0
                        @scene = data.toString().split('\n')[@msg.SceneIndex]
                    else
                        @scene = data
                catch error
                    return cb error

                log "Starting work on #{ @msg.OutputKey }"
                @msg.StartedTime = (new Date).toJSON()
                @msg.State = 'started'

                # Asynchronously let the world know we've started
                sqs.sendMessage
                    QueueUrl: @msg.OutputQueueUrl
                    MessageBody: JSON.stringify @msg
                    (error, data) => cb error if error

                # Start a watchdog, reminding us to refresh this message's visibility timer
                @watchdog = setInterval (() => @heartbeat()), kHeartbeatSeconds * 1000

                # Ask the child process to render the scene
                @runChildProcess cb

            (data, cb) =>
                # Upload finished scene
                log "Uploading results to #{@msg.OutputKey}"
                @msg.FinishTime = (new Date).toJSON()
                @msg.State = 'finished'

                # Scene is publicly readable, so we can refer to frames by URL easily.
                s3.putObject
                    Bucket: @msg.OutputBucket
                    Key: @msg.OutputKey
                    ContentType: 'image/png'
                    ACL: 'public-read'
                    Body: data
                    cb

                # Let another message start running
                asyncCb()

        ], (error) =>
            @cancelWatchdog
            if error
                # Log the error
                asyncCb error if error
                @msg.State = 'failed'
                @msg.Error = util.inspect error
            else
                @msg.UploadedTime = (new Date).toJSON()

            # Send final state change message after upload finishes
            sqs.sendMessage
                    QueueUrl: @msg.OutputQueueUrl
                    MessageBody: JSON.stringify @msg
                    (error, data) =>
                        # Done, we can delete the message now!
                        @cancelWatchdog()
                        sqs.deleteMessage
                            QueueUrl: @queue
                            ReceiptHandle: @envelope.ReceiptHandle
                            (error, data) =>
                                # Finished!
                                return asyncCb error if error
                                log "Finalized #{@msg.OutputKey} in #{@elapsedTime()} seconds"

    elapsedTime: () ->
        0.001 * ((new Date).getTime() - Date.parse(@msg.ReceivedTime))

    cancelWatchdog: () ->
        clearInterval @watchdog if @watchdog
        @watchdog = null

    runChildProcess: (cb) ->
        # Invokes callback with rendered image data after child process completes.

        @output = []
        @child = child_process.spawn kHQZ, ['-', '-'],
            env: '{}'
            stdio: ['pipe', 'pipe', process.stderr]

        @child.stdout.on 'data', (data) =>
            @output.push data

        @child.on 'exit', (code, signal) =>
            return cb "Render process exited with code " + code if code != 0
            @child = null
            cb null, bufferConcat @output

        @child.stdin.write @scene
        @child.stdin.end()

    heartbeat: ->
        # Periodically we need to reset our SQS message visibility timeout, so that
        # other nodes know we're still working on this job.

        log "Still working on #{ @msg.OutputKey } (#{ @elapsedTime() } seconds)"
        sqs.changeMessageVisibility
            QueueUrl: @queue
            ReceiptHandle: @envelope.ReceiptHandle
            VisibilityTimeout: kHeartbeatSeconds * 2
            (error) =>
                log "Error delivering heartbeat to #{ @msg.OutputKey }: #{ util.inspect error }" if error


log = (msg) ->
    console.log "[#{ (new Date).toJSON() }] #{msg}"


bufferConcat = (list) ->
    # Just like Buffer.concat(), but compatible with older versions of node.js
    size = 0
    for buf in list
        size += buf.length
    result = new Buffer size
    offset = 0
    for buf in list
        buf.copy(result, offset)
        offset += buf.length
    return result


s3cache = new AsyncCache
    # Cache decompressed objects from S3
    max: 512 * 1024 * 1024
    maxAge: 1000 * 60 * 60
    length: (obj) -> obj.length
    load: (key, cb) ->
        log "Downloading #{key}"
        path = key.split '/'
        bucket = path.shift()
        s3.getObject
            Bucket: bucket
            Key: path.join '/'
            (error, data) ->
                log "Decompressing #{key}"
                return cb error if error
                zlib.gunzip data.Body, cb


qr = new Runner
qr.run "zenphoton-hqz-render-queue", (error) ->
    console.log util.inspect error
    process.exit 1

