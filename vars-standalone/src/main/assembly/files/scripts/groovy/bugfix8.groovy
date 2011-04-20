import org.mbari.movie.Timecode


final toolBox = new vars.ToolBox()

def dao = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveSetDAO()

def allVas = dao.findAll()

def drVas = allVas.findAllByPlatform("Doc Ricketts")

def max = 7 * 60 * 60 * 1000 // 7 hours as millisec
def maxFrames = new Timecode("06:50:00:00").frames
drVas.each { vas ->
    dao.startTransaction()
    vas = dao.find(vas) // bring into transaction
    def diveNumber = vas.cameraDeployments.iterator().next().sequenceNumber
    def videoFrames = vas.videoFrames.sort { it.timecode }
    def d0 = new Date(0)
    videoFrames.each { vf ->
        def frames = new Timecode(vf.timecode).frames
        def dt = vf.recordedDate.time - d0
        if (dt < 0) {
            d0 = vf.recordedDate
        }
        if (frames > maxFrames && Math.abs(dt) > max) {
            println("Setting recordeDate for #${diveNumber} @ ${vf.timecode} to null" )
            vf.recordedDate = null
        }
    }
    dao.endTransaction()
}
