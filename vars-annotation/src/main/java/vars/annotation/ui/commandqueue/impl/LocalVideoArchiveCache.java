package vars.annotation.ui.commandqueue.impl;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;
import vars.annotation.VideoFrame;
import vars.annotation.ui.eventbus.ObservationsAddedEvent;
import vars.annotation.ui.eventbus.ObservationsChangedEvent;
import vars.annotation.ui.eventbus.ObservationsRemovedEvent;
import vars.annotation.ui.eventbus.ObservationsSelectedEvent;
import vars.annotation.ui.eventbus.UIEventSubscriber;
import vars.annotation.ui.eventbus.VideoArchiveChangedEvent;
import vars.annotation.ui.eventbus.VideoArchiveSelectedEvent;
import vars.annotation.ui.eventbus.VideoFramesChangedEvent;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * When using the annotation application the call to videoArchive.findVideoFrameByTimeCode(timecode);
 * can take several seconds when the videoFrame count is large (thousands). This is due to the fact
 * a videoArchive object may be disconnect from a JPA transaction; when it is looked up again and
 * and the new instance is brought into the JPA transaction, ALL the videoFrames need to be reloaded
 * from the database. This is great for consistency, but is very slow for the user experience.
 *
 * In order to speed it up we keep a local cache of the current VideoFrame and it's VideoFrames.
 * CommandEvents can lookup the correct videoFrame from this cache.
 *
 * This class implements the UIEventSubscriber interface in order to manually updated the cache in
 * response to any events that modify the VideoArchive in the database.
 *
 * <b>This class is currently unused except in the ToolBox.</b>
 *
 * @author Brian Schlining
 * @since 2012-07-31
 * @deprecated Not used. Get rid of this class
 */
public class LocalVideoArchiveCache implements UIEventSubscriber {

    private volatile VideoArchive videoArchive;
    private final AnnotationDAOFactory annotationDAOFactory;

    @Inject
    public LocalVideoArchiveCache(AnnotationDAOFactory annotationDAOFactory) {
        this.annotationDAOFactory = annotationDAOFactory;
        AnnotationProcessor.process(this);
    }

    /**
     *
     * @return The cached VideoArchive. <b>WARNING! Do not mutate this object or any objects
     *      attached to it. DO NOT update or merge it into a JPA transaction!!</b>
     */
    public synchronized VideoArchive getVideoArchive() {
        return videoArchive;
    }

    /**
     * Set the cached VideoArchive. This will actually fetch a fresh copy of the provided VideoArchive
     * from the database and load all the VideoFrames.
     *
     * @param videoArchive0
     */
    public synchronized void setVideoArchive(VideoArchive videoArchive0) {

        if (videoArchive0 != null) {
            VideoArchiveDAO dao = annotationDAOFactory.newVideoArchiveDAO();
            dao.startTransaction();
            videoArchive = dao.find(videoArchive0);
            if (videoArchive != null) {
                /*
                   Load all videoFrames (they're lazy loaded) and make sure we make a call
                   to each object so that it gets fully instantiated. This might not be needed
                   with EclipseLink but it was with Hibernate.
                 */
                Collection<VideoFrame> videoFrames = videoArchive.getVideoFrames();
                for (VideoFrame vf : videoFrames) {
                    vf.getTimecode();
                }
            }
            dao.endTransaction();

        }
        else {
            videoArchive = null;
        }

    }


    @Override
    public void respondTo(ObservationsAddedEvent event) {
        checkVideoArchive();

        final Collection<Observation> observations = event.get();


        // New VideoFrames, possibly including duplicates in videoFrames0
        final Set<VideoFrame> videoFrames1 = new HashSet<VideoFrame>(Collections2.transform(observations, new Function<Observation, VideoFrame>() {
            @Override
            public VideoFrame apply(Observation observation) {
                return observation.getVideoFrame();
            }
        }));

        synchronized (videoArchive.getVideoFrames()) {

            // Current VideoFrames
            final Set<VideoFrame> videoFrames0 = new HashSet<VideoFrame>(videoArchive.getVideoFrames());

            // New VideoFrames only
            videoFrames1.removeAll(videoFrames0);

            // Add new VideoFrames
            videoArchive.getVideoFrames().addAll(videoFrames1);
        }

    }

    @Override
    public void respondTo(ObservationsChangedEvent event) {
        respondTo(new ObservationsAddedEvent(this, event.get()));
    }

    @Override
    public void respondTo(ObservationsRemovedEvent event) {
        checkVideoArchive();

        final Collection<Observation> observations = event.get();

        // VideoFrames to be droppped
        final Set<VideoFrame> videoFrames1 = new HashSet<VideoFrame>(Collections2.transform(observations, new Function<Observation, VideoFrame>() {
            @Override
            public VideoFrame apply(Observation observation) {
                return observation.getVideoFrame();
            }
        }));

        synchronized (videoArchive.getVideoFrames()) {
            // Remove VideoFrames
            videoArchive.getVideoFrames().removeAll(videoFrames1);
        }
    }

    @Override
    public void respondTo(ObservationsSelectedEvent event) {
        // Do Nothing
    }

    @Override
    public void respondTo(VideoArchiveChangedEvent event) {
        setVideoArchive(event.get());
    }

    @Override
    public void respondTo(VideoArchiveSelectedEvent event) {
        // Do nothing
    }

    @Override
    public void respondTo(VideoFramesChangedEvent event) {
        checkVideoArchive();

        final Collection<VideoFrame> videoFramesNew = event.get();

        // Current VideoFrames
        synchronized (videoArchive) {
            final Collection<VideoFrame> videoFrames = videoArchive.getVideoFrames();
            final Set<VideoFrame> videoFramesOld = new HashSet<VideoFrame>(videoFrames);

            // If a changed videoframe exists alread, replace it with the new one
            for (VideoFrame vfNew : videoFramesNew) {
                for (VideoFrame vfOld : videoFramesOld) {
                    if (vfNew.getPrimaryKey().equals(vfOld.getPrimaryKey())) {
                        videoFrames.remove(vfOld);
                        videoFrames.add(vfNew);
                    }
                }
            }
        }


    }

    private void checkVideoArchive() {
        if (videoArchive == null) {
            throw new IllegalStateException("Unable to perform the requested operation on a null VideoArchive");
        }
    }
}
