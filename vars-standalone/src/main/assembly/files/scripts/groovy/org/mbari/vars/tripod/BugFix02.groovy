package org.mbari.vars.tripod

import vars.annotation.VideoArchive
import vars.annotation.VideoArchiveDAO
import vars.annotation.VideoFrame

/**
 * This fix merges two videoarchives that have overlapping images (same URLs) but
 * the images are regsitered at different timecodes
 */

def srcName = "SES Pulse 61"
def targetName = "T0061-01-Tripod"


final toolBox = new vars.ToolBox()
VideoArchiveDAO dao = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveDAO()
dao.startTransaction()
VideoArchive srcVa = dao.findByName(srcName)
VideoArchive targetVa = dao.findByName(targetName)

if (srcVa && targetVa) {
    srcVa.videoFrames.each { srcVf ->

        VideoFrame targetVf = targetVa.videoFrames.find {
            it.cameraData.imageReference == srcVf.cameraData.imageReference
        }

        def observations = srcVf.observations
        observations.each { obs ->
            println("Moving $obs from $srcVf to $targetVf")
            srcVf.removeObservation(obs)
            targetVf.addObservation(obs)
        }

    }
}

dao.endTransaction()
dao.close()
