package vars.annotation.ui.imagepanel;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import foxtrot.Job;
import foxtrot.Task;
import foxtrot.Worker;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.Observation;
import vars.annotation.VideoFrame;
import vars.annotation.VideoFrameDAO;
import vars.annotation.ui.PersistenceController;
import vars.annotation.ui.eventbus.ObservationsAddedEvent;
import vars.annotation.ui.eventbus.ObservationsChangedEvent;
import vars.annotation.ui.eventbus.ObservationsRemovedEvent;
import vars.annotation.ui.eventbus.ObservationsSelectedEvent;
import vars.annotation.ui.eventbus.UIEventSubscriber;
import vars.annotation.ui.eventbus.VideoArchiveChangedEvent;
import vars.annotation.ui.eventbus.VideoArchiveSelectedEvent;
import vars.annotation.ui.eventbus.VideoFramesChangedEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * @author Brian Schlining
 * @since 2012-08-06
 */
public class UIDataCoordinator implements UIEventSubscriber {

    private volatile VideoFrame videoFrame;
    private Collection<Observation> emptySet = Collections.unmodifiableSet(new HashSet<Observation>());
    private Collection<Observation> selectedObservations = Collections.synchronizedSet(new HashSet<Observation>());
    private final AnnotationDAOFactory annotationDAOFactory;

    public UIDataCoordinator(AnnotationDAOFactory annotationDAOFactory) {
        this.annotationDAOFactory = annotationDAOFactory;
        AnnotationProcessor.process(this);
    }

    public void setVideoFrame(final VideoFrame _videoFrame, Collection<Observation> _selectedObservations) {
        selectedObservations.clear();
        if (_videoFrame == null) {
            videoFrame = null;

            EventBus.publish(new IAFRepaintEvent(this, this));
        }
        else {
            selectedObservations.addAll(_selectedObservations);
            Worker.post(new Job() {
                @Override
                public Object run() {
                    VideoFrameDAO dao = annotationDAOFactory.newVideoFrameDAO();
                    dao.startTransaction();
                    videoFrame = dao.find(_videoFrame);
                    dao.endTransaction();
                    dao.close();
                    EventBus.publish(new IAFRepaintEvent(UIDataCoordinator.this, UIDataCoordinator.this));
                    return null;  //To change body of implemented methods use File | Settings | File Templates.
                }
            });

        }
    }


    public VideoFrame getVideoFrame() {
        return videoFrame;
    }

    public Collection<Observation> getSelectedObservations() {
        return selectedObservations;
    }

    public Collection<Observation> getObservations() {
        return (videoFrame == null) ? emptySet : videoFrame.getObservations();
    }

    @EventSubscriber(eventClass = ObservationsAddedEvent.class)
    @Override
    public void respondTo(ObservationsAddedEvent event) {
        setVideoFrame(videoFrame, new ArrayList<Observation>(selectedObservations));
    }

    @EventSubscriber(eventClass = ObservationsChangedEvent.class)
    @Override
    public void respondTo(ObservationsChangedEvent event) {
        setVideoFrame(videoFrame, new ArrayList<Observation>(selectedObservations));
    }

    @EventSubscriber(eventClass = ObservationsRemovedEvent.class)
    @Override
    public void respondTo(ObservationsRemovedEvent event) {
        setVideoFrame(videoFrame, new ArrayList<Observation>(selectedObservations));
    }

    @EventSubscriber(eventClass = ObservationsSelectedEvent.class)
    @Override
    public void respondTo(ObservationsSelectedEvent event) {
        Collection<VideoFrame> selectedVideoFrames = PersistenceController.toVideoFrames(event.get());

        if (selectedVideoFrames.size() == 1) {
            VideoFrame newVideoFrame = selectedVideoFrames.iterator().next();
            setVideoFrame(newVideoFrame, event.get());
        }
        else {
            setVideoFrame(null, emptySet);
        }
    }


    @EventSubscriber(eventClass = VideoArchiveChangedEvent.class)
    @Override
    public void respondTo(VideoArchiveChangedEvent event) {
        setVideoFrame(null, emptySet);
    }

    @EventSubscriber(eventClass = VideoArchiveSelectedEvent.class)
    @Override
    public void respondTo(VideoArchiveSelectedEvent event) {
        setVideoFrame(null, emptySet);
    }

    @EventSubscriber(eventClass = VideoFramesChangedEvent.class)
    @Override
    public void respondTo(VideoFramesChangedEvent event) {
        Collection<VideoFrame> changedVideoFrame = Collections2.filter(event.get(), new Predicate<VideoFrame>() {
            @Override
            public boolean apply(VideoFrame input) {
                return input.equals(videoFrame);
            }
        });

        if (!changedVideoFrame.isEmpty()) {
            setVideoFrame(changedVideoFrame.iterator().next(), new ArrayList<Observation>(selectedObservations));
        }
    }
}
