/*
 * plot.js -- A generic function plotter for HQZ. This module defines
 *            a function which, given a function describing a 2D curve,
 *            returns a list of HQZ objects describing that curve.
 *
 * Optional arguments:
 *
 *   material:     Material index. Defaults to zero.
 *   resolution:   Distance between vertices, in scene units.
 *   step:         Step size for numerical differentiation, default to 1e-6.
 *
 * The provided 'func' takes one argument, a float between 0 and 1.
 * It returns a 2-element [x, y] array.
 *
 * The function should be differentiable. This algorithm is not
 * designed for curves with instantaneous sharp corners.
 *
 * Micah Elizabeth Scott <micah@scanlime.org>
 *
 * Creative Commons BY-SA license:
 * http://creativecommons.org/licenses/by-sa/3.0/
 */

module.exports = function (options, func)
{
    var results = [];
    var material = options.material || 0;
    var resolution = options.resolution || 4.0;
    var step = options.step || 1e-6;
    var t = 0.0;
    var flag = false;
    var xp, yp, ap;

    while (1) {

        // Sample the function at two points
        var xy0 = func(t);
        var xy1 = func(t + step);

        // Numerical derivative
        var dx = (xy1[0]- xy0[0]) / step;
        var dy = (xy1[1] - xy0[1]) / step;

        // Length and angle of normal vector
        // (Convert to degrees, since the hqz scene format is in degrees.)
        var dl = Math.sqrt(dx*dx + dy*dy);
        var da = Math.atan2(dx, -dy) * (180.0 / Math.PI);

        if (flag) {
            // We have at least two points. Add a segment.

            // Compute the shortest path on a circle from 'ap' to 'da'.
            // The resulting delta is always in [-180, 180], but 'da' is unbounded.
            var d = (da - ap) % 360;
            if (d > 180) d -= 360;
            else if (d < -180) d += 360;
            da = ap + d;

            // Store a scene object: line segment with interpolated normal angle
            results.push([ material, xp, yp, ap, xy0[0]-xp, xy0[1]-yp, d ]);
        }

        // Save "previous" values for the next point
        xp = xy0[0];
        yp = xy0[1];
        ap = da;
        flag = true;

        // Estimate how far to advance 't' by using the derivative to find a step
        // that should approximate the requested resolution in scene units.
        var adv = resolution / dl;

        // Did we sample the function at 1?
        if (t >= 1.0)
            return results;

        // Limits on how quickly or slowly we can advance. Clamp to 1.0
        t = Math.min(1.0, t + Math.min(0.1, Math.max(0.000001, adv)));

        // If we hit a NaN, we could loop forever. Throw an exception
        if (t != t)
            throw "Numerical error in plot(), did your function return NaN?";
    }
}
