# <img height="42px" src="https://github.com/punkowl/gestalt/blob/master/public/cnc-logo.png" />

[![Shitcoded][ulaanbaatar-badge]][ub-wiki]

Definition of **gestalt**: "*something that is made of many parts and yet is somehow more than or different from the combination of its parts*"

This is an open source repository of creative coding integrations and playground. Some visual ouputs are posted on following pages.

* Facebook page: [Color Note Code](https://www.facebook.com/colornotecode/)
* Instagram: [@colornotecode](https://www.instagram.com/colornotecode/)
* YouTube: [vonqo](https://www.youtube.com/channel/UCGmwCteDtjSBGco4qqs1QIQ/)

#### Showcase
[<img src="https://img.youtube.com/vi/Z6x50-Gg3c0/maxresdefault.jpg" width="30%">](https://youtu.be/Z6x50-Gg3c0)
[<img src="https://img.youtube.com/vi/pM8h-2b6zzM/maxresdefault.jpg" width="30%">](https://youtu.be/pM8h-2b6zzM)

#### Gallery
[<img height="400px" src="https://github.com/punkowl/gestalt/blob/master/public/gallery.png" />](https://www.facebook.com/colornotecode/photos/)

## Directories
* **modules** - List of modules that written in different programming languages
    * **orchestrator** - Synthesize variety of images, audio and videos.
		* [Processing](https://github.com/processing) bindings
		* Some utility classes taken from [deeplearning4j](https://github.com/deeplearning4j)/[DataVec](https://github.com/deeplearning4j/DataVec)
    * **moodbar** - Used in orchestrator. Forked from [exaile](https://github.com/exaile)/[moodbar](https://github.com/exaile/moodbar) 
    * **hqz** - Used in orchestrator. Forked from [scanlime](https://github.com/scanlime)/[zenphoton](https://github.com/scanlime/zenphoton) 
    * **a1_restorer** - Image defect fixing, enhance greyscale

* **sketches** - Processing sketches.
* **study_materials** - Project related study material list.

## Installation
Execute ```install.sh```. Installation script is not maintained. If things gone wrong, screw this script and go manual.

### Manual installation
**Orchestrator** integrates all modules with isolated process calls. Each modules has to be successfully builded on the system. The source code of module(s) from this repository is slightly changed to fit with **Orchestrator**. 
 
Follow these instruction:
* [Moodbar installation](https://github.com/PunkOwl/gestalt/tree/master/modules/moodbar) or (For OSX) [Moodbar Xcode port](https://github.com/PunkOwl/gestalt/tree/master/modules/moodbar-xcode)
* [ZenPhoton installaton](https://github.com/PunkOwl/gestalt/tree/master/modules/zenphoton/hqz)

And install these third party tools:
* [FFmpeg](https://www.ffmpeg.org/)
* [experimental] ~~[Neural Style torch implementation](https://github.com/jcjohnson/neural-style) + [cuDNN](https://developer.nvidia.com/cudnn)~~


## Usage
1. Edit ```modules/orchestrator/config.json``` to setup your paths.
	* You can ignore ```neuralStyle``` section unless you've installed it.
2. Edit and freestyle your ```config.json```
```json
{
  "system": {
    "moodbarExecuteable": "moodbar",
    "hqzExecutable": "~/gestalt/modules/zenphoton/hqz/hqz",
    "ffmpegExecutable": "ffmpeg",
    "resourceDir": "~/Desktop/",
    "neuralStyle": {
      "executable": "~/neural-style.lua",
      "styleDir": "~/style_img/",
      "contentDir": "~/content_img/"
    }
  },
  "param": {
      "fontName": "Interstate",
      "audio": [
        {
          "exportType": "BUBBLE_BAR_2DRT",
          "audioFile": ["alive2"],
          "displayText": ["Portal - Still Alive (Portal OST)"],
          "hasBanner": true,
          "ray": 200000,
          "extraDataFile": "dafuq.json"
        },
      ],
      "videoExport": {
        "isVideoExport": false,
        "usableCore": 1,
        "startFrame": 0,
        "endFrame": 0
      }
    }
}
```
3. Build it with [Maven](https://maven.apache.org/). 
4. Run

## License
Enkh-Amar. G (vonqo). Released under the [Mozilla Public License Version 2.0](LICENSE)

[gestalt-logo]: https://github.com/lupino22/gestalt/blob/master/public/logo.png
[codacy-badge]: https://app.codacy.com/project/badge/Grade/8d438e4c49964773b4668d381c478bfc
[codacy]: https://www.codacy.com/gh/PunkOwl/gestalt/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=PunkOwl/gestalt&amp;utm_campaign=Badge_Grade
[gitter-badge]: https://badges.gitter.im/punkowl-gestalt/community.svg
[gitter]: https://gitter.im/punkowl-gestalt/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge
[ulaanbaatar-badge]: https://img.shields.io/badge/shitcoded%20in-%F0%9F%87%B2%F0%9F%87%B3ulaanbaatar-brightgreen.svg
[ub-wiki]: https://en.wikipedia.org/wiki/Ulaanbaatar
[linux-badge]: https://svgshare.com/i/Zhy.svg
[osx-badge]: https://svgshare.com/i/ZjP.svg
