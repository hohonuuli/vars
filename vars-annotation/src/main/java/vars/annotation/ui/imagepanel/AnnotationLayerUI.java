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
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.swing.SwingUtilities;
import org.jdesktop.jxlayer.JXLayer;
import org.mbari.awt.AwtUtilities;
import org.mbari.swing.JImageUrlCanvas;
import vars.annotation.Observation;
import vars.annotation.VideoFrame;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;
import vars.knowledgebase.Concept;

/**
 *
 * @author brian
 *
 * @param <T>
 */
public class AnnotationLayerUI<T extends JImageUrlCanvas> extends CrossHairLayerUI<T> {

    /**
     * This font is used to draw the concept name of concepts.
     */
    private final Controller controller = new Controller();

    /** A list of all observations within the bounds of the current tile */
    private List<Observation> observations = new Vector<Observation>();

    /** A list of observations that were selected using the boundingbox */
    private List<Observation> selectedObservations = new Vector<Observation>();

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

    private SelectObservationsListener selectObservationsListener;
    private CreateObservationListener createObservationListener;

    /**
     * Constructs ...
     *
     * @param toolBelt
     */
    public AnnotationLayerUI(ToolBelt toolBelt) {
        this.toolBelt = toolBelt;
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

    public SelectObservationsListener getSelectObservationsListener() {
        return selectObservationsListener;
    }

    public void setSelectObservationsListener(SelectObservationsListener selectObservationsListener) {
        this.selectObservationsListener = selectObservationsListener;
    }

    public CreateObservationListener getCreateObservationListener() {
        return createObservationListener;
    }

    public void setCreateObservationListener(CreateObservationListener createObservationListener) {
        this.createObservationListener = createObservationListener;
    }

    @Override
    protected void paintLayer(Graphics2D g2, JXLayer<? extends T> jxl) {
        super.paintLayer(g2, jxl);
        g2.setPaintMode();    // Make sure XOR is turned off

        if (videoFrame != null) {

            for (Observation observation : videoFrame.getObservations()) {
                Collection<Observation> rowSelectedObservations = (Collection<Observation>) Lookup
                    .getSelectedObservationsDispatcher().getValueObject();
                MarkerStyle markerStyle = rowSelectedObservations.contains(observation)
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

        // IF it's the same videoFrame DO NOT redraw. Otherwise the UI Will flicker
        if (videoFrame != videoFrame_) {
            /*
             * We have to look up the videoframe from the database since the reference
             * passed may be stale
             */
            videoFrame = (videoFrame_ == null) ? null : toolBelt.getAnnotationDAOFactory().newDAO().find(videoFrame_);
            observations.clear();
            selectedObservations.clear();
            boundingBox = null;
            setDirty(true);

            if (videoFrame != null) {
                observations.addAll(Collections2.filter(videoFrame.getObservations(), new Predicate<Observation>() {
                    public boolean apply(Observation input) {
                        return (input.getX() != null) && (input.getY() != null);
                    }
                }));
                setDirty(true);
            }
        }
    }

    private class Controller {

        void newObservation(Point2D point) {
            createObservationListener.doCreate(new CreateObservationEvent(getConcept(), getVideoFrame(), point, new Date()));
        }

        void sendSelectionNotification() {
            selectObservationsListener.doSelect(new SelectObservationsEvent(selectedObservations));
        }
    }
}
