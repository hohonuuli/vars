package vars.annotation

import vars.ToolBox
import org.mbari.movie.Timecode
import org.slf4j.LoggerFactory

/**
 * Sets the recordedDates in each VideoFrame based on a) a start date/time and
 * b) The timecode as an elapsed time since the start date/time you provide. 
 * Useful for video files without a timecode track.
 * @author Brian Schlining
 * @since 2012-09-06
 */
class VideofileDateAdjuster {

    def toolbox = new ToolBox()
    def log = LoggerFactory.getLogger(getClass())


    def adjust(String videoArchiveName, Date date) {

        VideoArchiveDAO dao = toolbox.toolBelt.annotationDAOFactory.newVideoArchiveDAO()
        dao.startTransaction()
        def videoArchive = dao.findByName(videoArchiveName)
        if (videoArchive) {
            println("Found ${videoArchive.videoFrames.size()} VideoFrames")

            videoArchive.videoFrames.each { VideoFrame vf ->
                try {
                    def timecode = new Timecode(vf.timecode)
                    vf.recordedDate = new Date(date.time + (timecode.frames * timecode.frameRate * 1000L) as Long)
                }
                catch (Exception e) {
                    log.warn("Unable to parse timecode of ${vf.timecode}")
                }
            }
        }
        else {
            println("!!! Unable to find ${videoArchiveName} !!!")
        }
        dao.endTransaction()
        dao.close()
    }

}
