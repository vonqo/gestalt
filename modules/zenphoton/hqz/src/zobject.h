/*
 * This file is part of HQZ, the batch renderer for Zen Photon Garden.
 *
 * Copyright (c) 2013 Micah Elizabeth Scott <micah@scanlime.org>
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

#pragma once
#include "rapidjson/document.h"
#include "ray.h"
#include "sampler.h"


/**
 * Utility class for working with scene objects in HQZ.
 * Understands the various types of scene objects, how to do a ray test,
 # and how to compute their AABB.
 */

struct ZObject {
    typedef rapidjson::Value Value;

    static bool rayIntersect(const Value &object, IntersectionData &d, Sampler &s);
    static void getBounds(const Value &object, AABB &bounds);
};


inline bool ZObject::rayIntersect(const Value &object, IntersectionData &d, Sampler &s)
{
    /*
     * Does this ray intersect a specific object? This samples the object once,
     * filling in the required IntersectionData on success. If this ray and object
     * don't intersect at all, returns false.
     *
     * Does not write to d.object; it is assumed that the caller does this.
     */

    switch (object.Size()) {

        case 5: {
            // Line segment

            Vec2 origin = { s.value(object[1]), s.value(object[2]) };
            Vec2 delta = { s.value(object[3]), s.value(object[4]) };

            if (d.ray.intersectSegment(origin, delta, d.distance)) {
                d.point = d.ray.pointAtDistance(d.distance);
                d.normal.x = -delta.y;
                d.normal.y = delta.x;
                return true;
            }
            break;
        }

        case 7: {
            // Line segment with trigonometrically interpolated normals

            Vec2 origin = { s.value(object[1]), s.value(object[2]) };
            Vec2 delta = { s.value(object[4]), s.value(object[5]) };
            double alpha;

            if (d.ray.intersectSegment(origin, delta, d.distance, alpha)) {
                double degrees = s.value(object[3]) + alpha * s.value(object[6]);
                double radians = degrees * (M_PI / 180.0);
                d.point = d.ray.pointAtDistance(d.distance);
                d.normal.x = cos(radians);
                d.normal.y = sin(radians);
                return true;
            }
            break;
        }
    }

    return false;
}

inline void ZObject::getBounds(const Value &object, AABB &bounds)
{
    switch (object.Size()) {

        case 5: {
            // Line segment

            Sampler::Bounds x0 = Sampler::bounds(object[1]);
            Sampler::Bounds y0 = Sampler::bounds(object[2]);
            Sampler::Bounds dx = Sampler::bounds(object[3]);
            Sampler::Bounds dy = Sampler::bounds(object[4]);

            bounds.left = std::min( x0.min + dx.min, x0.min );
            bounds.right = std::max( x0.max + dx.max, x0.max );
            bounds.top = std::min( y0.min + dy.min, y0.min );
            bounds.bottom = std::max( y0.max + dy.max, y0.max );

            break;
        }

        case 7: {
            // Line segment with trigonometrically interpolated normals

            Sampler::Bounds x0 = Sampler::bounds(object[1]);
            Sampler::Bounds y0 = Sampler::bounds(object[2]);
            Sampler::Bounds dx = Sampler::bounds(object[4]);
            Sampler::Bounds dy = Sampler::bounds(object[5]);

            bounds.left = std::min( x0.min + dx.min, x0.min );
            bounds.right = std::max( x0.max + dx.max, x0.max );
            bounds.top = std::min( y0.min + dy.min, y0.min );
            bounds.bottom = std::max( y0.max + dy.max, y0.max );

            break;
        }

        default: {
            // Unsupported

            bounds.left = bounds.right = bounds.top = bounds.bottom = 0;
            break;
        }
    }
}
