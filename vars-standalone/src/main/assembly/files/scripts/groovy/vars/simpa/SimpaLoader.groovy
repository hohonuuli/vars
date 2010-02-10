package vars.simpa

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Arrays
import java.util.Date
import java.util.List
import java.util.TimeZone
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import vars.ToolBox
import vars.annotation.ui.PersistenceController
import org.mbari.expd.actions.CoallateByDateFunction

class SimpaLoader {
    
    private final toolBox;
    private final dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final offsetSecs = 7.5D;
    private final coallateFunction = new CoallateByDateFunction()
    private final targetRootDirectory
    private final targetRootUrl



    /**
     * @param targetDir is the root of the directory to write images into
     * @param targetUrl is the mapping of the targetDirectory onto a web server 
     */
    def SimpaLoader(String targetDir, String targetUrl) {
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        toolBox = new ToolBox()
    }
    
    def read(URL url) {
        def reader = new BufferedReader(new InputStreamReader(url.openStream()));
        def lineCount = 0
        def line = null
        def data = []
        
        while((line = reader.readLine())) {
            data << parse(line)
        }
        
        reader.close()
        return data
    }
    
    def load(List<SimpaDatum> simpaData, String platform, Integer sequenceNumber) {
        
        // Need the dates for simpaData
        def simpaDates = simpaData.collect { it.date }
        
        // Fetch CTD data to get timecode
        def dive = toolBox.daoFactory.newDiveDAO().findByPlatformAndDiveNumber(platform, sequenceNumber)
        def uberData = toolBox.daoFactory.newUberDatumDAO().fetchData(dive, true, offsetSecs)
        def dataMap = coallateFunction.apply(simpaDates, uberData, offsetSecs * 1000)
        
        def annotationFactory = toolBox.toolBelt.annotationFactory
        def videoArchiveName = PersistenceController.makeVideoArchiveName(platform, sequenceNumber, 1, "-coolpix")
        try {
            def cameraDataDAO = toolBox.daoFactory.newCameraDatumDAO()
            def conceptDAO = toolBox.toolBelt.knowledgebaseDAOFactory.newConceptDAO()
            conceptDAO.startTransaction()
            def conceptNameAsString = conceptDAO.findRoot().primaryConceptName.name
            def videoArchiveDAO = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveDAO()
            videoArchiveDAO.startTransaction()
            
            def videoArchive = videoArchiveDAO.findOrCreateByParameters(platform, sequenceNumber, videoArchiveName)
            
            simpaData.each { simpaDatum ->
                def uberDatum = dataMap[simpaDatum.date]
                def cameraDatum = uberDatum?.cameraDatum
                if (cameraDatum) {
                    def videoFrame = videoArchive.findVideoFrameByTimeCode(cameraDatum.timecode)
                    if (!videoFrame) {

                        def videoTime = cameraDataDAO.interpolateTimecodeToDate(platform, simpaDatum.date, offsetSecs * 1000, 29.97)
                        if (videoTime) {
                            videoFrame = annotationFactory.newVideoFrame()
                            videoFrame.timecode = videoTime.timecode
                            videoFrame.recordedDate = simpaDatum.date
                            videoArchive.addVideoFrame(videoFrame)
                            videoArchiveDAO.persist(videoFrame)
                        }

                        if (videoFrame.cameraData.imageReference) {
                            log.warn("${videoFrame} already exists and contains an image reference. Not modifying it")
                        }
                        else {
                            videoFrame.cameraData.setImageReference(url.toExternalForm())
                            if (videoFrame.observations.size() == 0) {
                                def observation = annotationFactory.newObservation()
                                observation.conceptName = conceptNameAsString
                                observation.observer = getClass().simpleName
                                observation.observationDate = new Date()
                                videoFrame.addObservation(observation)
                                videoArchiveDAO.persist(observation)
                            }
                        }
                    }
                    
                }
                
                
                
            }
        }
        catch (Exception e) {
            
        }
        
    }
    
    def parse(String line) {
        /*
         * Split the line by white space. Drop the empty spaces in
         * the resulting array.
         */
        def simpaDatum = new SimpaDatum()
        def values = line.split('\t');
        if (values.size() == 11) {
            def tileIndex = Double.parseDouble(values[0])
            def date = dateFormat.parse("${values[1]} ${values[2]}")
            def x = Double.parseDouble(values[4]); // Circle swaps his axes (he uses +x = forward, +y = right)
            def y = Double.parseDouble(values[3]);
            def z = Double.parseDouble(values[5]);
            def roll = Double.parseDouble(values[6]);
            def pitch = Double.parseDouble(values[7]);
            def heading = Double.parseDouble(values[8]);
            def width = Double.parseDouble(values[10]);    // Swap width and height from file too.
            def height = Double.parseDouble(values[9]);
            simpaDatum = new SimpaDatum(tileIndex, date, x, y, z, roll, pitch, heading, 
                    width, height)
        }   
        return simpaDatum;
        
    }
    
    
    private class SimpaDatum {
        int tileIndex
        Date date
        double x, y, z, roll, pitch, heading, width, height
        
        def SimpaDatum() { }
        
        def SimpaDatum(tileIndex, date, x, y, z, roll, pitch, heading, width, height) {
            this.x = x
            this.y = y
            this.z = z
            this.roll = roll
            this.pitch = pitch
            this.heading = heading
            this.width = width
            this.height = height
        }
        
            
    }

}

