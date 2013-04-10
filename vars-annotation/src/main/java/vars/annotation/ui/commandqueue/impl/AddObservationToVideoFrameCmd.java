package vars.annotation.ui.commandqueue.impl;

import com.google.common.collect.ImmutableList;
import org.bushe.swing.event.EventBus;
import vars.UserAccount;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.AnnotationFactory;
import vars.annotation.Observation;
import vars.annotation.ObservationDAO;
import vars.annotation.VideoArchive;
import vars.annotation.VideoFrame;
import vars.annotation.VideoFrameDAO;
import vars.annotation.jpa.ObservationImpl;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.eventbus.ObservationsAddedEvent;
import vars.annotation.ui.eventbus.ObservationsRemovedEvent;
import vars.annotation.ui.eventbus.ObservationsSelectedEvent;

import java.awt.geom.Point2D;
import java.util.Date;

/**
 * Add an observation to an existing videoFrame
 * @author Brian Schlining
 * @since 2012-07-31
 */
public class AddObservationToVideoFrameCmd implements Command {

    private String conceptName;
    private final String user;
    private Object newPrimaryKey;
    private final Point2D point;
    private boolean selectAddedObservation = true;
    private VideoFrame videoFrameDetached;

    public AddObservationToVideoFrameCmd(String conceptName, VideoFrame videoFrame,
                                         String user, Point2D point, boolean selectAddedObservation) {
        this.conceptName = conceptName;
        this.user = (user == null) ? UserAccount.USERNAME_DEFAULT : user;
        this.point = point;
        this.selectAddedObservation = selectAddedObservation;
        this.videoFrameDetached = videoFrame;
    }

    @Override
    public void apply(ToolBelt toolBelt) {

        AnnotationDAOFactory daoFactory = toolBelt.getAnnotationDAOFactory();
        AnnotationFactory factory = toolBelt.getAnnotationFactory();
        VideoFrameDAO videoFrameDAO = daoFactory.newVideoFrameDAO();
        videoFrameDAO.startTransaction();
        VideoFrame videoFrame = videoFrameDAO.find(videoFrameDetached);
        Observation newObservation = null;
        if (conceptName != null && videoFrame != null) {

            /*
                Create observation
             */
            String validatedConceptName = toolBelt.getPersistenceController().getValidatedConceptName(conceptName);
            newObservation = factory.newObservation();
            newObservation.setConceptName(validatedConceptName);
            newObservation.setObserver(user);
            newObservation.setObservationDate(new Date());
            if (point != null) {
                newObservation.setX(point.getX());
                newObservation.setY(point.getY());
            }
            videoFrame.addObservation(newObservation);

        }

        videoFrameDAO.endTransaction();
        videoFrameDAO.getEntityManager().clear();
        videoFrameDAO.close();
        if (newObservation != null) {
            newPrimaryKey = newObservation.getPrimaryKey();
            EventBus.publish(new ObservationsAddedEvent(null, newObservation));

            if (selectAddedObservation) {
                EventBus.publish(new ObservationsSelectedEvent(this, ImmutableList.of(newObservation)));
            }
        }

    }

    @Override
    public void unapply(ToolBelt toolBelt) {
        if (newPrimaryKey != null) {
            AnnotationDAOFactory daoFactory = toolBelt.getAnnotationDAOFactory();
            ObservationDAO dao = daoFactory.newObservationDAO();
            VideoFrameDAO videoFrameDAO = daoFactory.newVideoFrameDAO(dao.getEntityManager());
            dao.startTransaction();
            final Observation observation = dao.findByPrimaryKey(newPrimaryKey);

            Observation fakeObservation = null;
            if (observation != null) {
                // XXX - START HACK: Had to use JPA implementation instead of interface!!
                ObservationImpl tmp  = (ObservationImpl) toolBelt.getAnnotationFactory().newObservation();
                tmp.setId((Long) observation.getPrimaryKey());
                fakeObservation = tmp;
                // XXX - END HACK:
                VideoFrame videoFrame = observation.getVideoFrame();
                videoFrame.removeObservation(observation);
                dao.remove(observation);
                if (videoFrame.getObservations().size() == 0) {
                    VideoArchive videoArchive = videoFrame.getVideoArchive();
                    videoArchive.removeVideoFrame(videoFrame);
                    videoFrameDAO.remove(videoFrame);
                }
            }
            dao.endTransaction();
            dao.close();
            if (fakeObservation != null) {
                EventBus.publish(new ObservationsRemovedEvent(null, ImmutableList.of(fakeObservation)));
            }
        }
    }


    @Override
    public String getDescription() {
        return "Add new Observation (" + conceptName + ") to " + videoFrameDetached;
    }
}
