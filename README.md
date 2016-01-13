# MBARI's Video Annotation and Reference System (VARS)

# General Info

Documentation for VARS is at [https://hohonuuli.github.io/vars/](https://hohonuuli.github.io/vars/)

Pre-built binaries can be downloaded from [Bintray](https://bintray.com/hohonuuli/generic/VARS/view). There are 2 versions:
1. __vcr__ - This version of VARS is configured to work with video decks that support RS422 (serial port) and connect to video capture cards such as [BlackMagic Decklink](https://www.blackmagicdesign.com/products/decklink).
2. __video__ - This version of VARS is for working directly with videofiles. (Currently MP4/H264 but other codecs such as ProRes are likely to be supported in 2016)

## Building

For the impatient:

1. Checkout VARS
  `git clone https://github.com/hohonuuli/vars.git`
2. Build
  `mvn install -P dev`
  
More details can be found in [BUILD.md](BUILD.md)

## Workflow

VARS uses a [Gitflow Workflow](https://www.atlassian.com/git/workflows#!workflow-gitflow). The _master_ branch is for releases. There is a _videofile-jdk8_ branch for videofile specific releases. Most development should be done on the _develop_ branch. For example:

```
git clone https://github.com/hohonuuli/vars.git
git checkout -b develop origin/develop

```

