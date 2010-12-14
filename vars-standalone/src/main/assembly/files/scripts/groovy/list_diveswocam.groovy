import vars.ToolBox
/**
 * List dives without camera data
 * @author Brian Schlining
 * @since 2010-12-14
 */

def toolBox = new ToolBox()
def diveDao = toolBox.daoFactory.newDiveDAO()
def camDao = toolBox.daoFactory.newCameraDatumDAO()
def dives = diveDao.findAll()
println("rov\tdiveNumber\tstartDate\tendDate")
dives = dives.sort { it.startDate }
dives.each { dive ->
    def cameraData = camDao.findAllBetweenDates(dive.rovName, dive.startDate, dive.endDate)
    if (cameraData.size() < 1) {
        println("${dive.rovName}\t${dive.diveNumber}\t${dive.startDate}\t${dive.endDate}")
    }
}