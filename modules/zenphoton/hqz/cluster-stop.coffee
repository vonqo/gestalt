#!/usr/bin/env coffee
#
#   Stop a cluster previously started with cluster-start.
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

ec2 = new AWS.EC2().client
log = (msg) -> console.log "[#{ (new Date).toJSON() }] #{msg}"

ec2.describeSpotInstanceRequests
    Filters: [
        { Name: 'tag:com.zenphoton.hqz', Values: [ 'node' ] }
    ]
    (error, data) ->
        toCancel = []
        toTerminate = []

        return log util.inspect error if error
        for s in data.SpotInstanceRequests
            if s.State != "cancelled"
                toTerminate.push s.InstanceId if s.InstanceId
                toCancel.push s.SpotInstanceRequestId

        if toCancel.length
            ec2.cancelSpotInstanceRequests
                SpotInstanceRequestIds: toCancel
                (error, data) ->
                    return log util.inspect error if error
                    log "Requests cancelled: " + util.inspect(toCancel)

        if toTerminate.length
            ec2.terminateInstances
                InstanceIds: toTerminate
                (error, data) ->
                    return log util.inspect error if error
                    log "Instances terminated: " + util.inspect(toTerminate)
