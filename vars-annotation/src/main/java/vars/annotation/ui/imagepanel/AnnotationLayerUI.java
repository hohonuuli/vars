/*
 * @(#)AnnotationLayerUI.java   2010.02.17 at 02:07:49 PST
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
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.swing.SwingUtilities;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jdesktop.jxlayer.JXLayer;
import org.mbari.awt.AwtUtilities;
import org.mbari.swing.JImageUrlCanvas;
import vars.UserAccount;
import vars.annotation.CameraDirections;
import vars.annotation.Observation;
import vars.annotation.VideoFrame;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.PersistenceController;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.commandqueue.CommandEvent;
import vars.annotation.ui.commandqueue.impl.AddObservationCmd;
import vars.annotation.ui.eventbus.ObservationsAddedEvent;
import vars.annotation.ui.eventbus.ObservationsChangedEvent;
import vars.annotation.ui.eventbus.ObservationsRemovedEvent;
import vars.annotation.ui.eventbus.ObservationsSelectedEvent;
import vars.annotation.ui.eventbus.UIEventSubscriber;
import vars.annotation.ui.eventbus.VideoArchiveChangedEvent;
import vars.annotation.ui.eventbus.VideoArchiveSelectedEvent;
import vars.annotation.ui.eventbus.VideoFramesChangedEvent;
import vars.knowledgebase.Concept;

/**
 *
 * @author brian
 *
 * @param <T>
 */
public class AnnotationLayerUI<T extends JImageUrlCanvas> extends CrossHairLayerUI<T> implements UIEventSubscriber {

    /**
     * This font is used to draw the concept name of concepts.
     */
    private final Controller controller = new Controller();

    /** A list of all observations within the bounds of the current tile */
    private List<Observation> observations = new Vector<Observation>();

    /** A list of observations that were selected using the boundingbox */
    private Set<Observation> selectedObservations = Collections.synchronizedSet(new HashSet<Observation>());

    /**
     * Record of the location of the most recent mousePress event. Used for
     * drawing the boundingBox
     */
    private Point2D clickPoint = new Point2D.Double();

    /**
     * Bounding box is used in the UI to select annotation
     */
    private Rectangle2D boundingBox;
    /** The concept to use for creating new observations **/
    private Concept concept;
    private final ToolBelt toolBelt;
    private VideoFrame videoFrame;

    private final Predicate<Observation> displayableObservationsPredicate = new Predicate<Observation>() {
        @Override
        public boolean apply(Observation input) {
            return (input.getX() != null) && (input.getY() != null);
        }
    };

    /**
     * Constructs ...
     *
     * @param toolBelt
     */
    public AnnotationLayerUI(ToolBelt toolBelt) {
        this.toolBelt = toolBelt;
        AnnotationProcessor.process(this);

    }


    /**
     * @return
     */
    public Concept getConcept() {
        if (concept == null) {
            concept = toolBelt.getAnnotationPersistenceService().findRootConcept();
        }

        return concept;
    }

    /**
     * @return
     */
    public VideoFrame getVideoFrame() {
        return videoFrame;
    }


    @Override
    protected void paintLayer(Graphics2D g2, JXLayer<? extends T> jxl) {
        super.paintLayer(g2, jxl);
        g2.setPaintMode();    // Make sure XOR is turned off

        if (videoFrame != null) {

            for (Observation observation : observations) {
                MarkerStyle markerStyle = selectedObservations.contains(observation)
                                          ? MarkerStyle.SELECTED : MarkerStyle.NOTSELECTED;
                if ((observation.getX() != null) && (observation.getY() != null)) {


                    Point2D imagePoint = new Point2D.Double(observation.getX(), observation.getY());
                    Point2D componentPoint2D = jxl.getView().convertToComponent(imagePoint);
                    if (componentPoint2D != null) {
                        Point componentPoint = AwtUtilities.toPoint(componentPoint2D);
                        int x = componentPoint.x;
                        int y = componentPoint.y;

                        g2.setStroke(markerStyle.stroke);
                        g2.setPaint(markerStyle.color);

                        // Write the concept name
                        g2.setFont(markerStyle.font);
                        g2.drawString(observation.getConceptName(), x + 5, y);

                        // Draw the annotation
                        int armLength = markerStyle.armLength;
                        GeneralPath gp = new GeneralPath();
                        gp.moveTo(x - armLength, y - armLength);
                        gp.lineTo(x + armLength, y + armLength);
                        gp.moveTo(x + armLength, y - armLength);
                        gp.lineTo(x - armLength, y + armLength);
                        g2.draw(gp);
                    }
                }

            }

            if (boundingBox != null) {

                // Draw the bounding box
                g2.setXORMode(Color.WHITE);
                g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[] { 2, 2 },
                                             2));
                g2.draw(boundingBox);
                g2.setPaintMode();
            }
        }
    }

    @Override
    protected void processMouseEvent(MouseEvent me, JXLayer<? extends T> jxl) {
        super.processMouseEvent(me, jxl);
        Point point = SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), jxl);
        switch (me.getID()) {
        case MouseEvent.MOUSE_PRESSED:
            clickPoint.setLocation((int) Math.round(point.getX()), (int) Math.round(point.getY()));
            break;
        case MouseEvent.MOUSE_RELEASED:
            if (me.getButton() != MouseEvent.BUTTON1) {
                selectedObservations.clear();
                setDirty(true);
            }
            else {
                selectedObservations.clear();

                if (boundingBox == null) {

                    /*
                     * If the mouse is NOT being dragged then create a new Observation.
                     * The point coordinates should be in the images
                     * coordinate system NOT the coordiates of the view displaying
                     * the image.
                     */
                    Point2D imagePoint = jxl.getView().convertToImage(point);
                    controller.newObservation(imagePoint);

                }
                else {
                    Rectangle r = boundingBox.getBounds();
                    boundingBox = null;

                    for (Observation observation : observations) {
                        Point2D imagePoint = jxl.getView().convertToComponent(new Point2D.Double(observation.getX(),
                            observation.getY()));
                        if (imagePoint != null && r.contains(imagePoint)) {
                            selectedObservations.add(observation);
                        }
                    }

                    controller.sendSelectionNotification();
                }
            }

            break;
        default:

        // Do nothing
        }
    }

    @Override
    protected void processMouseMotionEvent(MouseEvent me, JXLayer<? extends T> jxl) {
        super.processMouseMotionEvent(me, jxl);

        switch (me.getID()) {
        case MouseEvent.MOUSE_DRAGGED:

            // Draw bounding box
            Point point = SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), jxl);
            int x1 = (int) Math.round(point.getX());
            int y1 = (int) Math.round(point.getY());
            int x0 = (int) clickPoint.getX();
            int y0 = (int) clickPoint.getY();
            int w = Math.abs(x1 - x0);
            int h = Math.abs(y1 - y0);

            int x = Math.min(x0, x1);
            int y = Math.min(y0, y1);
            if (boundingBox == null) {
                boundingBox = new Rectangle2D.Double(x, y, w, h);
            }
            else {

                // Minimize he redraw area
                boundingBox.setRect(x, y, w, h);
            }

            setDirty(true);
            break;
        default:

        // Do nothing

        }
    }



    /**
     *
     * @param concept
     */
    public void setConcept(Concept concept) {
        this.concept = concept;
    }

    public ToolBelt getToolBelt() {
        return toolBelt;
    }

    /**
     *
     * @param videoFrame_
     */
    public void setVideoFrame(VideoFrame videoFrame_) {

        if (videoFrame == null || !videoFrame.equals(videoFrame_)) {

            /*
             * We have to look up the videoframe from the database since the reference
             * passed may be stale
             */
            videoFrame = (videoFrame_ == null) ? null : toolBelt.getAnnotationDAOFactory().newDAO().find(videoFrame_);
            observations.clear();
            selectedObservations.clear();
            boundingBox = null;

            if (videoFrame != null) {
                observations.addAll(Collections2.filter(videoFrame.getObservations(), displayableObservationsPredicate));
                selectedObservations.addAll((Collection<Observation>) Lookup.getSelectedObservationsDispatcher().getValueObject());
            }
            setDirty(true);
        }
    }

    @EventSubscriber(eventClass = ObservationsSelectedEvent.class)
    @Override
    public void respondTo(ObservationsSelectedEvent event) {
        if (event.getEventSource() == null || !event.getEventSource().equals(this)) {
            Collection<VideoFrame> selectedVideoFrames = PersistenceController.toVideoFrames(event.get());
            if (selectedVideoFrames.size() == 1 ) {
                VideoFrame newVideoFrame = selectedVideoFrames.iterator().next();
                if (!newVideoFrame.equals(videoFrame)) {
                    // new VideoFrame is different than current one
                    setVideoFrame(newVideoFrame);
                }
                else {
                    // new VideoFrame is the same as current one.
                    selectedObservations.clear();
                    selectedObservations.addAll(event.get());
                    setDirty(true);
                }
            }
            else {
                setVideoFrame(null);
            }
        }
    }

    @EventSubscriber(eventClass = ObservationsChangedEvent.class)
    @Override
    public void respondTo(ObservationsChangedEvent event) {
        List<Observation> changedObservations = new ArrayList<Observation>(event.get());
        changedObservations.retainAll(observations);
        if (!changedObservations.isEmpty()) {
            observations.removeAll(changedObservations); // This actually removes the OLD versions
            observations.addAll(changedObservations); // Add the new versions
            setDirty(true);
        }
    }

    @EventSubscriber(eventClass = ObservationsAddedEvent.class)
    @Override
    public void respondTo(ObservationsAddedEvent event) {
        Collection<Observation> addedObservations = new ArrayList<Observation>();
        for (Observation observation : event.get()) {
            if (observation.getVideoFrame().equals(videoFrame)) {
                addedObservations.add(observation);
            }
        }

        if (!addedObservations.isEmpty()) {
            observations.addAll(addedObservations);
            setDirty(true);
        }

    }

    @EventSubscriber(eventClass = ObservationsRemovedEvent.class)
    @Override
    public void respondTo(ObservationsRemovedEvent event) {
        int originalSize = observations.size();
        observations.removeAll(event.get());
        if (observations.size() != originalSize) {
            setDirty(true);
        }
    }

    @EventSubscriber(eventClass = VideoArchiveChangedEvent.class)
    @Override
    public void respondTo(VideoArchiveChangedEvent event) {
        setVideoFrame(null);
    }

    @EventSubscriber(eventClass = VideoArchiveSelectedEvent.class)
    @Override
    public void respondTo(VideoArchiveSelectedEvent event) {
        setVideoFrame(null);
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
            setVideoFrame(changedVideoFrame.iterator().next());
        }

    }

    private class Controller {

        void newObservation(Point2D point) {

            String timecode = getVideoFrame().getTimecode();
            String videoArchiveName = getVideoFrame().getVideoArchive().getName();
            String conceptName = getConcept().getPrimaryConceptName().getName();
            String user = ((UserAccount) Lookup.getUserAccountDispatcher().getValueObject()).getUserName();
            CameraDirections cameraDirection = (CameraDirections) Lookup.getCameraDirectionDispatcher().getValueObject();
            Command command = new AddObservationCmd(conceptName, timecode, new Date(), videoArchiveName,
                    user, cameraDirection.getDirection(), point, true);
            CommandEvent commandEvent = new CommandEvent(command);
            EventBus.publish(commandEvent);

        }

        void sendSelectionNotification() {
            EventBus.publish(new ObservationsSelectedEvent(AnnotationLayerUI.this, selectedObservations));
        }
    }
}
