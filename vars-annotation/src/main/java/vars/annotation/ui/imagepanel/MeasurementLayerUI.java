/*
 * @(#)MeasurementLayerUI.java   2011.09.20 at 02:51:09 PDT
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
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jdesktop.jxlayer.JXLayer;
import org.mbari.awt.AwtUtilities;
import org.mbari.swing.JImageUrlCanvas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.DAO;
import vars.ILink;
import vars.annotation.Association;
import vars.annotation.Observation;
import vars.annotation.ObservationDAO;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.eventbus.ObservationsChangedEvent;
import vars.annotation.ui.eventbus.ObservationsSelectedEvent;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

/**
 * @author Brian Schlining
 * @since 2011-07-22
 *
 * @param <T>
 */
public class MeasurementLayerUI<T extends JImageUrlCanvas> extends ImageFrameLayerUI<T> {

    private final JXPainter<T> crossHairPainter = new JXCrossHairPainter<T>();
    private final JXPainter<T> selectedObservationPainter = new JXSelectedObservationsPainter<T>(MarkerStyle.SELECTED_FAINT);

    /** Name of the observation property that can be used with property change listeners */
    public static final String PROP_OBSERVATION = "Observation";

    /** The diameter of the start of measurement marker */
    private static final int markerDiameter = 10;

    /** Point to hold start of line coordinates in image pixels */
    private final Point2D lineStart = new Point2D.Double();

    /** Point to hold end of line coordinates in image pixels */
    private final Point2D lineEnd = new Point2D.Double();

    /** Path used to draw measurement line */
    private GeneralPath line = new GeneralPath();
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /** The path from lineStart to the current mouse position */
    private final Collection<MeasurementPath> measurementPaths = new Vector<MeasurementPath>();
    private final Collection<MeasurementCompletedListener> measurementCompletedListeners = new Vector<MeasurementCompletedListener>();
    private Logger log = LoggerFactory.getLogger(getClass());
    private Font lineFont = new Font("Sans Serif", Font.PLAIN, 10);

    /** Transform that converts an association to a measurement object */
    private final Function<ILink, MeasurementPath> associationTransform = new Function<ILink, MeasurementPath>() {

        @Override
        public MeasurementPath apply(ILink input) {
            return new MeasurementPath(Measurement.LINK_TO_MEASUREMENT_TRANSFORM.apply(input));
        }
    };

    /** The observation that we're currently adding measurements to */
    private Observation observation;

    //private Collection<Observation> relatedObservations = Collections.synchronizedCollection(new ArrayList());

    /** Flag, when true we've just completed measuring a line */
    private boolean selectedLineEnd;

    /** Flag, when true we've just started measuring a line */
    private boolean selectedLineStart;
    private final ToolBelt toolBelt;

    /**
     * Runnable that resets the measurment UI state
     */
    private final Runnable resetRunable = new Runnable() {
        @Override
        public void run() {
            lineStart.setLocation(0, 0);
            lineEnd.setLocation(0, 0);
            line.reset();
            selectedLineEnd = false;
            selectedLineStart = false;
            setDirty(true);
        }
    };

    /**
     * Constructs ...
     *
     * @param toolBelt
     */
    public MeasurementLayerUI(ToolBelt toolBelt) {
        setDisplayName("Distance");
        setSettingsBuilder(new MeasurementLayerSettingsBuilder<T>(this, toolBelt.getAnnotationPersistenceService()));
        this.toolBelt = toolBelt;
        addPainter(crossHairPainter);
        addPainter(selectedObservationPainter);
        AnnotationProcessor.process(this);
    }

    @Override
    public void clearPainters() {
        super.clearPainters();
        addPainter(crossHairPainter);
        addPainter(selectedObservationPainter);
    }

    /**
     * By iteself this component does nothing with a measurement. In order to add functionality just
     * add a listener to process a measurement.
     *
     * @param listener
     */
    public void addMeasurementCompletedListener(MeasurementCompletedListener listener) {
        measurementCompletedListeners.add(listener);
    }

    /**
     * Creates a measurement from the current parameters
     * @param comment
     * @param jxl
     * @return
     */
    private Measurement newMeasurement(String comment, JXLayer<? extends T> jxl) {

        int x0 = (int) Math.round(lineStart.getX());
        int y0 = (int) Math.round(lineStart.getY());
        int x1 = (int) Math.round(lineEnd.getX());
        int y1 = (int) Math.round(lineEnd.getY());

        return new Measurement(x0, y0, x1, y1, comment);
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

    @Override
    protected void paintLayer(Graphics2D g2, JXLayer<? extends T> jxl) {
        super.paintLayer(g2, jxl);
        g2.setPaintMode();    // Make sure XOR is turned off


        // --- Draw and label existing measurements for selected observation
        g2.setPaint(Color.GREEN);
        g2.setStroke(new BasicStroke(2));
        for (MeasurementPath path : measurementPaths) {
            try {
                updateMeasurementPath(path, jxl);
                String comment = path.measurement.getComment();
                g2.draw(path.generalPath);
                g2.setFont(lineFont);
                g2.drawString(comment, (float) path.commentPoint.getX(), (float) path.commentPoint.getY());
            }
            catch (Exception e) {
                log.warn("Problem with drawing measurement path", e);
            }
        }
        g2.setPaintMode();

        // --- Paint lineStart
        g2.setStroke(new BasicStroke(2));
        g2.setPaint(Color.RED);
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

                    Measurement measurement = newMeasurement(null, jxl);
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

    @Override
    protected void processMouseMotionEvent(MouseEvent me, JXLayer<? extends T> jxl) {
        super.processMouseMotionEvent(me, jxl);

        if ((me.getID() == MouseEvent.MOUSE_MOVED) && selectedLineStart && !selectedLineEnd) {
            Point point = SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), jxl);
            int w = jxl.getWidth();
            int h = jxl.getHeight();

            /*
             * Create crosshair
             */
            line.reset();
            if ((point.y <= h) && (point.x <= w)) {
                Point2D componentPoint = jxl.getView().convertToComponent(lineStart);
                line.moveTo(componentPoint.getX(), componentPoint.getY());
                line.lineTo(point.x, point.y);
            }

            // mark the ui as dirty and needed to be repainted
            setDirty(true);
        }
    }

    /**
     * resets the ui to a known state
     */
    public void resetUI() {
        if (SwingUtilities.isEventDispatchThread()) {
            resetRunable.run();
        }
        else {
            SwingUtilities.invokeLater(resetRunable);
        }
    }

    /**
     *
     * @param newObservation
     */
    public void setObservation(Observation newObservation) {
        Observation oldObservation = this.observation;
        observation = newObservation;
        measurementPaths.clear();

        if (observation != null) {
            Collection<Association> associations = Collections2.filter(observation.getAssociations(),
                    Measurement.IS_MEASUREMENT_PREDICATE);
            for (Association association : associations) {
                try {
                    measurementPaths.add(associationTransform.apply(association));
                }
                catch (Exception e) {
                    log.warn("Unable to parse coordinates from the association, " + association);
                }
            }
        }

        resetUI();
        propertyChangeSupport.firePropertyChange(PROP_OBSERVATION, oldObservation, observation);
    }


    @EventSubscriber(eventClass = IAFRepaintEvent.class)
    public void respondsTo(IAFRepaintEvent event) {
        UIDataCoordinator dataCoordinator = event.get();
        Observation obs = (dataCoordinator.getSelectedObservations().size() == 1) ?
                dataCoordinator.getSelectedObservations().iterator().next() :
                null;
        setObservation(obs);
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

    class MeasurementPath {

        final GeneralPath generalPath = new GeneralPath();
        final Point2D commentPoint = new Point2D.Float();
        final Measurement measurement;

        MeasurementPath(Measurement measurement) {
            this.measurement = measurement;
        }
    }
}
