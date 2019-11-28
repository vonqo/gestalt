/*
 * A tiny, fast, and predictable public domain PRNG.
 *
 * A C++ adaptation of:
 *   http://burtleburtle.net/bob/rand/smallprng.html
 */

#pragma once
#include <stdint.h>

class PRNG {
private:
    uint32_t rng0, rng1, rng2, rng3;

public:
    /**
     * Thoroughly but relatively slowly reinitialize the PRNG state
     * based on a provided 32-bit value. This runs the algorithm for
     * enough rounds to ensure good mixing.
     */
    void seed(uint32_t s)
    {
        rng0 = 0xf1ea5eed;
        rng1 = rng2 = rng3 = s;
        for (unsigned i = 0; i < 20; ++i)
            uniform32();
    }

    /**
     * This quickly mixes additional entropy into  the PRNG with only
     * partially re-seeding it. Don't use it for something very important.
     */
    void remix(uint32_t v)
    {
        rng3 ^= v;
        rng2 <<= 25;
        rng1 >>= 11;
        rng0 |= v;
        for (unsigned i = 0; i< 3; ++i)
            uniform32();
    }

    uint32_t __attribute__((always_inline)) uniform32()
    {
        uint32_t rng4 = (rng0 - ((rng1 << 27) | (rng1 >> 5)));
        rng0 = rng1 ^ ((rng2 << 17) | (rng2 >> 15));
        rng1 = rng2 + rng3;
        rng2 = rng3 + rng4;
        rng3 = rng4 + rng0;
        return rng3;
    }

    double __attribute__((always_inline)) uniform()
    {
        return uniform32() * 2.3283064365386963e-10;
    }

    double __attribute__((always_inline)) uniform(double a, double b)
    {
        return a + uniform() * (b - a);
    }
};
