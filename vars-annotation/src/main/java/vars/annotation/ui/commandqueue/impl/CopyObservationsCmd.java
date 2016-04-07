package vars.annotation.ui.commandqueue.impl;

import org.bushe.swing.event.EventBus;
import org.mbari.vcr4j.VideoIndex;
import org.mbari.vcr4j.time.Timecode;
import vars.DAO;
import vars.annotation.AnnotationFactory;
import vars.annotation.AnnotationPersistenceService;
import vars.annotation.Association;
import vars.annotation.CameraDirections;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;
import vars.annotation.VideoFrame;
import vars.annotation.VideoFrameDAO;
import vars.annotation.ui.StateLookup;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.eventbus.ObservationsAddedEvent;
import vars.annotation.ui.eventbus.ObservationsRemovedEvent;
import vars.annotation.ui.eventbus.ObservationsSelectedEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

/**
 * @author Brian Schlining
 * @since 2011-10-17
 */
public class CopyObservationsCmd implements Command {

    /** These are the observations that will be copied */
    private final Collection<Observation> sourceObservations;
    private final String videoArchiveName;
    private final VideoIndex videoIndex;
    private final String user;
    private final Collection<Observation> copyObservations = Collections.synchronizedCollection(new ArrayList<Observation>());
    private final boolean selectAddedObservations;

    public CopyObservationsCmd(String videoArchiveName, VideoIndex videoIndex, String user, Collection<Observation> sourceObservations, boolean selectObservations) {
        if (videoArchiveName == null || videoIndex == null || sourceObservations == null) {
            throw new IllegalArgumentException("Command arguments can not be null");
        }
        this.sourceObservations = new ArrayList<Observation>(sourceObservations);
        this.videoArchiveName = videoArchiveName;
        this.videoIndex = videoIndex;
        this.user = user;
        this.selectAddedObservations = selectObservations;
    }

    @Override
    public void apply(ToolBelt toolBelt) {
        AnnotationFactory annotationFactory = toolBelt.getAnnotationFactory();
        /*
         * DAOTX See if a VideoFrame with the given time code already exists
         */
        AnnotationPersistenceService annotationPersistenceService = toolBelt.getAnnotationPersistenceService();
        DAO dao = toolBelt.getAnnotationDAOFactory().newDAO();
        VideoArchiveDAO videoArchiveDAO = toolBelt.getAnnotationDAOFactory().newVideoArchiveDAO(dao.getEntityManager());
        VideoFrameDAO videoFrameDAO = toolBelt.getAnnotationDAOFactory().newVideoFrameDAO(dao.getEntityManager());
        dao.startTransaction();
        VideoArchive videoArchive = videoArchiveDAO.findByName(videoArchiveName);

        if (videoArchive != null) {

            Timecode timecode = videoIndex.getTimecode().get(); // HACK, this needs to be changed in future when index is added

            Long id = annotationPersistenceService.findTimeCodeByVideoArchiveName(timecode.toString(), videoArchiveName);
            VideoFrame videoFrame = null;
            if (id != null) {
                videoFrame = videoFrameDAO.findByPrimaryKey(id);
            }

            if (videoFrame == null) {
                videoFrame = annotationFactory.newVideoFrame();
                final VideoFrame fVideoFrame = videoFrame;
                videoIndex.getTimecode().ifPresent(tc -> fVideoFrame.setTimecode(tc.toString()));
                videoIndex.getTimestamp().ifPresent(ts -> fVideoFrame.setRecordedDate(Date.from(ts)));
                // TODO add elapsedTime when it's finally added to database
                CameraDirections cameraDirections = StateLookup.getCameraDirection();
                videoFrame.getCameraData().setDirection(cameraDirections.getDirection());
                videoArchive.addVideoFrame(videoFrame);
                dao.persist(videoFrame);
            }

            Date date = new Date();

            synchronized (copyObservations) {
                for (Observation observation : sourceObservations) {
                    Observation copyObservation = annotationFactory.newObservation();
                    copyObservation.setObserver(user);
                    copyObservation.setObservationDate(date);
                    copyObservation.setConceptName(observation.getConceptName());
                    videoFrame.addObservation(copyObservation);
                    dao.persist(copyObservation);

                    // Deep copy
                    for (Association association : new ArrayList<Association>(observation.getAssociations())) {
                        Association copyAssociation = annotationFactory.newAssociation();
                        copyAssociation.setLinkName(association.getLinkName());
                        copyAssociation.setLinkValue(association.getLinkValue());
                        copyAssociation.setToConcept(association.getToConcept());
                        copyObservation.addAssociation(copyAssociation);
                    }

                    copyObservations.add(copyObservation);
                }
            }

            dao.endTransaction();

            EventBus.publish(new ObservationsAddedEvent(null, copyObservations));
            if (selectAddedObservations) {
                EventBus.publish(new ObservationsSelectedEvent(null, copyObservations));
            }
        }
    }

    @Override
    public void unapply(ToolBelt toolBelt) {
        Collection<Observation> droppedObservations = new ArrayList<Observation>(copyObservations);
        synchronized (copyObservations) {
            DAO dao = toolBelt.getAnnotationDAOFactory().newDAO();
            dao.startTransaction();

            for (Observation obs : copyObservations) {
                Observation observation = dao.find(obs);
                if (observation != null) {
                    droppedObservations.add(obs);
                    VideoFrame videoFrame = observation.getVideoFrame();
                    videoFrame.removeObservation(observation);
                    dao.remove(observation);
                    if (videoFrame.getObservations().size() == 0) {
                        dao.remove(videoFrame);
                    }
                }
            }

            dao.endTransaction();
            copyObservations.clear();
        }
        EventBus.publish(new ObservationsRemovedEvent(null, droppedObservations));
    }

    @Override
    public String getDescription() {
        return "Copy " + sourceObservations.size() + " observations to " + videoArchiveName + " at " +
                videoIndex.getTimecode().map(Timecode::toString).orElse("NO TIMECODE");
    }
}
