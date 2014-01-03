package org.mbari.vars.tripod

import vars.annotation.VideoArchive
import vars.annotation.VideoArchiveDAO
import vars.query.util.Dataset
import vars.query.util.QueryResultsReader

/*
   This is a bug fix for 2 video archives containing overlapping sets of images.
   This fix consolidates them to a single video archive

 */

File file = new File(args[0]);
def name = "SES Pulse 61"

final toolBox = new vars.ToolBox()
VideoArchiveDAO dao = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveDAO()
dao.startTransaction()
VideoArchive pulse61 = dao.findByName(name)

// Dataset contains data from our text file query results. rowIdx contains indexs of rows
// that are for SES Pulse 61
Dataset dataset = QueryResultsReader.read(file);
def videoArchiveNames = dataset.getData("videoArchiveName")
def rowIdx = []
videoArchiveNames.eachWithIndex { n, index ->
    if (n == name) {
        rowIdx << index
    }
}
def allTimecodes = dataset.getData("TapeTimeCode")
def allImageRefs = dataset.getData("Image")
def timecodes = []
def imageRefs = []
rowIdx.each { i ->
    timecodes << allTimecodes[i]
    imageRefs << allImageRefs[i]
}


pulse61.getVideoFrames().each { vf ->
    def matchingIndx = timecodes.findIndexOf { it == vf.timecode }
    if (matchingIndx >= 0) {
        println("Updating $vf image to ${imageRefs[matchingIndx]}")
        vf.cameraData.imageReference = imageRefs[matchingIndx]
    }
}
dao.endTransaction()
dao.close()




