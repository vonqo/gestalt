#!/usr/bin/env coffee
#
# This script converts a zenphoton.com URL into a JSON scene
# description compatible with hqz.
#
# Micah Elizabeth Scott <micah@scanlime.org>
# This file is released into the public domain.
#


findOrAppend = (list, item) ->
    # Find an item in the list or append if it isn't already there.
    # Returns a zero-based index. Uses a loose equivalency test;
    # if two objects have the same JSON represenatation, they're equal to us.

    itemStr = JSON.stringify item

    for id in [0 .. list.length - 1]
        if itemStr == JSON.stringify list[id]
            return id

    id = list.length
    list.push item
    return id


module.exports =

    parseZenBlob: (blob) ->
        # Parse a binary blob from zenphoton.com.
        # Switch to a decoder for this specific format version, encoded in
        # the first byte.

        try
            return switch blob.readUInt8 0
                when 0x00 then @parseZenBlobV0 blob
        catch e
            # Bad format
            return null if e.name == 'AssertionError'
            throw e

    parseZenBlobV0: (blob) ->
        # Parse a binary blob for zenphoton.com format version 0

        # Fixed header
        width = blob.readInt16BE 1
        height = blob.readInt16BE 3
        lightX = blob.readInt16BE 5
        lightY = blob.readInt16BE 7
        exposure = blob.readUInt8 9
        numSegments = blob.readUInt16BE 10

        # One light, monochromatic white
        light = [ 1, lightX, lightY, 0, 0, [0, 360], 0 ]

        # Fixed-size portions of the scene
        scene =
            resolution: [ width, height ]
            viewport: [ 0, 0, width, height ]
            exposure: exposure / 255.0
            lights: [ light ]
            objects: []
            materials: []

        # Iterate over segments
        o = 12
        for i in [0 .. numSegments-1] by 1
            x0 = blob.readInt16BE o+0
            y0 = blob.readInt16BE o+2
            x1 = blob.readInt16BE o+4
            y1 = blob.readInt16BE o+6
            diffuse = blob.readUInt8 o+8
            reflective = blob.readUInt8 o+9
            transmissive = blob.readUInt8 o+10
            o += 11

            # Build a material
            mat = []
            mat.push [ diffuse / 255.0, 'd' ] if diffuse > 0
            mat.push [ reflective / 255.0, 'r' ] if reflective > 0
            mat.push [ transmissive / 255.0, 't' ] if transmissive > 0
            matID = findOrAppend scene.materials, mat

            # Build an object
            scene.objects.push [ matID, x0, y0, x1-x0, y1-y0 ]

        return scene

    parseURL: (url) ->
        # Parse an entire zenphoton.com URL. If other web sites were compatible
        # with this rendering system in the future, this would handle their URLs too.

        url = url.trim()
        zenPrefix = "http://zenphoton.com/#"
        if url.toLowerCase().slice(0, zenPrefix.length) == zenPrefix
            return @parseZenBlob new Buffer url.slice(zenPrefix.length), 'base64'


main = (argv) ->
    if argv.length != 3
        process.stderr.write "usage: zen2json http://zenphoton.com/#...\n"
        return 1

    result = module.exports.parseURL argv[2]
    if not result
        process.stderr.write "Error parsing this URL. Are you sure it's complete?\n"
        return 1

    process.stdout.write JSON.stringify result
    process.stdout.write "\n"


# This file is usable as a command line tool or a node.js module
process.exit main process.argv if require.main is module
