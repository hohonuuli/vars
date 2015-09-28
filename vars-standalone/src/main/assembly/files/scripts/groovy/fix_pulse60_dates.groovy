import vars.ToolBox

import java.text.SimpleDateFormat

/**
 * 
 * @author Brian Schlining
 * @since 2013-01-31
 */
if (args.size() != 1) {
    println("""
    | Script that updates the recordedDates for rover nd tripod collections that have the form of
    | StaM_6006_Rover_120613_22_56_30.png (or .jpg)
    |
    | Usage:
    |   gsh fix_pulse60_dates <videoArchiveName>
    |
    | Arguments:
    |    videoArchiveName: The name of the VideoArchive to update the recorded dates
    """.stripMargin('|'))
    return
}

def videoArchiveName = args[0]

def toolBox = new ToolBox()
def dao = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveDAO()
dao.startTransaction()
def videoArchive = dao.findByName(videoArchiveName)
if (videoArchive == null) {
    println("Unable to find '${videoArchiveName}' in the database")
    dao.close()
    return
}

def dateFormat = new SimpleDateFormat('yyMMdd_HH_mm_ss')
def videoFrames = videoArchive.videoFrames
videoFrames.each { vf ->
    def url = new URL(vf.cameraData.imageReference)
    def img = url.toExternalForm().split("/")[-1]
    def date = dateFormat.parse(img[16..-4])
    vf.recordedDate = date
}
dao.endTransaction()
dao.close()




