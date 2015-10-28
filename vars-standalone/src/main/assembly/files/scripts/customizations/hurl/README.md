# README

This doc contains the steps performed to migrate the HURL VARS database to a more current version

## Castor to JPA migration

HURL has extensive annotations from VARS 5.1 (Castor-based). I did the following to migrate the database to VARS 8.1 (JPA-based).

### Preparation

- Edit _derbySetup_ and change the host to be `DERBY_SERVER_HOST=localhost`
- Delete the old derby jars and replace with latest version

### Database Migration

1. Run `update-hurlCastorToJPA1-anno.sql`
2. In `gsh` run `DestroyDuplicateFKFunction` and `CombineDuplicatesFunction`
3. Run `update-hurlCastorToJPA2-anno.sql`
4. Run `update-hurlCastorToJPA1-kb.sql`
5. Run `update-hurlImageURLS-anno.sql`


## Build, Installation, and Deployment

VARS is deployed to the server _max5kn1.soest.hawaii.edu_. I deployed the following:
- A VARS database build. This will generally never be touched once it's running.
- A standalone build of VARS that will be used for merging data and other maintenance tasks.
- A webstart build for annotating video files
- A webstart build for annotating VCR tapes

### Build

Run `mvn clean install -P hurl`

When deploying the `vars-database` version. I stripped out everything that wasn't derby related. Then edited `derbyrun` so that the DERBY_SERVER_HOST is dynamically set using `hostname`

### Intallation and Deploy

Copy build to _max5kn1.soest.hawaii.edu_ in the _max5khurl2_ share. This share has the following structure:

```
└── vars
    ├── applications
    │   ├── vars-8.1-videofile                      
    │   ├── vars-8.1-database
    │   ├── vars-database -> vars-8.1-database/     
    │   ├── vars-scripting-> vars-8.1-videofile/
    │   └── webstart
    ├── imagearchive
    │   └── data
    ├── videoarchive
    └── videosamples
```

- __applications__ contains the static VARS builds. We need one for doing tasks like merging (e.g. _vars-videofile_). The other one contains the database build whose sole responsibility is to run the derby database (i.e. _vars-database_)
- __imagearchive__ is the location where users should write images. It is mapped on a webserver as `http://max5kn1.soest.hawaii.edu/imagearchive/`. It currently has the following structure:
```
vars/imagearchive/data
├── M
├── P4
├── P5
├── R
└── RCV
```
- __videoarchive__ is where the proxied _.mv4_ files will be stored.
- __videosamples__ contains videos used for VARS development and testing. This directory does not need to be retained.

#### vars-database

A stripped down VARS build is symlinked to vars-database. This directory is owned by the user `derby` which has the same pwd as Chris. It's home directory is `/export/maxarray2/derby`. The VARS database is mapped locally as `/export/maxarray2/vars/applications/vars-database`. That directory is owned by the `derby` user. The script `script/customizations/hurl/derby` is symlinked to `/etc/init.d/derby`.

## NOTES

- For an Apple Specific video playback app, we could just have an app with a playback window (via AVFoundation) that is talked to via TCP. Here's links to TCP sockets in Objective-C: https://gist.github.com/rjungemann/446256, https://github.com/robbiehanson/CocoaAsyncSocket, https://github.com/dreese/FastSocket, https://github.com/socketio/socket.io-client-swift
