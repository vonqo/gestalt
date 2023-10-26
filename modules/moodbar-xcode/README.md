# Moodbar | Gestalt - XCode port

Due to library linking difficulties on macOS, i had to port current meson build system to xcode project.

## Steps
* Download and install GStreamer-devel version above 1.21 from [here](https://gstreamer.freedesktop.org/data/pkg/osx/)
* Install fftw ```sudo port install fftw-3 +universal```
* Build settings -> Search Paths -> Header Search Paths:
    * ```/Library/Frameworks/GStreamer.framework/Versions/1.0/Headers``` | non-recursive
    * ```/opt/local/include``` | non-recursive
* Build settings -> Search Paths -> Library Search Paths:
   * ```/opt/local/lib``` | non-recursive
   * ```/Library/Frameworks/GStreamer.framework/Versions/1.0``` | recursive
* Build phase -> Link Binary With Libraries:
    * ```GStreamer.framework```
    * ```libfftw3.a```
* Build & Run

