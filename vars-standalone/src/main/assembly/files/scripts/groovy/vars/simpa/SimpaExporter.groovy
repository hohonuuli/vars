package vars.simpa

import vars.annotation.ui.ToolBelt
import vars.annotation.VideoArchiveSet
import vars.annotation.VideoFrame
import vars.annotation.Observation
import vars.annotation.ObservationSpatialLocation
import vars.annotation.ObservationsSpatialLocations
import java.text.NumberFormat
import java.text.DecimalFormat

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

        out << "# Created on ${new Date()} by ${getClass()}\n"
        out << "# Date\tTimecode\tConcept\tTape\tHeadingRadians\txPixel\tyPixel\twidthPixels\theightPixels\txMeters\tyMeters\twidthMeters\theightMeters\txImageCenterMeters\tyImageCenterMeters\n"
        vas.videoFrames.sort({ it.recordedDate }).each { VideoFrame videoFrame ->
            def osl = new ObservationsSpatialLocations(videoFrame)

            osl.spatialLocations.each { observation, point ->
                def cameraData = videoFrame.cameraData
                out << "${videoFrame.recordedDate}\t"
                out << "${videoFrame.timecode}\t"
                out << "${observation.conceptName}\t"
                out << "${videoFrame.videoArchive.name}\t"
                out << "${cameraData.heading}\t"
                out << "${Math.round(observation.x) as Integer}\t"
                out << "${Math.round(observation.y) as Integer}\t"
                out << "${osl.widthInPixels}\t"
                out << "${osl.heightInPixels}\t"
                out << "${nf.format(point.x)}\t"
                out << "${nf.format(point.y)}\t"
                out << "${nf.format(cameraData.viewWidth)}\t"
                out << "${nf.format(cameraData.viewHeight)}\t"
                out << "${nf.format(cameraData.x)}\t"
                out << "${nf.format(cameraData.y)}\n"

            }
        }

        out.close()
        dao.endTransaction()

    }


}
