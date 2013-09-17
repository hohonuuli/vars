package uk.ac.ox.zoo

import vars.annotation.VideoFrame
import vars.ToolBox
import org.slf4j.LoggerFactory
import org.mbari.math.CoallateFunction
import vars.annotation.VideoArchiveSetDAO
import vars.query.ui.Lookup

/**
 *
 * @author Brian Schlining
 * @since 2012-09-06
 */
class MergeData {

    final String videoArchiveName
    final File file
    final csvData
    final toolBox = new ToolBox()
    private final log = LoggerFactory.getLogger(getClass())

    def offsetMillisecs = 10 * 1000

    def dateFormat = Lookup.DATE_FORMAT_UTC

    final convertVideoFrameToMillisecs = { videoFrame ->
        videoFrame?.recordedDate?.time
    }

    final convertCsvDatumToMillisecs = { CSVDatum datum ->
        datum?.recordedDate?.time
    }

    MergeData(String videoArchiveName, File file) {
        this.videoArchiveName = videoArchiveName
        this.file = file
        csvData = CSVReader.read(file)
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
        def coallatedData = [:]
        try {
            coallatedData = CoallateFunction.coallate(videoFrames, convertVideoFrameToMillisecs,
                csvData, convertCsvDatumToMillisecs, offsetMillisecs)
        }
        catch (Exception e) {
            def annoDates = videoFrames.collect {it.recordedDate} findAll { it != null} sort()
            def fileDates = csvData.collect {it.recordedDate} findAll { it != null}  sort()
            log.warn("""\
                Unable to match dates between the annotations and the data in the CSV file.

                Annotations:
                    start date:\t${dateFormat.format(annoDates[0])}
                    end date:\t${dateFormat.format(annoDates[-1])}

                $file
                    start date:\t${dateFormat.format(fileDates[0])}
                    end date:\t${dateFormat.format(fileDates[-1])}
            """.stripIndent(), e)
        }
        return coallatedData
    }

    Map<VideoFrame, CSVDatum> update(Map data) {
        VideoArchiveSetDAO dao = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveSetDAO()
        def newData = [:]
        dao.startTransaction()
        data.each { VideoFrame videoFrame, CSVDatum datum ->

            videoFrame = dao.find(videoFrame)
            
            def physicalData = videoFrame.physicalData
            physicalData.altitude = datum.altitude
            physicalData.depth = datum.depth
            physicalData.latitude = datum.latitude
            physicalData.logDate = datum.logDate
            physicalData.longitude = datum.longitude
            physicalData.salinity = datum.salinity
            physicalData.temperature = datum.temperature
            
            def cameraData = videoFrame.cameraData
            cameraData.x = datum.xVelocity
            cameraData.y = datum.yVelocity
            cameraData.z = datum.zVelocity

            newData[videoFrame] = datum

        }
        dao.endTransaction()
        dao.close()
        return newData
    }

}
