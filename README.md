# [MBARI's](http://www.mbari.org/) Video Annotation and Reference System (VARS)

The Video Annotation and Reference System (VARS) is a suite of tools developed by the [Monterey Bay Aquarium Research Institute](http://www.mbari.org/) for describing, cataloging, retrieving, and viewing the visual, descriptive, and quantitative data associated with video.

Originally designed for annotating underwater video, VARS can be applied to any video dataset that requires constrained, searchable annotations.

# General Info

Documentation for VARS is at [https://hohonuuli.github.io/vars/](https://hohonuuli.github.io/vars/)

Pre-built binaries can be downloaded from [Bintray](https://bintray.com/hohonuuli/generic/VARS/view). There are 2 versions:

1. __vcr__ - This version of VARS is configured to work with video decks that support RS422 (serial port) and connect to video capture cards such as [BlackMagic Decklink](https://www.blackmagicdesign.com/products/decklink). Video capture is currently only supported on Mac OS X.
2. __video__ - This version of VARS is for working directly with videofiles. (Currently MP4/H264 but other codecs such as ProRes are likely to be supported in 2016). Theoretically, this version work on Windows, Mac, and Linux.

## Building

For the impatient:

1. Checkout VARS
  `git clone https://github.com/hohonuuli/vars.git`
2. Build
  `mvn install -P dev`
  
More details can be found in [BUILD.md](https://github.com/hohonuuli/vars/blob/master/BUILD.md)

## Workflow

VARS uses a [Gitflow Workflow](https://www.atlassian.com/git/workflows#!workflow-gitflow). The _master_ branch is for releases. There is a _videofile-jdk8_ branch for videofile specific releases. Most development should be done on the _develop_ branch. For example:

```
git clone https://github.com/hohonuuli/vars.git
git checkout -b develop origin/develop

```

