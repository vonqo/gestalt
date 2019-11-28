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
#include "histogramimage.h"
#include "ray.h"
#include "sampler.h"
#include "zquadtree.h"
#include <sstream>
#include <vector>


class ZRender {
public:
    typedef rapidjson::Value Value;

    ZRender(const Value &scene);

    void render(std::vector<unsigned char> &pixels);
    void interrupt();

    const char *errorText() const { return mError.str().c_str(); }
    bool hasError() const { return !mError.str().empty(); }
    unsigned width() const { return mImage.width(); }
    unsigned height() const { return mImage.height(); }

private:
    static const uint32_t kDebugQuadtree = 1 << 0;

    HistogramImage mImage;
    ZQuadtree mQuadtree;

    const Value& mScene;
    const Value& mViewport;
    const Value& mLights;
    const Value& mObjects;
    const Value& mMaterials;

    uint32_t mSeed;
    double mLightPower;
    uint32_t mDebug;
    double mRayLimit;
    double mTimeLimit;

    std::ostringstream mError;

    struct ViewportSample {
        Vec2 origin;
        Vec2 size;

        double xScale(double x, double width) {
            return (x - origin.x) * width / size.x;
        }

        double yScale(double y, double height) {
            return (y - origin.y) * height / size.y;
        }            
    };

    // Data model
    bool checkTuple(const Value &v, const char *noun, unsigned expected);
    int checkInteger(const Value &v, const char *noun);
    double checkNumber(const Value &v, const char *noun);
    bool checkMaterialID(const Value &v);
    bool checkMaterialValue(int index);

    // Raytracer entry point
    void traceRay(Sampler &s);
    void traceRayBatch(uint32_t seed, uint32_t count);
    uint64_t traceRays();

    // Light sampling
    const Value &chooseLight(Sampler &s);
    bool initRay(Sampler &s, Ray &r, const Value &light);
    void initViewport(Sampler &s, ViewportSample &v);

    // Material sampling
    bool rayMaterial(IntersectionData &d, Sampler &s);

    // Object sampling
    bool rayIntersect(IntersectionData &d, Sampler &s, const ViewportSample &v);
    void rayIntersectBounds(IntersectionData &d, const ViewportSample &v);

    // Debugging
    void renderDebugQuadtree(ZQuadtree::Visitor &v);
};
