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
#include "zobject.h"
#include <stdio.h>
#include <cfloat>
#include <vector>


class ZQuadtree {
public:
    typedef rapidjson::Value Value;
    typedef uint32_t Index;
    typedef std::vector<Index> IndexArray;

    void build(const Value &objects);
    bool rayIntersect(IntersectionData &d, Sampler &s);

    struct Visitor;

private:

    struct Node
    {
        // Split threshold for number of objects in one node.
        static const unsigned kSplitThreshold = 16;

        Node() : split(0) {
            children[0] = 0;
            children[1] = 0;
        }

        IndexArray objects;     // Objects that don't fully fit in either child
        double split;           // Split location
        Node *children[2];      // [ < split, >= split ]
    };

    Node mRoot;
    const Value *mObjects;

    bool rayIntersect(IntersectionData &d, Sampler &s, Visitor &v);
    void split(Visitor &v);
    double splitPosition(Visitor &v);
};


struct ZQuadtree::Visitor
{
    Node *current;
    AABB bounds;
    bool axisY;

    operator bool () const {
        return current != 0;
    }

    static Visitor root(ZQuadtree *tree)
    {
        Visitor v;
        v.current = &tree->mRoot;
        v.bounds.left = v.bounds.top = FLT_MIN;
        v.bounds.right = v.bounds.bottom = FLT_MAX;
        v.axisY = false;
        return v;
    }

    Visitor first()
    {
        Visitor v;

        v.current = current->children[0];
        v.axisY = !axisY;
        v.bounds = bounds;

        if (axisY)
            v.bounds.bottom = current->split;
        else
            v.bounds.right = current->split;

        return v;
    }

    Visitor second()
    {
        Visitor v;

        v.current = current->children[1];
        v.axisY = !axisY;
        v.bounds = bounds;

        if (axisY)
            v.bounds.top = current->split;
        else
            v.bounds.left = current->split;

        return v;
    }
};


inline void ZQuadtree::build(const Value &objects)
{
    /*
     * Start out with all items in the root node
     */

    mObjects = &objects;
    mRoot.objects.resize(objects.Size());
    for (unsigned i = 0; i < objects.Size(); ++i)
        mRoot.objects[i] = i;

    /*
     * Recursively visit and split each node
     */

    Visitor v = Visitor::root(this);
    split(v);
}

inline void ZQuadtree::split(Visitor &v)
{
    Node &node = *v.current;

    // Is this node already small enough?
    if (node.objects.size() <= Node::kSplitThreshold)
        return;

    // New children
    node.split = splitPosition(v);
    node.children[0] = new Node();
    node.children[1] = new Node();
    Visitor first = v.first();
    Visitor second = v.second();

    /*
     * Loop through the objects in this node, and move them to
     * our children if possible.
     */

    unsigned in = 0;
    unsigned out = 0;
    unsigned end = node.objects.size();

    for (; in != end; ++in) {
        Index index = node.objects[in];
        const Value &object = (*mObjects)[index];

        AABB bounds;
        ZObject::getBounds(object, bounds);

        if (first.bounds.contains(bounds)) {
            first.current->objects.push_back(index);

        } else if (second.bounds.contains(bounds)) {
            second.current->objects.push_back(index);

        } else {
            // Keep it here
            node.objects[out++] = index;
        }
    }

    if (0) {
        printf("Splitting [%f, %f, %f, %f] orig: %d self: %d first: %d second: %d\n",
            v.bounds.left, v.bounds.top, v.bounds.right, v.bounds.bottom,
            end, out, (int)first.current->objects.size(), (int)second.current->objects.size());
    }

    node.objects.resize(out);

    // Recursively split child nodes    
    split(first);
    split(second);
}

inline double ZQuadtree::splitPosition(Visitor &v)
{
    /*
     * Choose a split position for node v.current.
     * This quick-and-dirty approach uses the mean of each object's AABB.
     */

    double numerator = 0;
    int denominator = 0;

    Node &node = *v.current;
    for (IndexArray::const_iterator i = node.objects.begin(), e = node.objects.end(); i != e; ++i)
    { 
        Index index = *i;
        const Value &object = (*mObjects)[index];

        AABB bounds;
        ZObject::getBounds(object, bounds);

        denominator += 2;
        if (v.axisY)
            numerator += bounds.top + bounds.bottom;
        else
            numerator += bounds.left + bounds.right;
    }

    return numerator / denominator;
}

inline bool ZQuadtree::rayIntersect(IntersectionData &d, Sampler &s)
{
    Visitor v = Visitor::root(this);
    return rayIntersect(d, s, v);
}

inline bool ZQuadtree::rayIntersect(IntersectionData &d, Sampler &s, Visitor &v)
{
    // Swappable buffers for keeping track of the closest intersection
    IntersectionData intersections[2];
    intersections[0] = intersections[1] = d;
    intersections[0].distance = intersections[1].distance = FLT_MAX;
    IntersectionData *closest = &intersections[0];
    IntersectionData *scratch = &intersections[1];
    bool result = false;

    Visitor first = v.first();
    double firstClosest = 0;
    bool firstHit = first && d.ray.intersectAABB(first.bounds, firstClosest);

    Visitor second = v.second();
    double secondClosest = 0;
    bool secondHit = second && d.ray.intersectAABB(second.bounds, secondClosest);

    // Try local objects. These could be leaves in the tree, or larger objects that
    // don't fully fit inside a subtree's AABB.

    Node &node = *v.current;
    for (IndexArray::const_iterator i = node.objects.begin(), e = node.objects.end(); i != e; ++i)
    { 
        Index index = *i;
        const Value &object = (*mObjects)[index];

        if (d.object == &object)
            continue;

        /*
         * Create a nested per-object sampler which allows us to test objects
         * in an arbitrary order without affecting the stream of values produced by
         * the parent sampler. The sampler must be perturbed in a way specific to
         * each object, however, since it's important not to allow correlation in
         * the random values used by different objects.
         */

        Sampler tempSampler = s;
        tempSampler.mRandom.remix(index);

        if (ZObject::rayIntersect(object, *scratch, tempSampler) && scratch->distance < closest->distance) {
            std::swap(closest, scratch);
            closest->object = &object;
            result = true;
        }
    }

    // Try the closest child first. Maybe we can skip testing the other side.
    if (firstClosest < secondClosest) {

        if (firstHit && firstClosest < closest->distance &&
            rayIntersect(*scratch, s, first) && scratch->distance < closest->distance) {
            std::swap(closest, scratch);
            result = true;
        }

        if (secondHit && secondClosest < closest->distance &&
            rayIntersect(*scratch, s, second) && scratch->distance < closest->distance) {
            std::swap(closest, scratch);
            result = true;
        }

    } else {

        if (secondHit && secondClosest < closest->distance &&
            rayIntersect(*scratch, s, second) && scratch->distance < closest->distance) {
            std::swap(closest, scratch);
            result = true;
        }

        if (firstHit && firstClosest < closest->distance &&
            rayIntersect(*scratch, s, first) && scratch->distance < closest->distance) {
            std::swap(closest, scratch);
            result = true;
        }
    }

    if (result) {
        d = *closest;
    }
    return result;
}
