
/**
 * Addresses https://oceana.mbari.org/jira/browse/VARS-461
 * @author Brian Schlining
 * @since 2012-01-20
 */

import vars.shared.ui.GlobalLookup
import vars.annotation.VideoArchiveSetDAO
import org.mbari.vars.integration.MergeEXPDAnnotations
import org.slf4j.LoggerFactory
import vars.integration.MergeFunction

def df = GlobalLookup.DATE_FORMAT_UTC
def log = LoggerFactory.getLogger("bugfix461")

def toolbox = new vars.ToolBox()
def annotationDAOFactory = toolbox.toolBelt.annotationDAOFactory

VideoArchiveSetDAO dao = annotationDAOFactory.newVideoArchiveSetDAO()
dao.startTransaction()
def needsRemerge = dao.findAllBetweenDates(new Date(0L), df.parse('2005-05-31 00:00:00'))
log.info("Found {} videoArchiveSets to remerge using a PESSIMISTIC merge", needsRemerge.size())
needsRemerge.each { videoArchiveSet ->
    try {
        def videoArchives = videoArchiveSet.videoArchives
        def platform = videoArchiveSet.platformName
        def sequenceNumber = videoArchiveSet?.cameraDeployments?.iterator()?.next()?.sequenceNumber
        if (platform && sequenceNumber) {
            def isHD = videoArchives.findAll { it.name.endsWith('HD') } isEmpty()
            def fn = new MergeEXPDAnnotations(platform, sequenceNumber, isHD)
            fn.apply(MergeFunction.MergeType.PESSIMISTIC)
        }
    }
    catch (Exception e) {
        log.info("Failed to merge {}", videoArchiveSet, e)
    }

}