import vars.ToolBox
import java.text.SimpleDateFormat

/**
 * 
 * @author Brian Schlining
 * @since 2011-12-06
 */
def toolBox = new ToolBox()
println "--- Executing Query"
def dao = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveSetDAO()
def vasc = dao.findAllWithMultipleCameraDeployments().sort { it.trackingNumber }
dao.close()
println "--- Query completed"
println "--- VideoArchiveSets with Multiple 'Dives':"
def df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
df.timeZone = TimeZone.getTimeZone('UTC')
vasc.each { vas ->
   if (vas.startDate && vas.endDate) {
       println "${vas.platformName} - ${vas.trackingNumber} [${df.format(vas.startDate)} to ${df.format(vas.endDate)}]"
   }
   else {
       println "${vas.platformName} - ${vas.trackingNumber} [MISSING DEPLOYMENT DATES]"
   }

   def cdc = vas.cameraDeployments.sort { it.sequenceNumber }
   cdc.each { cd ->
       if (cd.startDate && cd.endDate) {
           println "\t#${cd.sequenceNumber} [${df.format(cd.startDate)} to ${df.format(cd.endDate)}]"
       }
       else {
           println "\t#${cd.sequenceNumber} [MISSING DEPLOYMENT DATES]"
       }
   }
}