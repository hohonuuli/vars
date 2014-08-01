---
layout: default
title: VARS Annotation User Guide
---

This guide provides basic instructions for starting and using the VARS Annotation application. Useful definitions can be found in the [glossary](glossary.html)

## Set-up and Getting Started

- If you are working with __video tapes__, please read the instructions in this [README](https://github.com/hohonuuli/vars/tree/master/vars-standalone/src/main/assembly/files) file.
- If you are annotating __video files__, follow the setup instruction in this [README](https://github.com/hohonuuli/vars/tree/videofile-jdk8/vars-standalone/src/main/assembly/files) file.

## Logging in and Opening a Video

1. __Login to VARS__. To annotate in VARS you will need a VARS user account. If this is the very first time that VARS is being launched on a new database, you will need to create an admin account. Please see the [Knowledgebase User Guide](knowledgebase_userguide.html) for instruction on creating an admin account. If you just need to create a non-admin user account, do the following:
    1. Click on __User:__ on the toolbar. [<img width="100" src="images/annotation_app2.png">](images/annotation_app2.png)
    2. In the login dialog that appears, click on _Create a new user account_. [<img width="100" src="images/annotation_app_userlogin2.png">](images/annotation_app_userlogin2.png)
2. __Open a movie file__.  
    1. Click on the __Video:__ on the toolbar[<img width="100" src="images/annotation_app3.png">](images/annotation_app3.png)
    2. In the movie dialog that appears, you can choose to browse to a new movie or open one that had previously been annotated.
        - To browse to a new movie, select the _Open by Location_ check box. Then browse to or enter a URL to a movie. You __must__ also select a _camera platform_ and enter a _sequence number_.
        - To open a previously annotated move, select the _Open Existing_ checkbox. Then select the name of the file you'd like to annotated.
        - [<img width="100" src="images/annotation_app_moviedialog.png">](images/annotation_app_moviedialog.png)

## Video Controls
The interface includes a VCR control panel. This panel was designed for use with VCR's but works _mostly_ as expected with video files.  

<p align="center">
    <img src="images/annotation_vcr_control.jpg" />
</p>

## Adding and Modifying Annotations

### Adding an Annotation

1. Click on the __New_ button (or ctrl+n / cmd+n). A new line will be created in the observation table with the default object in the observation column and with the current VCR timecode.
2. Type the concept name in the editor and press __Enter__. The new concept name will be updated in the observation table.
3. For faster annotations see information below about a __custom icon panel__.

<p align="center">
    [<img width="400" src="images/annotation_app4.png" />](images/annotation_app4.png)
</p>