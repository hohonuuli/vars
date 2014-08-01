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

1. Click on the __New__ button (or ctrl+n / cmd+n). A new line will be created in the observation table with the default object in the observation column and with the current VCR timecode.
2. Type the concept name in the editor and press __Enter__. The new concept name will be updated in the observation table.
3. For faster annotations see information below about a __custom icon panel__.

<p align="center">
    <a href="images/annotation_app4.png"><img width="400" src="images/annotation_app4.png" /></a>
</p>

### Adding an Annotation with a Frame Grab

1. To create an annotation associated with a frame grab, click the frame grab (__F__) button (or ctrl+f / cmd+f ).
2. The framegrab will be displayed in the upper right corner of the user interface, under the __Frame-grab__ tab. By default, the concept physical-object will be entered in the observation column. The __F__ button in the __FG/S__ column of the observation table will be highlighted in green, indicating there is a frame grab associated with this observation.
3. To give the annotation a more specific name (concept), type the concept in the concept editor (located just under the observation table), and press __Enter__.

<p align="center">
    <a href="images/annotation_app5.png"><img width="400" src="images/annotation_app5.png" /></a>
</p>

### Adding an association to an observation

An _association_ is a structured descriptor that provides additional information about an annotation. For example, color of the item, behavior such as swimming or eating, resting on some substrate, etc.

1. Select a row in the annotation table.
2. In the concept editor click on the green __+__ button.
3. In the association search box, type part of the association you wish to search for and press enter. 
4. You can continue to press enter to scroll through additional matches.
5. Press the green __+__ button again to add teh association. The association editor will automatically close.

<p align="center">
    <a href="images/annotation_app6.png"><img width="400" src="images/annotation_app6.png" /></a>
</p>

<p align="center">
    <a href="images/annotation_app7.png"><img width="400" src="images/annotation_app7.png" /></a>
</p>