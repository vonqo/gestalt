# <img height="42px" src="https://github.com/vonqo/gestalt/blob/master/public/gestalt-logo.png" /> & <img height="42px" src="https://github.com/vonqo/gestalt/blob/master/public/cnc-logo.png" />


[![Codacy Badge][codacy-badge]][codacy]
[![Gitter][gitter-badge]][gitter]
[![HitCount][hit-badge]][hit]

Definition of **gestalt**: "*something that is made of many parts and yet is somehow more than or different from the combination of its parts*"

This is an open source repository of creative coding integrations and playground. Some visual ouputs are posted on following pages.

* Facebook page: [Color Note and Code](https://www.facebook.com/colornotecode/)
* Instagram: [@colornotecode](https://www.instagram.com/colornotecode/)

## Directories
* **modules** - List of modules that written in different programming languages
    * **orchestrator** - Synthesize variety of images, audio and videos.
		* [Processing](https://github.com/processing) bindings
		* Some utility classes taken from [deeplearning4j](https://github.com/deeplearning4j)/[DataVec](https://github.com/deeplearning4j/DataVec)
    * **moodbar** - Used in orchestrator. Forked from [exaile](https://github.com/exaile)/[moodbar](https://github.com/exaile/moodbar) 
    * **hqz** - Used in orchestrator. Forked from [scanlime](https://github.com/scanlime)/[zenphoton](https://github.com/scanlime/zenphoton) 
    * **a1_restorer** - Image defect fixing, enhance greyscale

* **sketches** - Processing sketches
* **study_materials** - Project related study materials.

## Install and use
Execute ```install.sh```. Installation script is not maintained. If things gone wrong, screw this script and go for manual.

### Manual installation
**Orchestrator** integrates all modules with isolated process calls. Each modules has to be successfully builded on the system. The source code of module(s) from this repository is slightly changed to fit with **Orchestrator**. 
 
Follow these instruction:
* [Moodbar installation](https://github.com/PunkOwl/gestalt/tree/master/modules/moodbar) or (For OSX) [Moodbar Xcode port](https://github.com/PunkOwl/gestalt/tree/master/modules/moodbar-xcode)
* [ZenPhoton installaton](https://github.com/PunkOwl/gestalt/tree/master/modules/zenphoton/hqz)

And install these third party tools:
* [FFmpeg](https://www.ffmpeg.org/)
* [experimental] ~~[Neural Style torch implementation](https://github.com/jcjohnson/neural-style) + [cuDNN](https://developer.nvidia.com/cudnn)~~


### How to use
1. Edit ```modules/orchestrator/config.json``` to setup your paths.
	* You can ignore ```neuralStyle``` section unless you've installed it.
2. Edit and freestyle your ```src/.../Orchestrator.java```
```java
@LoadOrchestrator
public static void main(String args[]) {
	Config.loadConfig();

        renderCollection();
        renderZenphoton();
        renderZenphotonFrames();
        renderVanillaMoodbars();
}
```
3. Build it with [Maven](https://maven.apache.org/). 
4. Run


## Copyright & License
Copyright (c) 2019 Enkh-Amar.G - Released under the [Mozilla Public License Version 2.0](LICENSE)

[gestalt-logo]: https://github.com/lupino22/gestalt/blob/master/public/logo.png
[codacy-badge]: https://api.codacy.com/project/badge/Grade/5085d2cd13a245a0af21f85f48ae23a9
[codacy]: https://www.codacy.com/app/lupino22/gestalt?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=lupino22/gestalt&amp;utm_campaign=Badge_Grade
[hit-badge]: http://hits.dwyl.io/vonqo/gestalt.svg
[hit]: http://hits.dwyl.io/vonqo/gestalt
[gitter-badge]: https://badges.gitter.im/punkowl-gestalt/community.svg
[gitter]: https://gitter.im/punkowl-gestalt/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge
