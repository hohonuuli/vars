package vars.annotation.ui.commandqueue.impl;

import org.bushe.swing.event.EventBus;
import vars.DAO;
import vars.UserAccount;
import vars.annotation.AnnotationFactory;
import vars.annotation.Association;
import vars.annotation.CameraDirections;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;
import vars.annotation.VideoFrame;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.eventbus.ObservationsAddedEvent;
import vars.annotation.ui.eventbus.ObservationsRemovedEvent;
import vars.shared.ui.video.VideoTime;

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
    private final VideoTime videoTime;
    private final String user;
    private final Collection<Observation> copyObservations = Collections.synchronizedCollection(new ArrayList<Observation>());

    public CopyObservationsCmd(String videoArchiveName, VideoTime videoTime, String user, Collection<Observation> sourceObservations) {
        if (videoArchiveName == null || videoTime == null || sourceObservations == null) {
            throw new IllegalArgumentException("Command arguments can not be null");
        }
        this.sourceObservations = new ArrayList<Observation>(sourceObservations);
        this.videoArchiveName = videoArchiveName;
        this.videoTime = videoTime;
        this.user = user;
    }

    @Override
    public void apply(ToolBelt toolBelt) {
        AnnotationFactory annotationFactory = toolBelt.getAnnotationFactory();
        /*
         * DAOTX See if a VideoFrame with the given time code already exists
         */
        DAO dao = toolBelt.getAnnotationDAOFactory().newDAO();
        VideoArchiveDAO videoArchiveDAO = toolBelt.getAnnotationDAOFactory().newVideoArchiveDAO(dao.getEntityManager());
        dao.startTransaction();
        VideoArchive videoArchive = videoArchiveDAO.findByName(videoArchiveName);

        if (videoArchive != null) {

            VideoFrame videoFrame = videoArchive.findVideoFrameByTimeCode(videoTime.getTimecode());
            if (videoFrame == null) {
                videoFrame = annotationFactory.newVideoFrame();
                videoFrame.setTimecode(videoTime.getTimecode());
                videoFrame.setRecordedDate(videoTime.getDate());
                CameraDirections cameraDirections = (CameraDirections) Lookup.getCameraDirectionDispatcher().getValueObject();
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
                        //dao.persist(copyAssociation);
                    }

                    copyObservations.add(copyObservation);
                }
            }

            dao.endTransaction();

            EventBus.publish(new ObservationsAddedEvent(null, copyObservations));
        }
    }

    @Override
    public void unapply(ToolBelt toolBelt) {
        Collection<Observation> droppedObservations = new ArrayList<Observation>(copyObservations);
        synchronized (copyObservations) {
            DAO dao = toolBelt.getAnnotationDAOFactory().newDAO();
            dao.startTransaction();

            for (Observation observation : copyObservations) {
                observation = dao.find(observation);
                if (observation != null) {
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
        return "Copy " + sourceObservations.size() + " observations to " + videoArchiveName + " at " + videoTime.getTimecode();
    }
}
