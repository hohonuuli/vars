package org.mbari.vars.benthicrover

import vars.ToolBox
import vars.annotation.jpa.CameraDataDAOImpl

import java.text.SimpleDateFormat
import vars.annotation.ui.PersistenceController
import org.ccil.cowan.tagsoup.Parser
import vars.annotation.VideoFrame
import vars.annotation.CameraData
import vars.annotation.PhysicalData
import vars.annotation.VideoArchive
import vars.annotation.Observation
import vars.knowledgebase.ConceptName
import org.mbari.movie.Timecode

/**
 * Useful for adding images on a webserver to a VideoArchive. You just need
 * the webserver directory (it should provide a directory listing) and the
 * name of the videoarchive. 
 *
 * This class should work with both tripod and benthic rover images
 */
class AppendImageLoader {

    def toolBox = new ToolBox()

    def load(URL remoteImageDirectory,
             String videoArchiveName) {

        def annotationFactory = toolBox.toolBelt.annotationFactory

        try {
            // Get root concept to use for new annotations
            def conceptDAO = toolBox.toolBelt.knowledgebaseDAOFactory.newConceptDAO()
            conceptDAO.startTransaction()
            def conceptNameAsString = conceptDAO.findRoot().primaryConceptName.name
            conceptDAO.close()

            def videoArchiveDAO = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveDAO()
            def cameraDataDAO = new CameraDataDAOImpl(videoArchiveDAO.entityManager)
            videoArchiveDAO.startTransaction()
            VideoArchive videoArchive = videoArchiveDAO.findByName(videoArchiveName)
            if (videoArchive == null) {
                println("$videoArchiveName was not found in the database")
                return
            }

            // Get maximum timecode
            def videoFrames = videoArchive.videoFrames.sort { it.timecode }
            def timecodeIdx = 0 // used to create new timecodes
            if (videoFrames.size() > 0) {
                timecodeIdx = (new Timecode(videoFrames[-1].timecode, 30)).frames
            }

            // Parse URLs of images from directory listing
            def images = fetchImagesURLs(remoteImageDirectory)
            def observationDate = new Date()

            def n = 0
            def inTransaction = false
            def newImageCount = 0
            images.eachWithIndex { imageRef, idx ->
                if (!inTransaction) {
                    videoArchiveDAO.startTransaction()
                    inTransaction = true
                }
                print("Processing ${imageRef}")
                def cameraData = cameraDataDAO.findByImageReference(imageRef)
                if (cameraData == null) {
                    // Image not in DB
                    def url = new URL(imageRef)
                    timecodeIdx = timecodeIdx + 1
                    def timecode = (new Timecode(timecodeIdx, 30)).toString()
                    def videoFrame = annotationFactory.newVideoFrame()
                    videoFrame.timecode = timecode
                    cameraData = videoFrame.cameraData
                    cameraData.imageReference = url.toExternalForm()
                    print(" ... adding videoframe at ${timecode}")
                    def observation = annotationFactory.newObservation()
                    observation.conceptName = conceptNameAsString
                    observation.observationDate = observationDate
                    observation.observer = getClass().simpleName
                    videoArchive.addVideoFrame(videoFrame)
                    videoFrame.addObservation(observation)
                    videoArchiveDAO.persist(videoFrame)
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
        println("End processing for video archive: $videoArchiveName")
    }

    static fetchImagesURLs(URL remoteDirectory) {
        def remotePath = remoteDirectory.toExternalForm()
        if (!remotePath.endsWith('/')) {
            remotePath = remotePath + "/"
        }
        println("Fetching images from ${remoteDirectory}")
        def images = []
        def slurper = new XmlSlurper(new Parser())
        remoteDirectory.withReader { reader ->
            def html = slurper.parse(reader)
            def rows = html.body.table.tr
            rows.each { row ->
                try {
                    // A is an anchor/link to an image
                    def a = row.td[1].a.@href.text()
                    images << remotePath + a
                }
                catch (Exception e) {
                    e.printStackTrace()
                }
            }
        }
        return images.findAll { i -> i.toUpperCase().endsWith("JPG") ||
                i.toUpperCase().endsWith("PNG") }
    }
}