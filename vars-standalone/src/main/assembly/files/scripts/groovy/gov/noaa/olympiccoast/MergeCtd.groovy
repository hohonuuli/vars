package gov.noaa.olympiccoast

import vars.annotation.VideoFrame
import org.mbari.movie.Timecode
import vars.ToolBox
import org.mbari.math.CoallateFunction
import org.slf4j.LoggerFactory
import vars.annotation.VideoArchiveSetDAO

/**
 * Merges Seabird text data file with VARS data. 
 */
class MergeCtd {
    
    final String platform
    final int sequenceNumber
    final ctdData
    final toolBox = new ToolBox()
    private final log = LoggerFactory.getLogger(getClass())
    
    def offsetMillisecs = 10 * 1000

    final convertVideoFrameToMillisecs = { videoFrame ->
        videoFrame?.recordedDate?.time
    }

    final convertCtdDatumToMillisecs = { CtdDatum ctdDatum ->
        ctdDatum.date.time
    }
    
    MergeCtd(String platform, int sequenceNumber, File file, int year) {
           this.platform = platform
           this.sequenceNumber = sequenceNumber
           ctdData = CtdReader.read(file, year)
    }
    
    Map<VideoFrame, CtdDatum> apply() {
        def data = coallate()
        return update(data)
    }
    
    Map<VideoFrame, CtdDatum> coallate() {
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
        def coallatedData = CoallateFunction.coallate(videoFrames, convertVideoFrameToMillisecs,
                ctdData, convertCtdDatumToMillisecs, offsetMillisecs)
        log.info("Merge Resulted in ${coallatedData?.size()} records")
        return coallatedData
    }
    
    Map<VideoFrame, CtdDatum> update(Map data) {
        VideoArchiveSetDAO dao = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveSetDAO()
        def newData = [:]
        dao.startTransaction()
        data.each { VideoFrame videoFrame, CtdDatum ctdDatum ->
            videoFrame = dao.find(videoFrame)
            def physicalData = videoFrame.physicalData
            physicalData.salinity = ctdDatum.salinity
            physicalData.temperature = ctdDatum.temperature
            physicalData.oxygen = ctdDatum.oxygen
            newData[videoFrame] = ctdDatum
        }
        dao.endTransaction()
        dao.close()
        return newData
    }
}
