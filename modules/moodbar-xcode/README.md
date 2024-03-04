# Moodbar | Gestalt - XCode port

Due to library linking difficulties on macOS, i had to port current meson build system to xcode project.

## Steps
* Download and install GStreamer-devel version from [here](https://gstreamer.freedesktop.org/data/pkg/osx/)
* Install fftw ```brew install fftw``` or ```sudo port install fftw-3 +universal``` also you might need ```glib2-devel``` ```gstreamer1-gst-plugins-base``` these packages.
* Build settings -> Search Paths -> Header Search Paths:
    * ```/Library/Frameworks/GStreamer.framework/Headers``` | non-recursive
    * ```.../fftw/{version}/include``` | non-recursive
* Build phase -> Link Binary With Libraries:
    * ```GStreamer.framework```
    * ```libfftw3.a```
* General -> Gstreamer.gramework -> Embed Without Signing
* Build & Run
