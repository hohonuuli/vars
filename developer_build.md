---
layout: default
title: VARS Developer Documentation
---

## Introduction

The VARS software is open source; it is available for you to use and modify to fit your needs. The source code is available at [https://github.com/hohonuuli/vars](https://github.com/hohonuuli/vars).

Out-of-the-box, VARS works on Windows, Mac OS X and Linux. It requires the Java Runtime Environment (Java 7 or later for VCRs, Java 8 or later for video files).

## Setting up for Development and Building

Detailed instruction for building VARS are available in the [README.txt file](https://github.com/hohonuuli/vars/blob/develop/README.txt)

### For Building VARS for VCR Annotations

The following commands will be VARS. The build application will be the zip file located in _vars\vars-standalone\target_.

```
git clone https://github.com/hohonuuli/vars.git
git checkout develop
mvn install -P dev
```

### For Building VARS for Video-file Annotations

The following commands will be VARS. The build application will be the zip file located in _vars\vars-standalone\target_.

```
git clone https://github.com/hohonuuli/vars.git
git checkout videofile-jdk8
mvn install -P dev
```

## To Run the Build Application

1. Extract the zip file, _vars/vars-standalone/target/vars-standalone-[VERSION]-scripting.zip_.
2. Open a Terminal or Command Prompt, in the directory created by extracting the zip file. Then `cd bin` to be in the correct directory.
3. On Macs, Linux or UNIX, you may need to exectute `chmod u+x *`
4. Start the included database server from the Terminal using `./derbyStart` on Mac, or `derbyStart.bat` on Windows.
5. The first time you run VARS after a build you will need to run the Knowledgebase application in order to create an administrator account. Use knowledgebase(.bat) to start it.
6. You can launch the annotation application using annotation(.bat) and the Query application using query(.bat)
7. When you're done you can stop the database server using derbyStop(.bat)