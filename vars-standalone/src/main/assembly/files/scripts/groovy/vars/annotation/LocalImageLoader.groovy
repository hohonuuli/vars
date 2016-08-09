package vars.annotation

import org.apache.sanselan.common.IImageMetadata
import org.apache.sanselan.formats.tiff.constants.TiffConstants
import org.apache.sanselan.Sanselan
import org.mbari.vcr4j.time.Timecode
import org.slf4j.LoggerFactory
import vars.ToolBox
import vars.annotation.ui.PersistenceController

import java.text.SimpleDateFormat
import java.util.stream.Collectors

class LocalImageLoader {

    def toolBox = new ToolBox()
    private final log = LoggerFactory.getLogger(getClass())
    private final observer = getClass().simpleName
    def dateFormat = new SimpleDateFormat('yyyy:MM:dd HH:mm:ss')

    def LocalImageLoader() {
        dateFormat.timeZone = TimeZone.getTimeZone('UTC')
    }

    def loadDir(File dir, String platform, Integer sequenceNumber) {
        def files = dir.listFiles() as List
        List<URL> urls = files.stream()
                .filter( { f -> !f.isDirectory() })
                .filter( { f -> f.absolutePath.toUpperCase().endsWith(".PNG") || f.absolutePath.toUpperCase().endsWith(".JPG") })
                .map( { f -> f.toURI().toURL() })
                .collect(Collectors.toList())
        load(urls, platform, sequenceNumber)
    }

    def load(List<URL> images, String platform, Integer sequenceNumber) {
        def observationDate = new Date()
        def n = 0
        def inTransaction = false
        def annotationFactory = toolBox.toolBelt.annotationFactory
        def videoArchiveName = PersistenceController.makeVideoArchiveName(platform, sequenceNumber, 1, "-local")
        try {
            def conceptDao = toolBox.toolBelt.knowledgebaseDAOFactory.newConceptDAO()
            conceptDao.startTransaction()
            def conceptNameAsString = conceptDao.findRoot().primaryConceptName.name
            conceptDao.endTransaction()
            def videoArchiveDAO = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveDAO()
            def videoArchive = videoArchiveDAO.findOrCreateByParameters(platform, sequenceNumber, videoArchiveName)

            images.eachWithIndex { url, idx ->
                print("Processing ${url.toExternalForm()}")
                if (!inTransaction) {
                    videoArchiveDAO.startTransaction()
                    inTransaction = true
                }
                def date = extractCreationDate(url)
                def timecode = new Timecode(idx, 60)
                def videoFrame = videoArchive.findVideoFrameByTimeCode(timecode.toString())
                if (videoFrame == null) {
                    videoFrame = annotationFactory.newVideoFrame()
                    videoFrame.timecode = timecode.toString()
                    videoArchive.addVideoFrame(videoFrame)
                    videoArchiveDAO.persist(videoFrame)
                }
                videoFrame.recordedDate = date
                videoFrame.cameraData.imageReference = url.toExternalForm()
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
            log.error("Something bad happened. Load is stopping", e)
            e.printStackTrace()
        }
        println("End processing for video archive: $videoArchiveName")
    }

    def extractCreationDate(URL url) {
        def date = null
        try {
            def stream = url.openStream()
            IImageMetadata metadata = Sanselan.getMetadata(stream, "FOO")
            def creationDate = metadata.(TiffConstants.EXIF_TAG_CREATE_DATE)
            stream.close()
            if (creationDate) {
                date = dateFormat.parse(creationDate.valueDescription[1..-2])
            }
        }
        catch (Exception e) {
            print(" [unable to read creation date] ")
            //e.printStackTrace()
        }
        return date
    }


}