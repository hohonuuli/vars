package gov.noaa.olympiccoast

import org.slf4j.LoggerFactory
import vars.ToolBox
import vars.integration.MergeFunction
import java.text.SimpleDateFormat
import java.text.DecimalFormat
import java.text.NumberFormat
import vars.annotation.ui.PersistenceController
import vars.LinkBean
import vars.ILink
import vars.annotation.VideoFrame
import vars.annotation.VideoArchive

class HabitatLoader {
    
    private toolBox = new ToolBox()
    private annotationFactory = toolBox.toolBelt.annotationFactory
    private videoArchiveDAO
    private final log = LoggerFactory.getLogger(getClass())
    private final timecodeFormat
    private observer = getClass().simpleName
    private final NumberFormat f0123 = new DecimalFormat("0000");
    private final habitatCodes = [
        "B" : "boulder",
        "C" : "cobble",
        "D" : "faulted",
        "E" : "bedrock",
        "F" : "sediment fans",
        "G" : "gravel",
        "H" : "mounds",
        "I" : "interface",
        "J" : "crevices",
        "M" : "mud",
        "P" : "pebbles",
        "Q" : "coquina",
        "R" : "rubble",
        "S" : "sand",
        "T" : "terrace",
        "W" : "wall"
    ]
    private final surveyActivities = [
        "pa" : new LinkBean("survey activity", ILink.VALUE_SELF, "pause over point of interest"),
        "tcx" : new LinkBean("comment", ILink.VALUE_SELF, "missing timecode"),
        "epi" : new LinkBean("sampled-by", "equipment", ILink.VALUE_NIL),
        "vo" : new LinkBean("image-quality", ILink.VALUE_SELF, "video partially obstructed"),
        "unk" : new LinkBean("survey activity", ILink.VALUE_SELF, "unknown activity"),
        "na" : new LinkBean("survey activity", ILink.VALUE_SELF, "ROV guage check"),
        "np" : new LinkBean("survey activity", ILink.VALUE_SELF, "not primary view"),
    ]
    private final primaryLink = new LinkBean("primary substrate", ILink.VALUE_SELF, ILink.VALUE_NIL)
    private final secondayLink = new LinkBean("secondary substrate", ILink.VALUE_SELF, ILink.VALUE_NIL)
    
    HabitatLoader() {
        timecodeFormat = new SimpleDateFormat("HH:mm:ss:'00'")
        timecodeFormat.timeZone = TimeZone.getTimeZone('UTC')
    }
 
    void load(List<HabitatDatum> habitatData, String platform) {
        
        videoArchiveDAO = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveDAO()
        
        // -- Get the dive number
        def dives = habitatData.collect { it.diveNumber } as Set
        if (dives.size() != 1) {
            throw IllegalArgumentException("You attempted to load data from more than 1 dive. That's not allowed!!")   
        }
        def dive = dives.iterator().next()
        def prefix = "${platform[0].toUpperCase()}${f0123.format(dive)}"
        
        // -- Get the video archive names
        def clipNames = habitatData.collect { it.clipName } as Set
        def videoArchiveNames = clipNames.collect { "${prefix}-${it}" }
        
        // Processes each videoArchiveName 
        videoArchiveNames.each { videoArchiveName ->
            def data = habitatData.findAll { "${prefix}-${it.clipName}" == videoArchiveName }
            videoArchiveDAO.startTransaction()
            def videoArchive = videoArchiveDAO.findOrCreateByParameters(platform, dive, videoArchiveName)
            data.each { habitatDatum ->
                def startTimecode = timecodeFormat.format(habitatDatum.startDate)
                def startVideoFrame = findVideoFrame(videoArchive, startTimecode, habitatDatum)
                buildVideoFrame(startVideoFrame, habitatDatum)
                //def endTimecode = timecodeFormat.format(habitatDatum.endDate)
                //def endVideoFrame = findVideoFrame(videoArchive, timecode)
            }
            videoArchiveDAO.endTransaction()
        }
        
        videoArchiveDAO.close()

    }
    
    /**
     * Find a video frame
     */
    private findVideoFrame(VideoArchive videoArchive, String timecode, HabitatDatum habitatDatum) {
        def videoFrame = videoArchive.findVideoFrameByTimeCode(timecode)
        if (!videoFrame) {
            videoFrame = annotationFactory.newVideoFrame()
            videoFrame.timecode = timecode
            videoFrame.recordedDate = habitatDatum.startDate
            videoArchive.addVideoFrame(videoFrame)
            videoArchiveDAO.persist(videoFrame)
        }
        
        return videoFrame
    }
    
    private buildVideoFrame(VideoFrame videoFrame, HabitatDatum habitatDatum) {

        def date = new Date()

        // -- Generate an observation for each habitatDatum
        def obs1 = annotationFactory.newObservation()
        obs1.observer = observer
        obs1.observationDate = date
        videoFrame.addObservation(obs1)
        videoArchiveDAO.persist(obs1)

        def habitatCode = habitatDatum.habitatCode
        def primaryCode = habitatCodes[habitatCode[0]]
        if (primaryCode) {
            obs1.conceptName = primaryCode
            // Add primary association
            def pa = annotationFactory.newAssociation(primaryLink)
            obs1.addAssociation(pa)
            videoArchiveDAO.persist(pa)

            // If there's a primary code AND a comment add the comment as
            // as separate observation
            if (habitatDatum.comments) {
                def obs0 = annotationFactory.newObservation()
                obs0.conceptName = habitatDatum.comments
                obs0.observer = observer
                obs0.observationDate = date
                videoFrame.addObservation(obs0)
                videoArchiveDAO.persist(obs0)
            }

            // -- Possibly add a secondary habitat type
            def secondaryCode = habitatCodes[habitatCode[1]]
            if (secondaryCode) {
                def obs2 = annotationFactory.newObservation()
                obs2.conceptName = secondaryCode
                obs2.observer = observer
                obs2.observationDate = date
                videoFrame.addObservation(obs2)
                videoArchiveDAO.persist(obs2)
                // Add secondary association   
                def sa = annotationFactory.newAssociation(secondayLink)
                obs2.addAssociation(sa)
                videoArchiveDAO.persist(sa)
            }

        }
        else {
            // If there's no primaryCode use comments as the observation
            obs1.conceptName = habitatDatum.comments ?: "object"
        }

        // -- Generate associations if needed
        if (habitatDatum.surveyActivity) {
            def link = surveyActivities[habitatDatum.surveyActivity]
            if (link) {
                def association = annotationFactory.newAssociation(link)
                obs1.addAssociation(association)
                videoArchiveDAO.persist(association)
            }
        }
    }
    
    
}
