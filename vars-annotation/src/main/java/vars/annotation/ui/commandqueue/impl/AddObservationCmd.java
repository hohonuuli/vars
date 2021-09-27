package vars.annotation.ui.commandqueue.impl;

import com.google.common.collect.ImmutableList;
import org.bushe.swing.event.EventBus;

import org.mbari.vcr4j.time.Timecode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.UserAccount;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.AnnotationFactory;
import vars.annotation.AnnotationPersistenceService;
import vars.annotation.CameraData;
import vars.annotation.Observation;
import vars.annotation.ObservationDAO;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;
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
 * Add a new observation
 *
 * @author Brian Schlining
 * @since 2011-10-10
 */
public class AddObservationCmd implements Command {

    private Logger log = LoggerFactory.getLogger(getClass());

    private String conceptName;
    private final String timecode;
    private final Date recordedDate;
    private final String videoArchiveName;
    private final String user;
    private final String cameraDirection;
    private Object newPrimaryKey;
    private final Point2D point;
    private final String imageReference;
    private boolean selectAddedObservation = true;

    public AddObservationCmd(String conceptName, String timecode, Date recordedDate, String videoArchiveName,
                             String user, String cameraDirection) {
        this(conceptName, timecode, recordedDate, videoArchiveName, user, cameraDirection, null, null, false);
    }

    public AddObservationCmd(String conceptName, String timecode, Date recordedDate, String videoArchiveName,
                             String user, String cameraDirection, boolean selectAddedObservation) {
        this(conceptName, timecode, recordedDate, videoArchiveName, user, cameraDirection, null, null, selectAddedObservation);
    }

    public AddObservationCmd(String conceptName, String timecode, Date recordedDate, String videoArchiveName,
                             String user, String cameraDirection, Point2D point) {
        this(conceptName, timecode, recordedDate, videoArchiveName, user, cameraDirection, point, null, false);
    }

    public AddObservationCmd(String conceptName, String timecode, Date recordedDate, String videoArchiveName,
                             String user, String cameraDirection, Point2D point, boolean selectAddedObservation) {
        this(conceptName, timecode, recordedDate, videoArchiveName, user, cameraDirection, point, null, selectAddedObservation);

    }

    public AddObservationCmd(String conceptName, String timecode, Date recordedDate, String videoArchiveName,
                             String user, String cameraDirection, Point2D point, String imageReference) {
        this(conceptName, timecode, recordedDate, videoArchiveName, user, cameraDirection, point, imageReference, false);
    }

    public AddObservationCmd(String conceptName, String timecode, Date recordedDate, String videoArchiveName,
                             String user, String cameraDirection, Point2D point, String imageReference, boolean selectAddedObservation) {
        this.conceptName = conceptName;
        this.timecode = timecode;
        this.recordedDate = recordedDate;
        this.videoArchiveName = videoArchiveName;
        this.user = (user == null) ? UserAccount.USERNAME_DEFAULT : user;
        this.cameraDirection = cameraDirection;
        this.point = point;
        this.imageReference = imageReference;
        this.selectAddedObservation = selectAddedObservation;
    }

    private double elapsedTime(Long start) {
        return (System.nanoTime() - start) / 1e9;
    }

    @Override
    public void apply(ToolBelt toolBelt) {

        long tic = System.nanoTime();

        AnnotationPersistenceService annotationPersistenceService = toolBelt.getAnnotationPersistenceService();
        AnnotationDAOFactory daoFactory = toolBelt.getAnnotationDAOFactory();
        AnnotationFactory factory = toolBelt.getAnnotationFactory();
        VideoFrameDAO videoFrameDAO = daoFactory.newVideoFrameDAO();
        VideoArchiveDAO videoArchiveDAO = daoFactory.newVideoArchiveDAO(videoFrameDAO.getEntityManager());
        videoArchiveDAO.startTransaction();
        VideoArchive videoArchive = videoArchiveDAO.findByName(videoArchiveName);

        Observation newObservation = null;

        if (videoArchive != null && conceptName != null && timecode != null) {

            /*
             * Verify that the timecode is acceptable
             */
            new Timecode(timecode); // Bad timecodes will throw an exception

            /*
             * Get or create the VideoFrame
             */
            log.debug(elapsedTime(tic) + "s :Lookup existing videoframe");
            Long id = annotationPersistenceService.findTimeCodeByVideoArchiveName(timecode, videoArchiveName);
            VideoFrame videoFrame = null;
            if (id != null) {
                videoFrame = videoFrameDAO.findByPrimaryKey(id);
            }
            log.debug(elapsedTime(tic) + "s :Find matching timecode");
            tic = System.nanoTime();

            if (videoFrame == null) {
                videoFrame = factory.newVideoFrame();
                videoFrame.setRecordedDate(recordedDate);
                videoFrame.setTimecode(timecode);
                CameraData cameraData = videoFrame.getCameraData();
                cameraData.setDirection(cameraDirection);
                log.debug(elapsedTime(tic) + "s :Build new videoframe");
                tic = System.nanoTime();
                videoArchive.addVideoFrame(videoFrame);
            }


            if (imageReference != null) {
                videoFrame.getCameraData().setImageReference(imageReference);
            }
            log.debug(elapsedTime(tic) + "s :Built Videoframe");
            tic = System.nanoTime();

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
            log.debug(elapsedTime(tic) + "s :Built Observation");
            tic = System.nanoTime();

        }
        videoArchiveDAO.endTransaction();
        videoArchiveDAO.getEntityManager().clear();
        videoArchiveDAO.close();
        log.debug(elapsedTime(tic) + "s :End Transaction");
        tic = System.nanoTime();
        if (newObservation != null) {
            newPrimaryKey = newObservation.getPrimaryKey();
        }
        EventBus.publish(new ObservationsAddedEvent(null, newObservation));
        log.debug(elapsedTime(tic) + "s :Publish ObservationsAddedEvent");
        tic = System.nanoTime();

        if (selectAddedObservation) {
            EventBus.publish(new ObservationsSelectedEvent(this, ImmutableList.of(newObservation)));
            log.debug(elapsedTime(tic) + "s :Publish ObservationsSelectedEvent");
            tic = System.nanoTime();
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
        return "Add new Observation (" + conceptName + ") to " + timecode + " (" + recordedDate + ")";
    }
}
