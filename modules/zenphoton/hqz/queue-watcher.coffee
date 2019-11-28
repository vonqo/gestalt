#!/usr/bin/env coffee
#
#   Watch the results of our rendering queue, and download the finished images.
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
util = require 'util'
fs = require 'fs'

log = (msg) -> console.log "[#{ (new Date).toJSON() }] #{msg}"
logURL = console.log

outputFile = "queue-watcher.log"

sqs = new AWS.SQS({ apiVersion: '2012-11-05' }).client
s3 = new AWS.S3({ apiVersion: '2006-03-01' }).client

fileLog = (msg) ->
    out = fs.createWriteStream outputFile, {flags: 'a'}
    out.end msg + '\n'

pad = (str, length) ->
    str = '' + str
    str = '0' + str while str.length < length
    return str

msgDuration = (msg) ->
    millis = new Date(msg.FinishTime) - new Date(msg.StartedTime)
    sec = (millis / 1000) | 0
    mins = (sec / 60) | 0
    hours = (mins / 60) | 0
    return hours + ':' + pad(mins % 60, 2) + ':' + pad(sec % 60, 2) + '.' + pad(millis % 1000, 3)

class Watcher
    constructor: ->
        @jobs = {}
        @polling = 0
        @pendingDeletions = []
        @ident = 0

    replaySync: (filename, cb) ->
        try
            lines = fs.readFileSync(filename).toString().split('\n')
        catch error
            # Okay if this file doesn't exist
            cb error if error.code != 'ENOENT'
            return

        for line in lines
            if line.length and line[0] == '{'
                try
                    @handleMessage JSON.parse(line), cb
                catch error
                    cb error

    run: (queueName, cb) ->
        sqs.createQueue
            QueueName: queueName
            (error, data) =>
                if error
                    cb error
                if data
                    @queue = data.QueueUrl
                    log 'Watching for results...'
                    @pollQueue()

    pollQueue: ->
        # Handle any pending message deletions at the same rate as our queue polling.
        if @pendingDeletions.length > 0
            batchSize = Math.min 10, @pendingDeletions.length
            batch = @pendingDeletions.slice 0, batchSize
            @pendingDeletions = @pendingDeletions.slice batchSize
            sqs.deleteMessageBatch
                QueueUrl: @queue
                Entries: batch
                (error, data) =>
                    if error
                        log error
                        @pendingDeletions = @pendingDeletions.concat batch

        @polling += 1
        sqs.receiveMessage
            QueueUrl: @queue
            MaxNumberOfMessages: 10
            VisibilityTimeout: 120
            WaitTimeSeconds: 10

            (error, data) =>
                @polling -= 1
                return log error if error
                if data and data.Messages
                    for m in data.Messages
                        do (m) =>
                            cb = (error) => @messageComplete(error, m)
                            try
                                @logAndHandleMessage JSON.parse(m.Body), cb
                            catch error
                                cb error
                @pollQueue()

    nextID: () ->
        @ident += 1
        return '' + @ident

    messageComplete: (error, m) ->
        return log "Error processing message: " + util.inspect error if error

        @pendingDeletions.push
            Id: @nextID()
            ReceiptHandle: m.ReceiptHandle            

        @pollQueue() if not @polling

    logAndHandleMessage: (msg, cb) ->
        fileLog JSON.stringify msg
        @handleMessage msg, cb

    resendMessage: (msg, cb) ->
        # XXX: Kind of a huge hack!
        sqs.createQueue
            QueueName: "zenphoton-hqz-render-queue"
            (error, data) =>
                return cb error if error
                sqs.sendMessage
                    QueueUrl: data.QueueUrl
                    MessageBody: JSON.stringify msg
                    cb

    handleMessage: (msg, cb) ->
        # Use job metadata left for us by queue-submit
        @jobs[msg.JobKey] = [] if not @jobs[msg.JobKey]
        job = @jobs[msg.JobKey]
        index = msg.JobIndex or 0

        # Do we have a new URL to show?
        if msg.State == 'finished'
            logURL "http://#{ msg.OutputBucket }.s3.amazonaws.com/#{ msg.OutputKey }"

        # Are we repairing failures?
        if msg.State == 'failed' and process.argv[2] == '--retry-failed'
            @resendMessage msg, (error, data) ->
                return log "Error resending message: " + util.inspect error if error
                log "Resent message: " + util.inspect msg

        # Update the state of this render job. Note that messages may arrive out of order,
        # so 'started' only has an effect if the frame hasn't already taken on a different state.    
        job[index] = msg.State if msg.State != 'started' or !job[index]

        # Summarize the job state
        summary = for i in [0 .. job.length - 1]
            switch job[i]
                when 'finished' then '#'
                when 'started' then '.'
                when 'failed' then '!'
                else ' '

        extra = switch msg.State
            when 'finished' then " in #{ msgDuration msg }"
            else ''

        log "[#{ summary.join '' }] -- #{msg.JobKey} [#{index}] #{msg.State}#{extra}"
        cb()


cb = (error) -> log util.inspect error if error
qw = new Watcher
qw.replaySync outputFile, cb
qw.run "zenphoton-hqz-results"
