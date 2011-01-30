package gov.noaa.olympiccoast

/**
 * Year	Dive #	Site	Date	Clip_Name	Transect	Hab code	
 Survey activity	Start time	End time	Comments
 
 2008	1162	10	7/12/2008	0001HT	1	XX	tcx	15:44:00	15:44:41	start tran 1								
 */
class HabitatDatum {
    
    Integer diveNumber
    String site
    String clipName
    String transect
    String habitatCode
    String surveyActivity
    Date startDate
    Date endDate
    String comments
    
    HabitatDatum(Integer diveNumber, site, clipName, transect, habitatCode, 
            surveyActivity, Date startDate, Date endDate, comments) {
        this.diveNumber = diveNumber
        this.site = site
        this.clipName = clipName
        this.transect = transect
        this.habitatCode = habitatCode
        this.surveyActivity = surveyActivity
        this.startDate = startDate
        this.endDate = endDate
        this.comments = comments
    }
    
    @Override
    public String toString() {
        return "${getClass().simpleName}[start=${startDate},end=${endDate}]"   
    }
    
}
