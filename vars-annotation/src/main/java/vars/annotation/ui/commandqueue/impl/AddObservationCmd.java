package vars.annotation.ui.commandqueue.impl;

import org.bushe.swing.event.EventBus;
import org.mbari.movie.Timecode;
import vars.UserAccount;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.AnnotationFactory;
import vars.annotation.CameraData;
import vars.annotation.Observation;
import vars.annotation.ObservationDAO;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;
import vars.annotation.VideoFrame;
import vars.annotation.VideoFrameDAO;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.eventbus.ObservationAddedEvent;
import vars.annotation.ui.eventbus.ObservationsRemovedEvent;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Brian Schlining
 * @since 2011-10-10
 */
public class AddObservationCmd implements Command {

    private String conceptName;
    private final String timecode;
    private final Date recordedDate;
    private final String videoArchiveName;
    private final String user;
    private final String cameraDirection;
    private Observation newObservation;
    private final Point2D point;

    public AddObservationCmd(String conceptName, String timecode, Date recordedDate, String videoArchiveName,
                             String user, String cameraDirection) {
        this(conceptName, timecode, recordedDate, videoArchiveName, user, cameraDirection, null);
    }

    public AddObservationCmd(String conceptName, String timecode, Date recordedDate, String videoArchiveName,
                             String user, String cameraDirection, Point2D point) {
        this.conceptName = conceptName;
        this.timecode = timecode;
        this.recordedDate = recordedDate;
        this.videoArchiveName = videoArchiveName;
        this.user = (user == null) ? UserAccount.USERNAME_DEFAULT : user;
        this.cameraDirection = cameraDirection;
        this.point = point;
    }

    @Override
    public void apply(ToolBelt toolBelt) {

        AnnotationDAOFactory daoFactory = toolBelt.getAnnotationDAOFactory();
        AnnotationFactory factory = toolBelt.getAnnotationFactory();
        VideoArchiveDAO videoArchiveDAO = daoFactory.newVideoArchiveDAO();
        videoArchiveDAO.startTransaction();
        VideoArchive videoArchive = videoArchiveDAO.findByName(videoArchiveName);
        if (videoArchive != null && conceptName != null && timecode != null) {
            /*
             * Verify that the timecode is acceptable
             */
            new Timecode(timecode); // Bad timecodes will throw an exception

            /*
             * Get or create the VideoFrame
             */
            VideoFrame videoFrame = videoArchive.findVideoFrameByTimeCode(timecode);
            if (videoFrame == null) {
                videoFrame = factory.newVideoFrame();
                videoFrame.setRecordedDate(recordedDate);
                videoFrame.setTimecode(timecode);
                CameraData cameraData = videoFrame.getCameraData();
                cameraData.setDirection(cameraDirection);
                videoArchive.addVideoFrame(videoFrame);
            }

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
        videoArchiveDAO.endTransaction();
        videoArchiveDAO.close();
        EventBus.publish(new ObservationAddedEvent(null, newObservation));
    }

    @Override
    public void unapply(ToolBelt toolBelt) {
        if (newObservation != null) {
            AnnotationDAOFactory daoFactory = toolBelt.getAnnotationDAOFactory();
            ObservationDAO dao = daoFactory.newObservationDAO();
            VideoFrameDAO videoFrameDAO = daoFactory.newVideoFrameDAO(dao.getEntityManager());
            dao.startTransaction();
            final Observation observation = dao.find(newObservation);
            if (observation != null) {
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
            EventBus.publish(new ObservationsRemovedEvent(null, new ArrayList<Observation>() {{
                add(observation);
            }}));
        }

    }


    @Override
    public String getDescription() {
        return "Add new Observation (" + conceptName + ") to " + timecode + " (" + recordedDate + ")";
    }
}
