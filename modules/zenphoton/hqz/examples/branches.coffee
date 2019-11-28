#!/usr/bin/env coffee
#
# Animation script for HQZ.
# Micah Elizabeth Scott <micah@scanlime.org>
# Creative Commons BY-SA license:
# http://creativecommons.org/licenses/by-sa/3.0/
#

RAYS = 10000000

arc4rand = require 'arc4rand'

TAU = Math.PI * 2
deg = (degrees) -> degrees * TAU / 360

childBranch = (frame, seed, x0, y0, dx, dy, offset, angle, len) ->
    cx = len * Math.cos angle
    cy = len * Math.sin angle
    drawBranches frame, seed, x0 + dx * offset, y0 + dy * offset, cx, cy

drawBranches = (frame, seed, x0, y0, dx, dy) ->
    rnd = (new arc4rand(seed)).random
    len = Math.sqrt(dx * dx + dy * dy)
    angle = Math.atan2(dy, dx)

    # Detail limit
    return [] if len < 12

    # Gently waving trees
    angle += 0.02 * Math.sin(frame * TAU / 100.0)

    # Different but stable random characteristics for each branch
    return [
        [0, x0, y0, dx, dy]
    ].concat(
        childBranch frame, seed+'a', x0,y0,dx,dy, rnd(0.5, 0.8), angle + deg(rnd(-10,10) - 30), len * rnd(0.6, 0.7)
        childBranch frame, seed+'b', x0,y0,dx,dy, rnd(0.5, 0.8), angle + deg(rnd(-10,10) + 30), len * rnd(0.6, 0.7)
    )

floor = [0, -100, 900, 2200, -50]

floorY = (floorX) ->
    floor[4] / floor[3] * (floorX - floor[1]) + floor[2]

drawTree = (frame, seed, x, dx, dy) ->
    drawBranches frame, seed, x, floorY(x), dx, dy

monoLight = (frame) ->
    [ 0.08, [800, 2000], -100, 0, 0, [90, 180], 0 ]

sunlight = (frame) ->
    x  = 2000 - frame * 0.8
    [ 0.2, x, -20, 0, 0, [90, 180], [5000, 'K'] ]

skylight = (frame) ->
    angle = 90
    spread = 20
    x0 = -100
    x1 = 1920 + 100
    [ 1, [x0, x1], -30, 0, 0, [angle - spread, angle + spread], [8000, 'K'] ]

zoomViewport = (width, height, focusX, focusY, zoom) ->
    left = focusX
    right = width - focusX
    top = focusY
    bottom = height - focusY
    scale = 1.0 - zoom

    left = focusX - left * scale
    right = focusX + right * scale
    top = focusY - top * scale
    bottom = focusY + bottom * scale

    [ left, top, right - left, bottom - top ]

scene = (frame) ->
    resolution: [1920, 1080]
    rays: RAYS
    exposure: 0.65

    viewport: zoomViewport 1920, 1080, 324, 178, frame * 0.0003
    seed: frame * RAYS / 50

    lights: [
        monoLight frame
        sunlight frame
        skylight frame
    ]

    materials: [
        [ [0.9, "d"], [0.1, "r"] ]                  # 0. Floor
        [ [0.2, "t"], [0.4, "d"], [0.1, "r"] ]      # 1. Branches
    ]

    objects: [
        floor
    ].concat(
        drawTree frame, '1', 500, -30, -300
        drawTree frame, '2', 900, 5, -100 
        drawTree frame, '3', 1300, 12, -180 
    )

console.log JSON.stringify scene i for i in [0 .. 255]
