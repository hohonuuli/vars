/*
 * @(#)UIDataCoordinator.java   2012.08.21 at 10:53:51 PDT
 *
 * Copyright 2009 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.annotation.ui.imagepanel;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import foxtrot.Job;
import foxtrot.Worker;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.Observation;
import vars.annotation.ObservationDAO;
import vars.annotation.VideoFrame;
import vars.annotation.VideoFrameDAO;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.PersistenceController;
import vars.annotation.ui.eventbus.ObservationsAddedEvent;
import vars.annotation.ui.eventbus.ObservationsChangedEvent;
import vars.annotation.ui.eventbus.ObservationsRemovedEvent;
import vars.annotation.ui.eventbus.ObservationsSelectedEvent;
import vars.annotation.ui.eventbus.UIEvent;
import vars.annotation.ui.eventbus.UIEventSubscriber;
import vars.annotation.ui.eventbus.VideoArchiveChangedEvent;
import vars.annotation.ui.eventbus.VideoArchiveSelectedEvent;
import vars.annotation.ui.eventbus.VideoFramesChangedEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Brian Schlining
 * @since 2012-08-06
 */
public class UIDataCoordinator implements UIEventSubscriber {

    private Collection<Observation> emptySet = Collections.unmodifiableSet(new HashSet<Observation>());
    private Collection<Observation> selectedObservations = Collections.synchronizedSet(new HashSet<Observation>());
    private final AnnotationDAOFactory annotationDAOFactory;
    private volatile VideoFrame videoFrame;

    /**
     * Constructs ...
     *
     * @param annotationDAOFactory
     */
    public UIDataCoordinator(AnnotationDAOFactory annotationDAOFactory) {
        this.annotationDAOFactory = annotationDAOFactory;
        AnnotationProcessor.process(this);
    }

    /**
     * @return
     */
    public Collection<Observation> getObservations() {
        return (videoFrame == null) ? emptySet : videoFrame.getObservations();
    }

    /**
     * @return
     */
    public Collection<Observation> getSelectedObservations() {
        return selectedObservations;
    }

    /**
     * @return
     */
    public VideoFrame getVideoFrame() {
        return videoFrame;
    }

    /**
     *
     * @param event
     */
    @EventSubscriber(eventClass = ObservationsAddedEvent.class)
    @Override
    public void respondTo(final ObservationsAddedEvent event) {
        respondToObservationEvent(event);
    }

    /**
     *
     * @param event
     */
    @EventSubscriber(eventClass = ObservationsChangedEvent.class)
    @Override
    public void respondTo(ObservationsChangedEvent event) {
        respondToObservationEvent(event);
    }

    /**
     *
     * @param event
     */
    @EventSubscriber(eventClass = ObservationsRemovedEvent.class)
    @Override
    public void respondTo(ObservationsRemovedEvent event) {
        respondToObservationEvent(event);
    }

    private void respondToObservationEvent(UIEvent<Collection<Observation>> event) {
        Collection<Observation> alteredObservations = event.get();
        Set<VideoFrame> videoFrames = PersistenceController.toVideoFrames(alteredObservations);
        if (videoFrame != null && videoFrames.contains(videoFrame)) {
            setVideoFrame(videoFrame, new ArrayList<Observation>(selectedObservations));
        }
    }


    /**
     * @param event
     */
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

    /**
     * @param event
     */
    @EventSubscriber(eventClass = VideoArchiveChangedEvent.class)
    @Override
    public void respondTo(VideoArchiveChangedEvent event) {
        setVideoFrame(null, emptySet);
    }

    /**
     *
     * @param event
     */
    @EventSubscriber(eventClass = VideoArchiveSelectedEvent.class)
    @Override
    public void respondTo(VideoArchiveSelectedEvent event) {
        setVideoFrame(null, emptySet);
    }

    /**
     *
     * @param event
     */
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
            Collection<Observation> obs = (Collection<Observation>) Lookup.getSelectedObservationsDispatcher().getValueObject();
            setVideoFrame(changedVideoFrame.iterator().next(), new ArrayList<Observation>(obs));
        }
    }

    /**
     *
     * @param _videoFrame
     * @param _selectedObservations
     */
    public void setVideoFrame(final VideoFrame _videoFrame, final Collection<Observation> _selectedObservations) {
        selectedObservations.clear();
        if (_videoFrame == null) {
            videoFrame = null;

            EventBus.publish(new IAFRepaintEvent(this, this));
        }
        else {
            Worker.post(new Job() {

                @Override
                public Object run() {

                    VideoFrameDAO dao = annotationDAOFactory.newVideoFrameDAO();
                    ObservationDAO obsDao = annotationDAOFactory.newObservationDAO(dao.getEntityManager());
                    dao.startTransaction();
                    videoFrame = dao.find(_videoFrame);
                    for (Observation obs : _selectedObservations) {
                        Observation foundObs = obsDao.find(obs);
                        if (foundObs != null) {
                            selectedObservations.add(foundObs);
                        }
                    }
                    dao.endTransaction();
                    dao.close();
                    EventBus.publish(new IAFRepaintEvent(UIDataCoordinator.this, UIDataCoordinator.this));

                    return null;    //To change body of implemented methods use File | Settings | File Templates.
                }

            });

        }
    }
}
