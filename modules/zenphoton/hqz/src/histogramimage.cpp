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

#include <string.h>
#include "histogramimage.h"
#include "prng.h"


void HistogramImage::resize(unsigned w, unsigned h)
{
    mWidth = w;
    mHeight = h;
    mCounts.resize(w * h * kChannels);
    clear();
}

void HistogramImage::clear()
{
    memset(&mCounts[0], 0, mCounts.size() * sizeof mCounts[0]);
}

void HistogramImage::render(std::vector<unsigned char> &rgb, double scale, double exponent)
{
    // Tone mapping from 64-bit-per-channel to 8-bit-per-channel, with dithering.

    PRNG rng;
    rng.seed(0);

    unsigned i = 0;
    unsigned e = mWidth * mHeight * kChannels; 
    rgb.resize(e);

    for (; i != e; ++i) {
        double u = std::max(0.0, mCounts[i] * scale);
        double dither = rng.uniform();
        double v = 255.0 * pow(u, exponent) + dither;
        rgb[i] = std::max(0.0, std::min(255.9, v));
    }
}

void HistogramImage::line(Color c, double x0, double y0, double x1, double y1)
{
    /*
     * Modified version of Xiaolin Wu's antialiased line algorithm:
     * http://en.wikipedia.org/wiki/Xiaolin_Wu%27s_line_algorithm
     *
     * Brightness compensation:
     *   The total brightness of the line should be proportional to its
     *   length, but with Wu's algorithm it's proportional to dx.
     *   We scale the brightness of each pixel to compensate.
     */

    unsigned hx = kChannels;
    unsigned hy = kChannels * mWidth;
    double limitX = mWidth - 1.0001;
    double limitY = mHeight - 1.0001;
    {
        double dx = x1 - x0;
        double dy = y1 - y0;
        if (dx < 0.0) dx = -dx;
        if (dy < 0.0) dy = -dy;

        if (dy > dx) {
            // Axis swap. The virtual 'x' is always the major axis.
            std::swap(x0, y0);
            std::swap(x1, y1);
            std::swap(hx, hy);
            std::swap(limitX, limitY);
        }
    }

    // We expect x0->x1 to be in the +X direction

    if (x0 > x1) {
        std::swap(x0, x1);
        std::swap(y0, y1);
    }

    // Slope calculations

    double gradient, br;
    {
        double dx = x1 - x0;
        double dy = y1 - y0;
        gradient = dy / dx;
        br = 128.0 * sqrt(dx*dx + dy*dy) / dx;
    }

    // X axis clipping.
    // Note the odd comparisons: We want to return on NaN.

    if (!(x1 >= 0.0)) return;
    if (!(x0 <= limitX)) return;
    if (!(x0 >= 0.0)) {
        double delta = 0.0 - x0;
        x0 = 0.0;
        y0 += gradient * delta;
    }
    if (!(x1 <= limitX)) {
        double delta = limitX - x1;
        x1 = limitX;
        y1 += gradient * delta;
    }

    // Leave room for our second pixel in each pair on the minor axis.
    limitY -= 1.0;

    if (gradient < 0.0) {
        // Y axis clipping, negative slope

        if (!(y0 >= 0.0)) return;
        if (!(y1 <= limitY)) return;
        if (!(y1 >= 0.0)) {
            double delta = 0.0 - y1;
            y1 = 0.0;
            x1 += delta / gradient;
        }
        if (!(y0 <= limitY)) {
            double delta = limitY - y0;
            y0 = limitY;
            x0 += delta / gradient;
        }

    } else {
        // Y axis clipping, positive slope

        if (!(y1 >= 0.0)) return;
        if (!(y0 <= limitY)) return;
        if (!(y0 >= 0.0)) {
            double delta = 0.0 - y0;
            y0 = 0.0;
            x0 += delta / gradient;
        }
        if (!(y1 <= limitY)) {
            double delta = limitY - y1;
            y1 = limitY;
            x1 += delta / gradient;
        }
    }

    if (isnan(x0)) return;
    if (isnan(y0)) return;
    if (isnan(x1)) return;
    if (isnan(y1)) return;

    // First endpoint

    double x05 = x0 + 0.5;
    int xpxl1 = x05;
    double xend = xpxl1;
    double yend = y0 + gradient * (xend - x0);
    double xgap = br * (1.0 - x05 + xend);
    int ypxl1 = yend;
    double t = yend - int(yend);
    int64_t *ptr = &mCounts[ xpxl1 * hx + ypxl1 * hy ];
    c.plot(ptr, xgap * (1.0 - t));
    c.plot(ptr + hy, xgap * t);
    double intery = yend + gradient;

    // Second endpoint

    double x15 = x1 + 0.5;
    int xpxl2 = x15;
    t = xpxl2;
    yend = y1 + gradient * (t - x1);
    xgap = br * (x15 - t);
    int ypxl2 = yend;
    t = yend - int(yend);
    ptr = &mCounts[ xpxl2 * hx + ypxl2 * hy ];
    c.plot(ptr, xgap * (1.0 - t));
    c.plot(ptr + hy, xgap * t);

    // Inner loop

    ptr = &mCounts[ (xpxl1 + 1) * hx ];
    int64_t *ptrEnd = &mCounts[ xpxl2 * hx ];

    while (ptr < ptrEnd) {
        unsigned iy = intery;
        double fy = intery - iy;
        int64_t *py = ptr + iy * hy;

        c.plot(py, br * (1.0 - fy));
        c.plot(py + hy, br * fy);

        ptr += hx;
        intery += gradient;
    }
}
