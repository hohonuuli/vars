/*
 * @(#)AnnotationLayerUI.java   2012.11.26 at 08:48:37 PST
 *
 * Copyright 2011 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.annotation.ui.imagepanel;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jdesktop.jxlayer.JXLayer;
import mbarix4j.swing.JImageUrlCanvas;
import vars.UserAccount;
import vars.annotation.Observation;
import vars.annotation.VideoFrame;
import vars.annotation.ui.StateLookup;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.commandqueue.CommandEvent;
import vars.annotation.ui.commandqueue.impl.AddObservationToVideoFrameCmd;
import vars.annotation.ui.eventbus.ObservationsSelectedEvent;
import vars.knowledgebase.Concept;

import javax.swing.SwingUtilities;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashSet;

/**
 * A LayerUI that allows users to add annotations by clicking on an image. The resulting annotations
 * will contain x, and y pixel coordinates for their location in the image.
 *
 * @author brian
 * @tparam The type of JComponent that this layer will be applied to. It's required to be a subtype
 *  of JImageUrlCanvas
 */
public class AnnotationLayerUI<T extends JImageUrlCanvas> extends ImageFrameLayerUI<T> {

    //private JXCrossHairPainter<T> crossHairPainter = new JXCrossHairPainter<T>();
    private JXPainter<T> selectedObservationsPainter = new JXSelectedObservationsPainter<T>();

    /**
     * This font is used to draw the concept name of concepts.
     */
    private final Controller controller = new Controller();

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
    private final UIDataCoordinator dataCoordinator;
    private VideoFrame oldVideoFrame;
    private final ToolBelt toolBelt;

    /**
     * Constructs ...
     *
     * @param toolBelt
     * @param dataCoordinator
     */
    public AnnotationLayerUI(ToolBelt toolBelt, UIDataCoordinator dataCoordinator,
                             CommonPainters<T> commonPainters) {
        super(commonPainters);
        setDisplayName("Annotate");
        setSettingsBuilder(new AnnotationLayerSettingsBuilder<T>(this));
        this.toolBelt = toolBelt;
        this.dataCoordinator = dataCoordinator;
        AnnotationProcessor.process(this);
        clearPainters();
    }

    /**
     */
    @Override
    public void clearPainters() {
        super.clearPainters();
        addPainter(selectedObservationsPainter);
        //addPainter(crossHairPainter);
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
    public ToolBelt getToolBelt() {
        return toolBelt;
    }

    @Override
    protected void paintLayer(Graphics2D g2, JXLayer<? extends T> jxl) {
        super.paintLayer(g2, jxl);
        g2.setPaintMode();    // Make sure XOR is turned off

        if (dataCoordinator.getVideoFrame() != null) {

            if (boundingBox != null) {

                // Draw the bounding box
                g2.setPaint(Color.MAGENTA);
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
                controller.sendSelectionNotification(new HashSet<>());
                setDirty(true);
            }
            else {

                //controller.sendSelectionNotification(new HashSet<Observation>());

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

                    VideoFrame videoFrame = dataCoordinator.getVideoFrame();
                    if (videoFrame != null) {
                        Collection<Observation> selectedObservations = new HashSet<Observation>();
                        for (Observation observation : videoFrame.getObservations()) {
                            if ((observation.getX() != null) && (observation.getY() != null)) {
                                Point2D imagePoint = jxl.getView().convertToComponent(
                                    new Point2D.Double(observation.getX(), observation.getY()));
                                if ((imagePoint != null) && r.contains(imagePoint)) {
                                    selectedObservations.add(observation);
                                }
                            }
                        }

                        controller.sendSelectionNotification(selectedObservations);
                    }
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
     * @param event
     */
    @EventSubscriber(eventClass = IAFRepaintEvent.class)
    public void respondTo(IAFRepaintEvent event) {
        VideoFrame videoFrame = event.get().getVideoFrame();
        if ((videoFrame == null) || !videoFrame.equals(oldVideoFrame)) {
            oldVideoFrame = videoFrame;
            boundingBox = null;
        }
        setDirty(true);
    }

    /**
     *
     * @param concept
     */
    public void setConcept(Concept concept) {
        this.concept = concept;
    }

    private class Controller {

        void newObservation(Point2D point) {

            String conceptName = getConcept().getPrimaryConceptName().getName();
            UserAccount userAccount = StateLookup.getUserAccount();
            String user = userAccount.getUserName();
            VideoFrame videoFrame = dataCoordinator.getVideoFrame();
            if (videoFrame != null) {
                Command command = new AddObservationToVideoFrameCmd(conceptName, dataCoordinator.getVideoFrame(), user,
                        point, true);
                CommandEvent commandEvent = new CommandEvent(command);
                EventBus.publish(commandEvent);
            }
        }

        void sendSelectionNotification(Collection<Observation> selectedObservations) {
            EventBus.publish(new ObservationsSelectedEvent(AnnotationLayerUI.this, selectedObservations));
        }
    }
}
