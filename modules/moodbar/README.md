# Gestalt - Moodbar

Forked from [exaile/moodbar](https://github.com/exaile/moodbar)
Made little update for more readable code color:

```c
for(unsigned int i = 0; i < data.size(); i++){
    if(i%3 == 0) std::cout << std::endl;
    std::cout << unsigned(data[i]) << " ";
}
```


## Requirements

* FFTW 3
* GStreamer 1
  * GStreamer Base Plugins

For building:

* Development files for the above
* C++ compiler
* Meson. If your OS doesn't have a package for this, you can install it through pip (requires Python 3).
* pkgconf or pkg-config
* Ninja

At runtime you may also need other GStreamer plugin packages to read your audio files.
For example, to process MP3 files you may have to install GStreamer Ugly Plugins.


## Building & installing

```sh
meson --buildtype=release build/
cd build/
ninja
sudo ninja install
```

You can add `-Db_lto=true` to the `meson` call to produce slightly more efficient code.


## Usage

```sh
moodbar -o OUTPUT INPUT
```

This creates an output file containing pixel values in `R1 G1 B1 R2 G2 B2 ...` format.


## Testing

You can run `meson test -v` inside the build directory to run some unit tests.
The tests require the following:

* Python 3
* NumPy
* `gst-launch-1.0`. If this is named differently in your system or is not in PATH, point the GST_LAUNCH environment variable to it.


