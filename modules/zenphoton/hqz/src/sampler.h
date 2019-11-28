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
#include "prng.h"
#include "spectrum.h"
#include <cfloat>
#include <algorithm>


/*
 * Samplers stochastically sample a JSON value which may be a random
 * variable. 'v' may be any of the following JSON constructs:
 *
 *      1.0             A single number. Always returns this value.
 *      null            Synonymous with zero.
 *      [ 1.0, 5.0 ]    A list of exactly two numbers. Samples uniformly.
 *      others          Reserved for future definition. (Zero)
 *
 */

struct Sampler
{
    PRNG mRandom;

    typedef rapidjson::Value Value;

    struct Bounds {
        double min;
        double max;

        void sort() {
            if (min > max)
                std::swap(min, max);
        }
    };

    Sampler(uint32_t seed) {
        mRandom.seed(seed);
    }

    Sampler(const Sampler &parent)
        : mRandom(parent.mRandom) {}

    uint32_t uniform32() {
        return mRandom.uniform32();
    }

    double uniform() {
        return mRandom.uniform();
    }

    double uniform(double a, double b) {
        return mRandom.uniform(a, b);
    }

    double blackbody(double temperature) {
        return Color::blackbodyWavelength(temperature, uniform());
    }

    /**
     * Sample a JSON Value as a random variable.
     * Returns a sampled value, and updates the sampler state.
     */

    double value(const Value &v)
    {
        if (v.IsNumber()) {
            // Constant
            return v.GetDouble();
        }

        if (v.IsArray() && v.Size() == 2 && v[0u].IsNumber()) {
            // 2-tuples starting with a number

            if (v[1].IsNumber())
                return uniform(v[0u].GetDouble(), v[1].GetDouble());

            if (v[1].IsString() && v[1].GetStringLength() == 1 && v[1].GetString()[0] == 'K')
                return blackbody(v[0u].GetDouble());
        }

        // Unknown
        return 0;
    }

    /**
     * Determine the upper and lower bounds of a JSON Value that would
     * be sampled as a random variable. Does not require access to sampler state.
     *
     * This must be kept in sync with the behavior exposed by value(), in order
     * to calculate bounding boxes for objects.
     */

    static Bounds bounds(const Value &v)
    {
        Bounds result = { FLT_MIN, FLT_MAX };

        if (v.IsNumber()) {
            // Constant
            result.min = result.max = v.GetDouble();

        } else if (v.IsArray() && v.Size() == 2 && v[0u].IsNumber() && v[1].IsNumber()) {
            // Uniform
            result.min = v[0u].GetDouble();
            result.max = v[1].GetDouble();
            result.sort();
        }

        return result;
    }

};
