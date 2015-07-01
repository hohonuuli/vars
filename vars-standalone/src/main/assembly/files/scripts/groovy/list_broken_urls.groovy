import vars.annotation.DatabaseUtility

/*
   This script fetches all frame grab URLS in VARS and checks that the link 
   is not broken. It then generates a report listing information needed to
   track down and fix or remove the broken link.
 */

// Brian Schlining
// 2014-12-04

def du = new DatabaseUtility()
def missingImages = du.listMissingStillImages().sort { it.toExternalForm() }
def dao = du.toolBox.toolBelt.annotationDAOFactory.newDAO()
def sql = """
| SELECT 
|  videoArchiveName,
|  TapeTimeCode, 
|  StillImageURL 
| FROM 
|  VideoArchive AS va LEFT JOIN
|  VideoFrame AS vf ON vf.VideoArchiveID_FK = va.id LEFT JOIN
|  CameraData AS cd ON cd.VideoFrameID_FK = vf.id
| WHERE
|  cd.StillImageURL = ?
""".stripMargin("|")
def query = dao.entityManager.createNativeQuery(sql)

println("# VARS Broken link report on ${new Date()}")
println("#VideoArchiveName\tTimeCode\tURL")
missingImages.each { url ->    
    query.setParameter(1, url.toExternalForm())
    def r = query.resultList[0]
    println("${r[0]}\t${r[1]}\t${r[2]}")
}