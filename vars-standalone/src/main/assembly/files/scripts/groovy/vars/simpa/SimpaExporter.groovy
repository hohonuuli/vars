package vars.simpa

import vars.annotation.ui.ToolBelt
import vars.annotation.VideoArchiveSet
import vars.annotation.VideoFrame
import vars.annotation.ObservationsSpatialLocations
import vars.shared.ui.GlobalLookup
import java.text.DecimalFormat
import org.slf4j.LoggerFactory
import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Mar 8, 2010
 * Time: 10:41:56 AM
 * To change this template use File | Settings | File Templates.
 */
class SimpaExporter {

    final VideoArchiveSet videoArchiveSet
    final ToolBelt toolBelt
    private final log = LoggerFactory.getLogger(getClass())

    def SimpaExporter(toolBelt, videoArchiveSet) {
        this.toolBelt = toolBelt;
        this.videoArchiveSet = videoArchiveSet;
    }

    def apply(File target) {
        def dao = toolBelt.annotationDAOFactory.newDAO()
        dao.startTransaction()
        def vas = dao.find(videoArchiveSet)
        def out = new BufferedOutputStream(new FileOutputStream(target))
        DecimalFormat nf = new DecimalFormat()
        nf.decimalSeparatorAlwaysShown = false
        nf.minimumFractionDigits = 3
        nf.maximumIntegerDigits = 3

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS")
        timeFormat.timeZone = TimeZone.getTimeZone("UTC")

        def s = ","

        out << "# Created on ${new Date()} by ${getClass()}\n"
        out << "# Date${s}Time${s}Timecode${s}Concept${s}Tape${s}HeadingRadians${s}xPixel${s}yPixel${s}widthPixels${s}heightPixels${s}xMeters${s}yMeters${s}widthMeters${s}heightMeters${s}xImageCenterMeters${s}yImageCenterMeters\n"
        vas.videoFrames.sort({ it.recordedDate }).each { VideoFrame videoFrame ->
            log.info("Analyzing ${videoFrame}")
            


            try {
                def osl = new ObservationsSpatialLocations(videoFrame)

                osl.spatialLocations.each { observation, point ->
                    def cameraData = videoFrame.cameraData
                    out << "${dateFormat.format(videoFrame.recordedDate)}${s}"
                    out << "${timeFormat.format(videoFrame.recordedDate)}${s}"
                    //out << "${GlobalLookup.DATE_FORMAT_UTC.format(videoFrame.recordedDate)}${s}"
                    out << "${videoFrame.timecode}${s}"
                    out << "${observation.conceptName}${s}"
                    out << "${videoFrame.videoArchive.name}${s}"
                    out << "${cameraData.heading}${s}"
                    out << "${Math.round(observation.x) as Integer}${s}"
                    out << "${Math.round(observation.y) as Integer}${s}"
                    out << "${osl.widthInPixels}${s}"
                    out << "${osl.heightInPixels}${s}"
                    out << "${nf.format(point.x)}${s}"
                    out << "${nf.format(point.y)}${s}"
                    out << "${nf.format(cameraData.viewWidth)}${s}"
                    out << "${nf.format(cameraData.viewHeight)}${s}"
                    out << "${nf.format(cameraData.x)}${s}"
                    out << "${nf.format(cameraData.y)}\n"

                }
            }
            catch (Exception e) {
                log.warn("Failed to analyze ${videoFrame}", e)
            }
        }

        out.close()
        dao.endTransaction()

    }


}
