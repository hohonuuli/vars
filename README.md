vars
====

MBARI's Video Annotation and Reference System

## Building

1. Checkout VARS
  `git clone https://github.com/hohonuuli/vars.git`
2. Build
  `mvn install -P dev`

## Workflow

VARS uses a [Gitflow Workflow](https://www.atlassian.com/git/workflows#!workflow-gitflow). The _master_ branch is for releases. There is a _videofile_ branch for videofile specific releases. Most development should be done on the _develop_ branch. For example:

```
git clone https://github.com/hohonuuli/vars.git
git checkout -b develop origin/develop

```

