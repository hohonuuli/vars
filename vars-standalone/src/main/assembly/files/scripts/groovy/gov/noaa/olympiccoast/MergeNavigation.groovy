package gov.noaa.olympiccoast

import vars.integration.MergeFunction
import vars.annotation.VideoFrame
import vars.integration.MergeFunction.MergeType
import org.mbari.movie.Timecode
import java.text.SimpleDateFormat
import vars.ToolBox
import org.slf4j.LoggerFactory
import vars.annotation.VideoArchiveSetDAO

/**
 * Merges OCNMS standard navigation files with VARS data. The OCNMS files should have a header row
 * and data formatted as follows:
 *
 * "Dive","Depth","ROV_Latitude","ROV_Longitude","Logdate","Pitch","Roll","Heading","Altimeter","Speed"
 * 1157,297.777777,48.403987,-124.981079,"2008-07-09 09:13:20",-0.477777,3.700000,215.600000,0.600000,0
 *
 * Use as:
 * {@code
 * platform = 'Ropos'
 * sequenceNumber = 1157
 * dateFormat = new SimpleDateFormat('yyyy-MM-dd hh:mm:ss') 
 * initialDate = dateFormat.parse(
 * m = new MergeNavigation(platform, sequenceNumber, initialDate)
 * m.merge(navigationFile)
 * }
 * @author Brian Schlining
 * @since 2011-01-24
 */
class MergeNavigation implements MergeFunction<Map<VideoFrame, NavigationDatum>> {

    private final log = LoggerFactory.getLogger(getClass())
    final String platform
    final int sequenceNumber
    final Date initialDate
    static final dateFormat = new SimpleDateFormat("HH:mm:ss:'00'")
    static final dateFormat1 = new SimpleDateFormat("HH:mm:ss")
    static final dateFormat2 = new SimpleDateFormat("yyyy-MM-dd")
    static final dateFormat3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    Map data = [:]
    def navigationData = []
    private final toolBox = new ToolBox()

    def offsetFrames = 10 * 29.97 // 10 seconds

    def MergeNavigation(String platform, int sequenceNumber) {
        this.platform = platform
        this.sequenceNumber = sequenceNumber
    }

    void merge(File file) {
        read(file)
        apply(MergeType.PRAGMATIC)
    }

    /**
     * Read the source data file
     * @param file
     * @return
     */
    void read(File file) {
        data.clear();
        navigationData = NavigationReader.read(file)
        navigationData = navigationData.findAll { it.dive = sequenceNumber }
        navigationData.sort { it.date }
    }
    
    Map<VideoFrame, NavigationDatum> apply(MergeType mergeType = null) {
        coallate(mergeType)
        update(data, mergeType)
        return data
    }

    void update(Map data, MergeType mergeType = null) {
        VideoArchiveSetDAO dao = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveSetDAO()
        def newData = [:]
        dao.startTransaction()

        data.each { VideoFrame videoFrame, NavigationDatum navigationDatum ->

            def dateString = dateFormat2.format(navigationDatum.date)
            def timeString = videoFrame.timecode[0..7]
            def date = dateFormat3.parse("${dateString} ${timeString}")

            if (navigationDatum.dive == sequenceNumber) {
                videoFrame = dao.find(videoFrame)
                def physicalData = videoFrame.physicalData
                def cameraData = videoFrame.cameraData
                videoFrame.recordedDate = date
                physicalData.depth = navigationDatum.depth
                physicalData.latitude = navigationDatum.latitude
                physicalData.longitude = navigationDatum.longitude
                physicalData.logDate = navigationDatum.date
                cameraData.pitch = navigationDatum.pitch
                cameraData.roll = navigationDatum.roll
                cameraData.heading = navigationDatum.heading

                // TODO add speed and altitude
                newData[videoFrame] = navigationDatum

            }
            else {
                log.info("NavigationDatum.dive = ${navigationDatum.dive}; Expected ${sequenceNumber}. Skipping this record")
            }
        }

        dao.endTransaction()
        dao.close()
        data.clear()
        data = newData
        //To change body of implemented methods use File | Settings | File Templates.
    }

    Map<VideoFrame, NavigationDatum> coallate(MergeType mergeType = null) {
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
        data = coallate(videoFrames, navigationData)
        return data
    }
    
    Map<VideoFrame, NavigationDatum> coallate(List<VideoFrame> videoFrames, List<NavigationDatum> navData) {

        /* videoFrames.each {
            println("${it.recordedDate} = ${convertVideoFrameToMillisecs(it)}")   
        } */
        
//        def coallatedData = CollateFunction.coallate(videoFrames, convertVideoFrameToMillisecs,
//                navigationData, convertNavigationDatumToMillisecs, offsetMillisecs)

        // Convert navigation time to frames and get nearest to videoFrame
        def navFrames = navData.collect { new Timecode(dateFormat.format(it.date)).frames }
        //def vidFrames = videoFrames.collect { new Timecode(it.timecode).frames }
        //NavigationDatum.metaClass.frames = 0D
        //VideoFrame.metaClass.frames = 0D
        //navData.each { it.frames = new Timecode(dateFormat.format(it.date)).frames }
        //videoFrames.each { it.frames = new Timecode(it.timecode).frames }
        def coallatedData = [:]
        videoFrames.each { VideoFrame videoFrame ->
            def frames = new Timecode(videoFrame.timecode).frames
            def diffFrames = navFrames.collect { Math.abs(it - frames) }
            def nearest = diffFrames.min()
            def idx = diffFrames.findIndexOf { it == nearest }
            coallatedData[videoFrame] = navData[idx]
        }

        log.info("Merge Resulted in ${coallatedData?.size()} records")
        return coallatedData
    }
}
