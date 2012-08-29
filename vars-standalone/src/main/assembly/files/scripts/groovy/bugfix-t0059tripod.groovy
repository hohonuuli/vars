
/**
 * Fixes VicdeoArchive T0059-01-tripod that had part of the image dataset
 * loaded 3 times
 * @author Brian Schlining
 * @since 2012-01-20
 */

import vars.shared.ui.GlobalLookup
import vars.annotation.VideoArchiveDAO
import org.mbari.vars.integration.MergeEXPDAnnotations
import org.slf4j.LoggerFactory
import vars.integration.MergeFunction

def log = LoggerFactory.getLogger("bugfix-t0059tripod")
def df = GlobalLookup.DATE_FORMAT_UTC
def date = df.parse('2012-06-26 23:33:00')

def toolbox = new vars.ToolBox()
def annotationDAOFactory = toolbox.toolBelt.annotationDAOFactory

VideoArchiveDAO dao = annotationDAOFactory.newVideoArchiveDAO()
def obsDao = annotationDAOFactory.newObservationDAO(dao.entityManager)
dao.startTransaction()
def videoArchive = dao.findByName('T0059-01-tripod')
videoArchive.videoFrames.each { videoFrame ->
    def observations = videoFrame.observations
    if (observations.size() >= 3) {
        observations.each { obs ->
            if (obs.observer.startsWith('TripodLoader') && obs.observationDate.before(date)) {
                //videoFrame.removeObservation(obs)
                obsDao.remove(obs)
                println("${videoFrame.timecode} :: deleteing ${obs} ")
            }
        }
    }


}
dao.endTransaction()
dao.close()