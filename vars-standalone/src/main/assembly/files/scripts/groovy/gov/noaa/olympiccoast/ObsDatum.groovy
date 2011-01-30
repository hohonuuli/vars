package gov.noaa.olympiccoast

/**
 * Obervational data
 *
 * "Logdate","dive","Speciesrpt","TimeCode","Camera_view"
 * "2008-07-12 17:41:11","1162","Anemone","17 41 11 10","vertical"
 *
 */
class ObsDatum {

    Date logDate
    Integer dive
    String conceptName
    String timecode
    String cameraView
    
    ObsDatum(logDate, dive, conceptName, timecode, cameraView) {
        this.logDate = logDate
        this.dive = dive
        this.conceptName = conceptName 
        this.timecode = timecode
        this.cameraView = cameraView
    }
    
    @Override
    public String toString() {
        return "${getClass().simpleName}[logDate=${logDate},conceptName=${conceptName}]"   
    }
}
