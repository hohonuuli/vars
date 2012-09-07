package vars.annotation

import vars.ToolBox
import org.mbari.movie.Timecode
import org.slf4j.LoggerFactory

/**
 * Changes the date portion (Year, month, day) of the recordedDate field leaving the time (hour,
 * minutes, seconds) untouched. Useful if you're annotating video fields that have time recorded
 * onto the timecode track but need to correct the date portion.
 * @author Brian Schlining
 * @since 2012-09-06
 */
class DateAdjuster {

    def toolbox = new ToolBox()
    def log = LoggerFactory.getLogger(getClass())


    def adjust(String videoArchiveName, Date date) {

        VideoArchiveDAO dao = toolbox.toolBelt.annotationDAOFactory.newVideoArchiveDAO()
        dao.startTransaction()
        def videoArchive = dao.findByName(videoArchiveName)
        if (videoArchive) {
            println("Found ${videoArchive.videoFrames.size()} VideoFrames")

            def toCalendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"))
            toCalendar.setTime(date)
            def fromCalendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"))


            videoArchive.videoFrames.each { VideoFrame vf ->
                try {
                    def timecode = new Timecode(vf.timecode)
                    def recordedDate = vf.recordedDate
                    fromCalendar.setTime(recordedDate)
//                toCalendar.set(Calendar.HOUR_OF_DAY, fromCalendar.get(Calendar.HOUR_OF_DAY))
//                toCalendar.set(Calendar.MINUTE, fromCalendar.get(Calendar.MINUTE))
//                toCalendar.set(Calendar.SECOND, fromCalendar.get(Calendar.SECOND))
                    toCalendar.set(Calendar.HOUR_OF_DAY, timecode.hour)
                    toCalendar.set(Calendar.MINUTE, timecode.minute)
                    toCalendar.set(Calendar.SECOND, timecode.second)
                    vf.recordedDate = toCalendar.getTime()
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
