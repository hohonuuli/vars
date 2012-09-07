package uk.ac.ox.zoo

import vars.annotation.VideoFrame
import vars.ToolBox
import org.slf4j.LoggerFactory
import org.mbari.math.CoallateFunction
import vars.annotation.VideoArchiveSetDAO

/**
 *
 * @author Brian Schlining
 * @since 2012-09-06
 */
class MergeData {

    final String videoArchiveName
    final File file
    final toolBox = new ToolBox()
    private final log = LoggerFactory.getLogger(getClass())

    def offsetMillisecs = 10 * 1000

    final convertVideoFrameToMillisecs = { videoFrame ->
        videoFrame?.recordedDate?.time
    }

    final convertCsvDatumToMillisecs = { CSVDatum datum ->
        datum.recordedDate.time
    }

    MergeData(String videoArchiveName, File file) {
        this.videoArchiveName = videoArchiveName
        this.file = file
    }

    Map<VideoFrame, CSVDatum> apply() {
        def data = coallate()
        return update(data)
    }

    Map<VideoFrame, CSVDatum> coallate() {
        def dao = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveDAO()
        dao.startTransaction()
        def videoArchive = dao.findByName(videoArchiveName)
        if (!videoArchive) {
            throw new RuntimeException("Unable to find ${videoArchiveName} in the VARS database")
        }
        def videoFrames = videoArchive.videoFrames
        dao.endTransaction()
        dao.close()
        log.info("Found ${videoFrames?.size()} videoFrames")
        def coallatedData = CoallateFunction.coallate(videoFrames, convertVideoFrameToMillisecs,
                ctdData, convertCsvDatumToMillisecs, offsetMillisecs)
        log.info("Merge Resulted in ${coallatedData?.size()} records")
        return coallatedData
    }

    Map<VideoFrame, CSVDatum> update(Map data) {
        VideoArchiveSetDAO dao = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveSetDAO()
        def newData = [:]
        dao.startTransaction()
        data.each { VideoFrame videoFrame, CSVDatum datum ->
            videoFrame = dao.find(videoFrame)
            def physicalData = videoFrame.physicalData
            physicalData.salinity = datum.salinity
            physicalData.temperature = datum.temperature
            physicalData.depth = datum.depth
            physicalData.latitude = datum.latitude
            physicalData.longitude = datum.longitude
            physicalData.logDate = datum.logDate
            newData[videoFrame] = datum

        }
        dao.endTransaction()
        dao.close()
        return newData
    }

}
