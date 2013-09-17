package gov.noaa.olympiccoast

import vars.integration.MergeFunction
import vars.annotation.VideoFrame
import vars.integration.MergeFunction.MergeType
import org.mbari.math.CoallateFunction
import org.mbari.movie.Timecode
import java.text.SimpleDateFormat
import vars.ToolBox
import org.mbari.math.CoallateFunction
import org.slf4j.LoggerFactory
import vars.annotation.VideoArchiveSetDAO

class PumaLoader implements MergeFunction<Map<VideoFrame, PumaDatum>> {

    final String platform
    final int sequenceNumber
    private final offsetFrames = 10 * 29.97 // 10 seconds
    private final log = LoggerFactory.getLogger(getClass())
    private final toolBox = new ToolBox()
    private static final dateFormat = new SimpleDateFormat("HH:mm:ss:'00'")
    def videoFrameToFrames = { new Timecode(it.timecode, 29.97).frames }
    def pumaDatumToFrames = { it.elapsedTime * 29.97 }

    Map data = [:]
    def pumaData = []
    

    def PumaLoader(String platform, int sequenceNumber) {
        this.platform = platform
        this.sequenceNumber = sequenceNumber
    }

    void merge(File file) {
        read(file)
        apply(MergeType.PESSIMISTIC)
    }

    void read(File file) {
        data.clear()
        pumaData = PumaReader.read(file)
        pumaData.sort { it.date }
    }

    Map<VideoFrame, PumaDatum> apply(MergeType mergeType = null) {
        coallate(mergeType)
        update(data, mergeType)
        return data
    }

    void update(Map data, MergeType mergeType = null) {
        VideoArchiveSetDAO dao = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveSetDAO()
        def newData = [:]
        dao.startTransaction()
        data.each { VideoFrame videoFrame, PumaDatum PumaDatum ->
            videoFrame = dao.find(videoFrame)
            def physicalData = videoFrame.physicalData
            def cameraData = videoFrame.cameraData
            videoFrame.recordedDate = date
            physicalData.latitude = pumaDatum.latitude
            physicalData.longitude = pumaDatum.longitude
            physicalData.logDate = pumaDatum.date
            physicalData.altitude = pumaDatum.altitude
            cameraData.heading = pumaDatum.heading

            newData[videoFrame] = pumaDatum
        }
        dao.endTransaction()
        dao.close()
        data.clear()
        data = newData
    }

    Map<VideoFrame, PumaDatum> coallate(MergeType mergeType = null) {
        def dao = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveSetDAO()
        dao.startTransaction()
        def videoArchiveSet = dao.findAllByPlatformAndSequenceNumber(platform, sequenceNumber)
        if (!videoArchiveSet) {
            throw new RuntimeException("Did not find data for '${platform}' ${sequenceNumber}")   
        }
        def videoFrames = videoArchiveSet[0].videoFrames
        dao.endTransaction()
        dao.close()
        log.info("Found ${videoFrames?.size()} videoFrames")
        data = coallate(videoFrames, pumaData)
        return data
    }

    Map<VideoFrame, PumaDatum> coallate(List<VideoFrame> videoFrames, List<PumaDatum> pumaData) {
        return CoallateFunction.coallate(videoFrames, videoFrameToFrames, pumaData, pumaDatumToFrames)
    }
    

}
