#!/usr/bin/env coffee
#
# Animation script for HQZ.
# Micah Elizabeth Scott <micah@scanlime.org>
# Creative Commons BY-SA license:
# http://creativecommons.org/licenses/by-sa/3.0/
#

plot = require './plot'
arc4rand = require 'arc4rand'
TAU = Math.PI * 2
toRadians = (degrees) -> degrees * (Math.PI / 180.0)

harmonicOscillator = (frame, opts) ->
    rnd = (new arc4rand(opts.seed)).random

    # Calculate series coefficients once per frame
    series = for i in [1 .. 20]
        freq: i
        amplitude: rnd(0, 1.0) / i
        phase: rnd(0, TAU) + rnd(-0.01, 0.01) * frame

    plot opts, (t) ->
        # Draw a truncated Fourier series, in polar coordinates
        t *= TAU
        r = 0
        r += s.amplitude * Math.sin(s.freq * (t + s.phase)) for s in series
        r = opts.radius + r * opts.radius * opts.modulationDepth
        [ opts.x0 + r * Math.cos(t), opts.y0 + r * Math.sin(t) ]

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

    # Glowing oscillator also moves, eventually intersecting the other.
    glowy =
        x: 1920*0.8
        y: 1080/3
        speed: 0.9
        angle: toRadians 160

    glowy.x += glowy.speed * frame * Math.cos glowy.angle
    glowy.y += glowy.speed * frame * Math.sin glowy.angle

    resolution: [1920, 1080]
    timelimit: 60 * 60
    rays: 5000000
    seed: frame * 100000

    exposure: 0.7
    gamma: 1.8
    viewport: zoomViewport 1920, 1080, 1920/2, 1080/2, frame * 0.001

    lights: [
        [0.75, 1920/2, 1080/2, [0, -180], 1200, [0, 360], 480]
        [0.2, 1920/2, 1080/2, [0, -180], 1200, [0, 360], 0]
        [0.1, glowy.x, glowy.y, [0, 360], [0, 15], [0, 360], 590]
    ]

    objects: [].concat [],

        harmonicOscillator frame,
            material: 0
            seed: 's'
            x0: 1920*0.4
            y0: 1080*2/3
            radius: 500
            modulationDepth: 0.25

        harmonicOscillator frame,
            material: 1    
            seed: 't'
            x0: glowy.x
            y0: glowy.y
            radius: 120
            modulationDepth: 0.25

    materials: [
        # 0. Light catching. Lots of internal reflection.
        [ [0.1, "d"], [0.9, "r"] ]

        # 1. Light emitting. Diffuse the light, reflect only a little.
        [ [0.99, "d"], [0.01, "r"] ]
    ]

console.log JSON.stringify scene i for i in [0 .. 800]
#console.log JSON.stringify scene 0
