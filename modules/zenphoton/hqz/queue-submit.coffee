#!/usr/bin/env coffee
#
#   Job Submitter. Uploads the JSON scene description for a job
#   and enqueues a work item for each frame.
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
#      npm install aws-sdk coffee-script async clarinet
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
util = require 'util'
fs = require 'fs'
crypto = require 'crypto'
zlib = require 'zlib'
path = require 'path'

sqs = new AWS.SQS({ apiVersion: '2012-11-05' }).client
s3 = new AWS.S3({ apiVersion: '2006-03-01' }).client

kRenderQueue = "zenphoton-hqz-render-queue"
kResultQueue = "zenphoton-hqz-results"
kBucketName = process.env.HQZ_BUCKET
kChunkSizeLimit = 8 * 1024 * 1024
kConcurrentUploads = 6
kIndent = "    "

pad = (str, length) ->
    str = '' + str
    str = '0' + str while str.length < length
    return str

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

if process.argv.length != 3
    console.log "usage: queue-submit JOBNAME.json"
    process.exit 1

filename = process.argv[2]
console.log "Reading #{filename}..."

async.waterfall [

    # Parallel initialization tasks
    (cb) ->
        async.parallel

            renderQueue: (cb) ->
                sqs.createQueue
                    QueueName: kRenderQueue
                    cb

            resultQueue: (cb) ->
                sqs.createQueue
                    QueueName: kResultQueue
                    cb

            # Truncated sha1 hash of input file
            hash: (cb) ->
                hash = crypto.createHash 'sha1'
                s = fs.createReadStream filename
                s.on 'data', (chunk) -> hash.update chunk
                s.on 'end', () ->
                    h = hash.digest 'hex'
                    console.log "    sha1 #{h}"
                    cb null, h.slice(0, 8)

            # Info about frames: Total number of frames, and a list of file
            # offsets used for reading groups of frames later.
            frames: (cb) ->
                frameOffsets = [ 0 ]
                offset = 0
                tail = true
                s = fs.createReadStream filename

                s.on 'data', (d) ->
                    parts = d.toString().split('\n')

                    # If we found any newlines, record the index of the character
                    # following the newline. This is the end of the previous frame,
                    # or the beginning of the next.
                    for i in [0 .. parts.length - 2] by 1
                        offset += parts[i].length + 1
                        frameOffsets.push offset

                    last = parts[parts.length - 1]
                    offset += last.length
                    tail = (last == '')

                s.on 'end', (e) ->
                    frames = frameOffsets.length
                    frames-- if tail

                    if frames.length == 1
                        console.log "#{kIndent}found a single frame"
                    else
                        console.log "#{kIndent}found animation with #{frames} frames"

                    # frameOffsets always has a beginning and end for each frame.
                    frameOffsets.push offset

                    cb null,
                        count: frames
                        offsets: frameOffsets
            cb

    (obj, cb) ->
        # Create a unique identifier for this job
        jobName = path.basename filename, '.json'
        obj.key = jobName + '/' + obj.hash

        # Examine the frames we have to render. Generate a list of work items
        # and split our scene up into one or more chunks. Each chunk will have a whole
        # number of frames in it, and be of a bounded size.

        obj.work = []
        obj.chunks = []

        for i in [0 .. obj.frames.count - 1]

            # Make a new chunk if necessary
            chunk = obj.chunks[ obj.chunks.length - 1 ]
            if !chunk or chunk.dataSize > kChunkSizeLimit
                chunk =
                    dataSize: 0
                    name: obj.key + '-' + pad(obj.chunks.length, 4) + '.json.gz'
                    firstFrame: i
                    lastFrame: i
                obj.chunks.push chunk

            # Add this frame to a chunk
            chunk.lastFrame = i
            chunk.dataSize += obj.frames.offsets[i + 1] - obj.frames.offsets[i]

            # Work item
            obj.work.push
                Id: 'item-' + i
                MessageBody: JSON.stringify

                    # Metadata for queue-watcher
                    JobKey: obj.key
                    JobIndex: i

                    # queue-runner parameters
                    SceneBucket: kBucketName
                    SceneKey: chunk.name
                    SceneIndex: i - chunk.firstFrame
                    OutputBucket: kBucketName
                    OutputKey: obj.key + '-' + pad(i, 4) + '.png'
                    OutputQueueUrl: obj.resultQueue.QueueUrl

        # Compress and upload all chunks
        uploadCounter = 0

        uploadChunks = (chunk, cb) ->
            # Is this chunk already on S3?
            s3.headObject
                Bucket: kBucketName
                Key: chunk.name
                (error, data) ->
                    return cb error if error and error.code != 'NotFound'

                    # Increment the counter right before use, so counts appear in-order in the log
                    logPrefix = () ->
                        uploadCounter++
                        "#{kIndent}[#{uploadCounter} / #{obj.chunks.length}] chunk #{chunk.name}"

                    if not error
                        console.log "#{logPrefix()} already uploaded"
                        return cb()

                    # Read and gzip just this section of the file
                    s = fs.createReadStream filename,
                        start: obj.frames.offsets[ chunk.firstFrame ]
                        end: obj.frames.offsets[ chunk.lastFrame + 1 ] - 1

                    # Store the chunks in an in-memory buffer
                    gz = zlib.createGzip()
                    chunks = []
                    s.pipe gz
                    gz.on 'data', (chunk) -> chunks.push chunk
                    gz.on 'end', () ->
                        data = bufferConcat chunks
                        s3.putObject
                            Bucket: kBucketName
                            ContentType: 'application/json'
                            Key: chunk.name
                            Body: data
                            (error) ->
                                console.log "#{logPrefix()} uploaded #{data.length} bytes" if !error
                                cb error

        if obj.chunks.length > 1
            console.log "Uploading scene data in #{obj.chunks.length} chunks..."
        else
            console.log "Uploading scene data..."

        async.eachLimit obj.chunks, kConcurrentUploads, uploadChunks, (error) ->
            return cb error if error
            cb null, obj

    # Enqueue work items
    (obj, cb) ->
        async.whilst(
            () -> obj.work.length > 0
            (cb) ->
                console.log "Enqueueing work items, #{obj.work.length} remaining"
                batch = Math.min(10, obj.work.length)
                thisBatch = obj.work.slice(0, batch)
                obj.work = obj.work.slice(batch)
                sqs.sendMessageBatch
                    QueueUrl: obj.renderQueue.QueueUrl
                    Entries: thisBatch
                    cb
            cb
        )

], (error) -> 
    return console.log util.inspect error if error
    console.log "Job submitted"
