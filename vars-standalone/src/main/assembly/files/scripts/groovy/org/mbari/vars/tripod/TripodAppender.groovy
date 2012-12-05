package org.mbari.vars.tripod


import vars.ToolBox
import java.text.SimpleDateFormat
import vars.annotation.ui.PersistenceController
import org.ccil.cowan.tagsoup.Parser
import org.apache.sanselan.Sanselan
import org.apache.sanselan.formats.tiff.constants.TiffConstants
import vars.annotation.VideoFrame
import vars.annotation.CameraData
import vars.annotation.PhysicalData
import vars.annotation.VideoArchive
import vars.annotation.Observation
import vars.knowledgebase.ConceptName
import org.mbari.movie.Timecode

/**
 * Appends image from the tripod onto an existing VARS videoarchive. Existing
 * annotations will not be modified.
 */
class TripodAppender {

    def toolBox = new ToolBox()
    def dateFormat = new SimpleDateFormat('yyyy:MM:dd HH:mm:ss')
    def timecodeFormat = new SimpleDateFormat("HH:mm:ss:'00'")


    def TripodAppender() {
        dateFormat.timeZone = TimeZone.getTimeZone('UTC')
        timecodeFormat.timeZone = TimeZone.getTimeZone('UTC')
    }

    def load(URL remoteImageDirectory,
            String videoArchiveName) {

        def annotationFactory = toolBox.toolBelt.annotationFactory

        try {
            // Look up existing archive
            def videoArchiveDAO = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveDAO()
            def videoArchive = videoArchiveDAO.findByName(videoArchiveName)
            if (!videoArchive) {
                print("Unable to find $videoArchive in the database")
                return
            }

            // Figure out what images need to be loaded
            def images = TripodLoader.fetchImagesURLs(remoteImageDirectory) // All Images
            def existingImages = videoArchive.videoFrames.collect { vf ->   // Images that exist as annotations
                return vf?.cameraData?.imageReference
            } findAll { it != null}
            images.removeAll(existingImages) // Mutate images. It now contains only missing images
            if (images.isEmpty()) {
                return
            }

            // Get largest timecode
            def maxTimecodeString = videoArchive.videoFrames.collect { vf -> vf.timecode} max()
            def maxTimecode = new Timecode(maxTimecodeString, 30)
            def startIdx = maxTimecode.frames + 1

            // Get a default concept
            def conceptDAO = toolBox.toolBelt.knowledgebaseDAOFactory.newConceptDAO()
            conceptDAO.startTransaction()
            def conceptNameAsString = conceptDAO.findRoot().primaryConceptName.name
            conceptDAO.close()

            // Process Images
            def observationDate = new Date()
            def n = 0
            def inTransaction = false
            videoArchiveDAO.startTransaction()
            images.eachWithIndex { imageRef, idx ->
                if (!inTransaction) {
                    videoArchiveDAO.startTransaction()
                    inTransaction = true
                }

                print("Processing ${imageRef}")
                def url = new URL(imageRef)
                def stream = url.openStream()
                def metadata = Sanselan.getMetadata(stream, "FOO")
                stream.close()
                def creationDate = metadata.findEXIFValue(TiffConstants.EXIF_TAG_CREATE_DATE)
                def date = null
                if (creationDate) {
                    date = dateFormat.parse(creationDate.valueDescription[1..-2])
                }
                def timecode = new Timecode(idx + startIdx, 30)
                VideoFrame videoFrame = videoArchive.findVideoFrameByTimeCode(timecode.toString())
                if (videoFrame == null) {
                    videoFrame = annotationFactory.newVideoFrame()
                    videoFrame.timecode = timecode.toString()
                    videoArchive.addVideoFrame(videoFrame)
                    videoArchiveDAO.persist(videoFrame)
                }
                videoFrame.recordedDate = date
                CameraData cameraData = videoFrame.cameraData
                //cameraData.z = lensToSeafloorDistance
                //cameraData.pitch = angleOfInclination
                cameraData.imageReference = url.toExternalForm()
                //PhysicalData physicalData = videoFrame.physicalData
                //physicalData.latitude = latitude
                //physicalData.longitude = longitude
                //physicalData.depth = depth
                print(" ... ${videoFrame.timecode}")
                if (videoFrame.observations.isEmpty()) {
                    Observation observation = annotationFactory.newObservation()
                    observation.conceptName = conceptNameAsString
                    observation.observationDate = observationDate
                    observation.observer = getClass().simpleName
                    videoFrame.addObservation(observation)
                    videoArchiveDAO.persist(observation)
                }
                


                println(" ... done")
                n = n + 1

                if (n == 50) {
                    print(" ! commiting checkpoint")
                    videoArchiveDAO.endTransaction()
                    inTransaction = false
                    n = 0
                    println(" ... done")
                }

            }

            if (inTransaction) {
                videoArchiveDAO.endTransaction()
            }

            videoArchiveDAO.close()


        }
        catch (Exception e) {
            println("\nFailed to process images in ${remoteImageDirectory}")
            e.printStackTrace()
        }



    }

}