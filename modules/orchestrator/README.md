# Gestalt - Orchestrator

## Usage

Export types:
* VANILLA
* COLLECTION
* BUBBLE_BAR
* BUBBLE_BAR_2DRT
* WHIRLWIND_2DRT
* DRAWING_2DRT

if **File of unsupported format** is thrown, following simple touch will fix codec error:
```ffmpeg -i audio.mp3 -acodec libmp3lame audio.mp3```

#### Codebase
* Audio dsp and spectogram has cloned from deeplearning4j/DataVec

