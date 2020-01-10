
/**
 * Fixes VicdeoArchive T0059-01-tripod that had part of the image dataset
 * loaded 3 times
 * @author Brian Schlining
 * @since 2012-01-20
 */

import vars.annotation.VideoArchiveDAO
import vars.shared.ui.GlobalStateLookup

def df = GlobalStateLookup.getUTCDateFormat()
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