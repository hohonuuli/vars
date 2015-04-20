def toolbox = new vars.ToolBox()
def dao = toolbox.toolBelt.annotationDAOFactory.newVideoArchiveSetDAO()
def cache = toolbox.toolBelt.persistenceCache

def vasc = dao.findAllByPlatformAndSequenceNumber("Doc Ricketts", 580)
vasc.each { vas -> 
    println(vas)
    def vac = vas.videoArchives
    vac.each { va ->
        def n = va.videoFrames.size() 
        println("\t$va : $n videofames")
    }
}