BINS := hqz

HQZ_OBJS := \
	src/zrender.o \
	src/histogramimage.o \
	src/spectrum.o \
	src/main.o \
	src/lodepng.o

TMP_FILES := examples/benchmark.json examples/benchmark.png

CDEPS := src/*.h

CCFLAGS := -Isrc
LIBS := -lstdc++ -lm

CCFLAGS += -Wall -g
CCFLAGS += -O3 -march=native -ffast-math -fno-exceptions -fomit-frame-pointer -funroll-loops

all: $(BINS)

hqz: $(HQZ_OBJS)
	$(CC) -o $@ $(HQZ_OBJS) $(LIBS)

%.o: %.cpp $(CDEPS)
	$(CC) -c -o $@ $< $(CCFLAGS)

# Simple benchmarking target
time: hqz examples/benchmark.json
	time ./hqz examples/benchmark.json examples/benchmark.png
	open examples/benchmark.png

examples/%.json: examples/%.coffee
	coffee $< > $@

.PHONY: clean time

clean:
	rm -f $(BINS) $(HQZ_OBJS) $(TMP_FILES)
