#!/usr/bin/env coffee
#
#   Quick script to get an overview of the queue state.
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
util = require 'util'
sqs = new AWS.SQS({ apiVersion: '2012-11-05' }).client

kRenderQueue = "zenphoton-hqz-render-queue"
kResultQueue = "zenphoton-hqz-results"

sqs.createQueue
    QueueName: kRenderQueue
    (error, data) ->
        return console.log util.inspect error if error
        sqs.getQueueAttributes
            QueueUrl: data.QueueUrl
            AttributeNames: [ 'ApproximateNumberOfMessages', 'ApproximateNumberOfMessagesNotVisible' ]
            (error, data) ->
                return console.log util.inspect error if error
                console.log "work-items-waiting: #{ data.Attributes.ApproximateNumberOfMessages }"
                console.log "work-items-running: #{ data.Attributes.ApproximateNumberOfMessagesNotVisible }"

sqs.createQueue
    QueueName: kResultQueue
    (error, data) ->
        return console.log util.inspect error if error
        sqs.getQueueAttributes
            QueueUrl: data.QueueUrl
            AttributeNames: [ 'ApproximateNumberOfMessages', 'ApproximateNumberOfMessagesNotVisible' ]
            (error, data) ->
                return console.log util.inspect error if error
                console.log "results-backlog: #{ data.Attributes.ApproximateNumberOfMessages }"
                console.log "results-in-flight: #{ data.Attributes.ApproximateNumberOfMessagesNotVisible }"
