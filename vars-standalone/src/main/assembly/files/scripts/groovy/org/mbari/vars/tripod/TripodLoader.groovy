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
 *
 * @author Brian Schlining
 * @since 2011-11-11
 */
class TripodLoader {

    def toolBox = new ToolBox()
    def dateFormat = new SimpleDateFormat('yyyy:MM:dd HH:mm:ss')
    def timecodeFormat = new SimpleDateFormat("HH:mm:ss:'00'")

    def TripodLoader() {
        dateFormat.timeZone = TimeZone.getTimeZone('UTC')
        timecodeFormat.timeZone = TimeZone.getTimeZone('UTC')
    }

    def load(URL remoteImageDirectory,
             String platform,
             Integer sequenceNumber,
             Double longitude,
             Double latitude,
             Double depth,
             Double lensToSeafloorDistance,
             Double angleOfInclination) {
        def annotationFactory = toolBox.toolBelt.annotationFactory
        def videoArchiveName = PersistenceController.makeVideoArchiveName(platform, sequenceNumber, 1, "-tripod")
        try {
            def conceptDAO = toolBox.toolBelt.knowledgebaseDAOFactory.newConceptDAO()
            conceptDAO.startTransaction()
            def conceptNameAsString = conceptDAO.findRoot().primaryConceptName.name
            conceptDAO.close()
            def videoArchiveDAO = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveDAO()

            VideoArchive videoArchive = videoArchiveDAO.findOrCreateByParameters(platform, sequenceNumber, videoArchiveName)

            // Parse URLs of images from directory listing
            def images = fetchImagesURLs(remoteImageDirectory)
            def observationDate = new Date()
            //
            def n = 0
            def inTransaction = false
            images.eachWithIndex { imageRef, idx ->
                if (!inTransaction) {
                    videoArchiveDAO.startTransaction()
                }
                print("Processing ${imageRef}")
                def url = new URL(imageRef)
                def stream = url.openStream()
                def metadata = Sanselan.getMetadata(stream, "FOO")
                stream.close()
                def creationDate = metadata.findEXIFValue(TiffConstants.EXIF_TAG_CREATE_DATE)
                def date = dateFormat.parse(creationDate.valueDescription[1..-2])
                def timecode = new Timecode(idx, 30)
                VideoFrame videoFrame = videoArchive.findVideoFrameByTimeCode(timecode.toString())
                if (videoFrame == null) {
                    videoFrame = annotationFactory.newVideoFrame()
                    videoFrame.timecode = timecode.toString()
                    videoArchive.addVideoFrame(videoFrame)
                    videoArchiveDAO.persist(videoFrame)
                }
                videoFrame.recordedDate = date
                CameraData cameraData = videoFrame.cameraData
                cameraData.z = lensToSeafloorDistance
                cameraData.pitch = angleOfInclination
                cameraData.imageReference = url.toExternalForm()
                PhysicalData physicalData = videoFrame.physicalData
                physicalData.latitude = latitude
                physicalData.longitude = longitude
                physicalData.depth = depth
                print(" ... ${videoFrame.timecode}")
                Observation observation = annotationFactory.newObservation()
                observation.conceptName = conceptNameAsString
                observation.observationDate = observationDate
                observation.observer = getClass().simpleName
                videoFrame.addObservation(observation)
                videoArchiveDAO.persist(observation)
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

    def fetchImagesURLs(URL remoteDirectory) {
        def remotePath = remoteDirectory.toExternalForm()
        if (!remotePath.endsWith('/')) {
            remotePath = remotePath + "/"
        }
        def images = []
        def slurper = new XmlSlurper(new Parser())
        remoteDirectory.withReader { reader ->
            def html = slurper.parse(reader)
            def rows = html.body.table.tr
            rows.each { row ->
                try {
                    // A is an anchor/link to an image
                    def a = row.td[1].a
                    images << remotePath + a
                }
                catch (Exception e) {
                    e.printStackTrace()
                }
            }
        }
        return images.findAll { it.toUpperCase().endsWith("JPG")}
    }


}

