/*
 * @(#)DistanceMeasurementLayerUI.java   2011.07.25 at 01:22:47 PDT
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

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.jdesktop.jxlayer.JXLayer;
import org.mbari.awt.AwtUtilities;
import org.mbari.swing.JImageUrlCanvas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.ILink;
import vars.annotation.Association;
import vars.annotation.Observation;
import vars.annotation.ui.ToolBelt;

import javax.swing.SwingUtilities;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeSupport;
import java.util.Collection;

import java.util.Vector;

/**
 * @author Brian Schlining
 * @since 2011-07-22
 *
 * @param <T>
 */
public class MeasurementLayerUI<T extends JImageUrlCanvas> extends CrossHairLayerUI<T> {

    private static final int markerDiameter = 10;
    public static final String PROP_OBSERVATION = "Observation";
    private final Point2D lineStart = new Point2D.Double();
    private final Point2D lineEnd = new Point2D.Double();
    private boolean selectedLineEnd;
    private boolean selectedLineStart;
    private GeneralPath line = new GeneralPath();
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private final Collection<MeasurementCompletedListener> measurementCompletedListeners = new Vector<MeasurementCompletedListener>();
    private Observation observation;
    private final ToolBelt toolBelt;

    class MeasurementPath {
        final Measurement measurement;
        final GeneralPath generalPath = new GeneralPath();
        final Point2D commentPoint = new Point2D.Float();

        MeasurementPath(Measurement measurement) {
            this.measurement = measurement;
        }

    }
    
    /** The path from lineStart to the current mouse position */
    private final Collection<MeasurementPath> measurementPaths = new Vector<MeasurementPath>();
    private Logger log = LoggerFactory.getLogger(getClass());
    private Font lineFont = new Font("Sans Serif", Font.PLAIN, 10);

    private final Function<ILink, MeasurementPath> associationTransform = new Function<ILink, MeasurementPath>() {
        @Override
        public MeasurementPath apply(ILink input) {
            return new MeasurementPath(Measurement.LINK_TO_MEASUREMENT_TRANSFORM.apply(input));
        }
    };


    /**
     * Constructs ...
     *
     * @param toolBelt
     */
    public MeasurementLayerUI(ToolBelt toolBelt) {
        this.toolBelt = toolBelt;
    }

    /**
     * Handle to the class that handles property changes. Used to hang listeners onto the
     * 'measuring' property.
     *
     * @return
     */
    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    /**
     * @return
     */
    public Observation getObservation() {
        return observation;
    }

    @Override
    protected void paintLayer(Graphics2D g2, JXLayer<? extends T> jxl) {
        super.paintLayer(g2, jxl);
        g2.setPaintMode();    // Make sure XOR is turned off
        // --- Paint observation
        if (observation != null) {
            MarkerStyle markerStyle = MarkerStyle.NOTSELECTED;
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

        // --- Draw and label existing measurements
        g2.setXORMode(Color.GREEN);
        g2.setStroke(new BasicStroke(2));
        for(MeasurementPath path : measurementPaths) {
            updateMeasurementPath(path, jxl);
            String comment = path.measurement.getComment();
            g2.draw(path.generalPath);
            g2.setFont(lineFont);
            g2.drawString(comment, (float) path.commentPoint.getX(), (float) path.commentPoint.getY());
        }
        g2.setPaintMode();

        // --- Paint lineStart
        g2.setStroke(new BasicStroke(2));
        g2.setXORMode(Color.RED);
        final int markerOffset = markerDiameter / 2;
        if (selectedLineStart) {
            Point2D lineStartPoint = jxl.getView().convertToComponent(lineStart);
            Point componentPoint = AwtUtilities.toPoint(lineStartPoint);
            int x = componentPoint.x;
            int y = componentPoint.y;
            Ellipse2D marker = new Ellipse2D.Double(x - markerOffset, y - markerOffset, markerDiameter, markerDiameter);
            g2.draw(marker);
            g2.draw(line);
        }

    }

    @Override
    protected void processMouseEvent(MouseEvent me, JXLayer<? extends T> jxl) {
        super.processMouseEvent(me, jxl);
        Point point = SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), jxl);
        switch (me.getID()) {
        case MouseEvent.MOUSE_PRESSED:
            Point2D imagePoint = jxl.getView().convertToImage(point);
            if (!selectedLineStart) {
                // --- On first click set lineStart value
                lineStart.setLocation(imagePoint);
                selectedLineStart = true;
                setDirty(true);
            }
            else {
                // --- On second click set lineEnd value, generate association, set measuring property to false
                lineEnd.setLocation(imagePoint);
                selectedLineEnd = true;
                // TODO show dialog to get comment added
                Measurement measurement = getMeasurement(null, jxl);
                setDirty(true);

                // Notify listeners
                MeasurementCompletedEvent event = new MeasurementCompletedEvent(measurement, observation);
                for (MeasurementCompletedListener listener : measurementCompletedListeners) {
                    listener.onComplete(event);
                }

                resetUI();

            }
        default:
            // Do nothing
        }
    }

    private Measurement getMeasurement(String comment, JXLayer<? extends T> jxl) {
        Point2D lineStartInImage = jxl.getView().convertToImage(lineStart);
        Point2D lineEndInImage = jxl.getView().convertToImage(lineEnd);
        int x0 = (int) Math.round(lineStartInImage.getX());
        int y0 = (int) Math.round(lineStartInImage.getX());
        int x1 = (int) Math.round(lineEndInImage.getX());
        int y1 = (int) Math.round(lineEndInImage.getX());
        return new Measurement(x0, y0, x1, y1, comment);
    }

    @Override
    protected void processMouseMotionEvent(MouseEvent me, JXLayer<? extends T> jxl) {
        super.processMouseMotionEvent(me, jxl);

        if (me.getID() == MouseEvent.MOUSE_MOVED  &&
                selectedLineStart &&
                !selectedLineEnd) {
            Point point = SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), jxl);
            int w = jxl.getWidth();
            int h = jxl.getHeight();

            /*
             * Create crosshair
             */
            line.reset();
            if (point.y <= h && point.x <= w) {
                Point2D componentPoint = jxl.getView().convertToComponent(lineStart);
                line.moveTo(componentPoint.getX(), componentPoint.getY());
                line.lineTo(point.x, point.y);
            }

            // mark the ui as dirty and needed to be repainted
            setDirty(true);
        }
    }

    /**
     *
     * @param newObservation
     */
    public void setObservation(Observation newObservation) {
        Observation oldObservation = this.observation;
        this.observation = newObservation;
        measurementPaths.clear();
        resetUI();
        // --- Parse out any existing measurement
        if (newObservation != null) {
            Collection<Association> associations = Collections2.filter(newObservation.getAssociations(), Measurement.IS_MEASUREMENT_PREDICATE);
            for (Association  association : associations) {
                try {
                    measurementPaths.add(associationTransform.apply(association));
                }
                catch (Exception e) {
                    log.warn("Unable to parse coordinates from the association, " + association);
                }
            }
        }
        setDirty(true);
        propertyChangeSupport.firePropertyChange(PROP_OBSERVATION, oldObservation, newObservation);
    }

    /**
     * resets the ui to a known state
     */
    public void resetUI() {
        lineStart.setLocation(0, 0);
        lineEnd.setLocation(0, 0);
        line.reset();
        selectedLineEnd = false;
        selectedLineStart = false;
        setDirty(true);
    }


    /**
     * Resets the path of measurement lines and comment location based on the size of the underlying componenet
     * @param measurementPath
     * @param jxl
     */
    private void updateMeasurementPath(MeasurementPath measurementPath, JXLayer<? extends T> jxl) {

        Measurement measurement = measurementPath.measurement;
        GeneralPath generalPath = measurementPath.generalPath;
        generalPath.reset();

        Point2D imagePoint0 = new Point2D.Double(measurement.getX0(), measurement.getY0());
        Point2D componentPoint2D0 = jxl.getView().convertToComponent(imagePoint0);
        generalPath.moveTo(componentPoint2D0.getX(), componentPoint2D0.getY());

        Point2D imagePoint1 = new Point2D.Double(measurement.getX1(), measurement.getY1());
        Point2D componentPoint2D1 = jxl.getView().convertToComponent(imagePoint1);
        generalPath.lineTo(componentPoint2D1.getX(), componentPoint2D1.getY());

        double cx = ((componentPoint2D0.getX() + componentPoint2D1.getX()) / 2) + 5;
        double cy = (componentPoint2D0.getY() + componentPoint2D1.getY()) / 2;
        measurementPath.commentPoint.setLocation(cx, cy);

    }

    public void addMeasurementCompletedListener(MeasurementCompletedListener listener) {
        measurementCompletedListeners.add(listener);
    }


}
