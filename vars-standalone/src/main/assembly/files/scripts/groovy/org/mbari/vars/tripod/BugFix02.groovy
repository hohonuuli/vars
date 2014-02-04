package org.mbari.vars.tripod

import org.mbari.movie.Timecode
import vars.annotation.VideoArchive
import vars.annotation.VideoArchiveDAO
import vars.annotation.VideoFrame

/**
 * This fix merges two videoarchives that have overlapping images (same URLs) but
 * the images are regsitered at different timecodes
 */

def targetName = "SES Pulse 61"
def srcName = "T0061-01-Tripod"

def doReorder = false
final toolBox = new vars.ToolBox()
VideoArchiveDAO dao = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveDAO()
dao.startTransaction()
VideoArchive srcVa = dao.findByName(srcName)
VideoArchive targetVa = dao.findByName(targetName)

if (srcVa && targetVa) {

    // We need max timecode
    def maxTimecode = new Timecode(0.0)
    targetVa.videoFrames.each { vf ->
        def t = new Timecode(vf.timecode)
        if (t.frames > maxTimecode.frames) {
            maxTimecode = t;
        }
    }
    println("Maximum timecode in ${targetVa} is ${maxTimecode}")

    // Move frames in here
    def srcVideoframes = new ArrayList(srcVa.videoFrames)
    srcVideoframes.each { srcVf ->

        VideoFrame targetVf = targetVa.videoFrames.find {
            it.cameraData.imageReference == srcVf.cameraData.imageReference
        }

        if (targetVf == null) {
            // not found in targetVa. We need to move it AND rename the timecode
            def t = new Timecode(srcVf.timecode)
            def t1 = new Timecode(t.frames + maxTimecode.frames)
            println("Moving ${srcVf.timecode} as ${t1} to ${targetVa}")
            srcVf.timecode = t1.toString()
            srcVa.removeVideoFrame(srcVf)
            targetVa.addVideoFrame(srcVf)
            doReorder = true
        }
        else {
            def observations = new ArrayList(srcVf.observations)
            observations.each { obs ->
                println("Moving $obs from $srcVf to $targetVf")
                srcVf.removeObservation(obs)
                targetVf.addObservation(obs)
            }
        }

    }
}

dao.endTransaction()

// Retag timecodes based on order of images
if (doReorder) {
    dao.startTransaction()
    targetVa = dao.find(targetVa)
    def vfs = new ArrayList(targetVa.videoFrames)
    vfs.sort { a, b ->
        a.cameraData.imageReference <=> b.cameraData.imageReference
    }
    def vas = new ArrayList(targetVa.videoFrames)
    vas.eachWithIndex { vf, i ->
        def t = new Timecode(i)
        println("Changing timecode from ${vf.timecode} to ${t}")
        vf.timecode = t.toString()
    }
    dao.endTransaction()
}

dao.close()


