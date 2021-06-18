# [MBARI's](http://www.mbari.org/) Video Annotation and Reference System (VARS)

The Video Annotation and Reference System (VARS) is a suite of tools developed by the [Monterey Bay Aquarium Research Institute](http://www.mbari.org/) for describing, cataloging, retrieving, and viewing the visual, descriptive, and quantitative data associated with video.

Originally designed for annotating underwater video, VARS can be applied to any video dataset that requires constrained, searchable annotations.

# General Info

Documentation for VARS is at [https://hohonuuli.github.io/vars/](https://hohonuuli.github.io/vars/)

Pre-built binaries can be downloaded from [Bintray](https://github.com/hohonuuli/vars/releases). 

## Building

For the impatient:

1. Checkout VARS
  `git clone https://github.com/hohonuuli/vars.git`
2. Configure a Github security token. This is need to access custom libraries written by MBARI. Instructions are [here](https://github.com/mbari-org/maven)
3. Build
  `mvn install -P dev`
  
More details can be found in [BUILD.md](https://github.com/hohonuuli/vars/blob/master/BUILD.md)
