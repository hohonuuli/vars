/*
 * @(#)AreaMeasurementLayerUI.java   2011.12.19 at 04:18:51 PST
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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jdesktop.jxlayer.JXLayer;
import org.mbari.awt.AwtUtilities;
import org.mbari.swing.JImageUrlCanvas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.ILink;
import vars.ToolBelt;
import vars.annotation.Association;
import vars.annotation.Observation;
import vars.annotation.ObservationDAO;
import vars.annotation.ui.Lookup;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Brian Schlining
 * @since 2011-10-27
 *
 * @param <T>
 */
public class AreaMeasurementLayerUI<T extends JImageUrlCanvas> extends CrossHairLayerUI<T> {

    /*

        NOTES:

        IC = image coordinates, e.g. pixels. This can be stored but need to be converted to
            component coordinates to be drawn.

        CC = component coordinates, e.g. suitable for drawing with Graphics2D

     */

    /** The diameter of the start of measurement marker */
    private static final int markerDiameter = 10;
    private Logger log = LoggerFactory.getLogger(getClass());
    private final Font lineFont = new Font("Sans Serif", Font.PLAIN, 10);

    /** Collection of pointsIC to be used to create the AreaMeasurement in image pixels */
    private List<org.mbari.geometry.Point2D<Integer>> pointsIC = new CopyOnWriteArrayList<org.mbari.geometry
        .Point2D<Integer>>();

    /** Path used to draw measurement polygonCC in component coordinates*/
    private GeneralPath polygonCC = new GeneralPath();

    /** The current point where the mouse is in component coordinates*/
    private Point2D currentPointCC = new Point2D.Double();

    /**
     * Path used to draw the triangle that will be added to the polygonCC if user addes another
     * click. In component coordiantes*/
    private GeneralPath bridgeCC = new GeneralPath();
    
    /** Need a synchronized collection */
    private final Collection<AreaMeasurementPath> areaMeasurementPaths = new CopyOnWriteArrayList<AreaMeasurementPath>();

    /** Synchronized collection of observations in the same videoframe */
    private final Collection<Observation> relatedObservations = new CopyOnWriteArraySet<Observation>();
    private final Predicate<Observation> matchObservationPredicate = new Predicate<Observation>() {

        @Override
        public boolean apply(Observation input) {
            return input.equals(observation);
        }
    };

    /** Transform that converts an association to an AreaMeasurement object */
    private final Function<ILink, AreaMeasurementPath> associationTransform = new Function<ILink,
        AreaMeasurementPath>() {

        @Override
        public AreaMeasurementPath apply(ILink input) {
            return new AreaMeasurementPath(AreaMeasurement.LINK_TO_AREA_MEASUREMENT_TRANSFORM.apply(input));
        }
    };

    /**
     * Runnable that resets the measurment UI state
     */
    private final Runnable resetRunable = new Runnable() {

        @Override
        public void run() {

            // TODO finish implementation
            polygonCC.reset();
            bridgeCC.reset();
            //areaMeasurementPaths.clear();
            pointsIC.clear();
            //relatedObservations.clear();
            setDirty(true);
        }
    };

    /** The observation that we're currently adding measurements to */
    private Observation observation;
    private final ToolBelt toolBelt;

    /**
     * Constructs ...
     *
     * @param toolBelt
     */
    public AreaMeasurementLayerUI(ToolBelt toolBelt) {
        this.toolBelt = toolBelt;
        AnnotationProcessor.process(this);
        Collection<Observation> selectedObservations = (Collection<Observation>) Lookup.getSelectedObservationsDispatcher().getValueObject();
        respondsTo(new ObservationsSelectedEvent(null, selectedObservations));
    }

    private AreaMeasurement newAreaMeasurement(String comment) {
        return new AreaMeasurement(new ArrayList<org.mbari.geometry.Point2D<Integer>>(pointsIC), comment);
    }

    @Override
    protected void paintLayer(Graphics2D g2, JXLayer<? extends T> jxl) {
        super.paintLayer(g2, jxl);
        g2.setPaintMode();    // Make sure xor is turned off
        if (observation != null) {

            // --- Paint related observations
            for (Observation obs : relatedObservations) {
                if ((obs.getX() != null) && (obs.getY() != null)) {

                    Point2D imagePoint = new Point2D.Double(obs.getX(), obs.getY());
                    Point2D componentPoint2D = jxl.getView().convertToComponent(imagePoint);
                    if (componentPoint2D != null) {
                        Point componentPoint = AwtUtilities.toPoint(componentPoint2D);
                        int x = componentPoint.x;
                        int y = componentPoint.y;

                        MarkerStyle markerStyle = obs.equals(observation) ? MarkerStyle.NOTSELECTED : MarkerStyle.FAINT;

                        g2.setStroke(markerStyle.stroke);
                        g2.setPaint(markerStyle.color);

                        // Write the concept name
                        g2.setFont(markerStyle.font);
                        g2.drawString(obs.getConceptName(), x + 5, y);

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

            // --- Draw and label existing measurements for selected observations
            g2.setPaint(Color.GREEN);
            g2.setStroke(new BasicStroke(2));
            for (AreaMeasurementPath path : areaMeasurementPaths) {
                try {
                    updateAreaMeasurementPath(path, jxl);
                    String comment = path.areaMeasurement.getComment();
                    g2.draw(path.generalPath);
                    g2.setFont(lineFont);
                    if (!Strings.isNullOrEmpty(comment)) {
                        g2.drawString(comment, (float) path.commentPoint.getX(), (float) path.commentPoint.getY());
                    }
                }
                catch (Exception e) {
                    log.warn("Problem with drawing area measurement path", e);
                }
            }

            polygonCC.reset();
            if (pointsIC.size() > 0) {

                // --- Paint current area measurement
                Color color = (pointsIC.size() < 3) ? Color.RED : Color.CYAN;
                g2.setStroke(new BasicStroke(2));
                g2.setPaint(color);

                // TODO polygonCC should start and end with the currentPointCC marker.
                for (int i = 0; i < pointsIC.size(); i++) {
                    org.mbari.geometry.Point2D<Integer> coordinate = pointsIC.get(i);
                    Point2D imagePoint = new Point2D.Double(coordinate.getX(), coordinate.getY());
                    Point2D componentPoint = jxl.getView().convertToComponent(imagePoint);
                    if (i == 0) {
                        polygonCC.moveTo(componentPoint.getX(), componentPoint.getY());
                    }
                    else {
                        polygonCC.lineTo(componentPoint.getX(), componentPoint.getY());
                        polygonCC.moveTo(componentPoint.getX(), componentPoint.getY());
                    }
                }

                // Close path

                org.mbari.geometry.Point2D<Integer> coordinate = pointsIC.get(0);
                Point2D imagePoint = new Point2D.Double(coordinate.getX(), coordinate.getY());
                Point2D componentPoint = jxl.getView().convertToComponent(imagePoint);
                polygonCC.lineTo(componentPoint.getX(), componentPoint.getY());
                g2.draw(polygonCC);

                // --- Draw current mouse point as a hint to where next point will be placed
                final int markerOffset = markerDiameter / 2;
                if (pointsIC.size() > 1) {
                    g2.setPaint(Color.RED);
                    g2.draw(bridgeCC);
                    Point p = AwtUtilities.toPoint(currentPointCC);
                    Ellipse2D marker = new Ellipse2D.Double(p.x - markerOffset, p.y - markerOffset, markerDiameter,
                        markerDiameter);
                    g2.draw(marker);
                }
            }

        }
    }

    @Override
    protected void processMouseEvent(MouseEvent me, JXLayer<? extends T> jxl) {
        super.processMouseEvent(me, jxl);
        Point point = SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), jxl);
        switch (me.getID()) {
        case MouseEvent.MOUSE_PRESSED:
            Point2D imagePoint = jxl.getView().convertToImage(point);
            int x = (int) Math.round(imagePoint.getX());
            int y = (int) Math.round(imagePoint.getY());
            if (me.getClickCount() == 1 && (me.getButton() == MouseEvent.BUTTON1)) {
                pointsIC.add(new org.mbari.geometry.Point2D<Integer>(x, y));
                setDirty(true);
            }
            else if ((me.getClickCount() == 2) || (me.getButton() != MouseEvent.BUTTON1)) {
                if (pointsIC.size() > 2 && observation != null) {
                    // --- Publish action via EventBus
                    AreaMeasurement areaMeasurement = newAreaMeasurement(null);
                    AddAreaMeasurementEvent event = new AddAreaMeasurementEvent(observation, areaMeasurement);
                    EventBus.publish(event);
                }
                resetUI();
                setObservation(observation);
            }
        default:
            // Do nothing
        }
    }

    @Override
    protected void processMouseMotionEvent(MouseEvent me, JXLayer<? extends T> jxl) {
        super.processMouseMotionEvent(me, jxl);
        if ((me.getID() == MouseEvent.MOUSE_MOVED) && (pointsIC.size() > 1)) {
            Point point = SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), jxl);
            currentPointCC.setLocation(point.getX(), point.getY());
            int w = jxl.getWidth();
            int h = jxl.getHeight();

            bridgeCC.reset();
            if ((point.y <= h) && (point.x <= w) && (point.y >= 0) && (point.x >= 0)) {
                Point2D start = jxl.getView().convertToComponent(pointsIC.get(0).toJavaPoint2D());
                Point2D end = jxl.getView().convertToComponent(pointsIC.get(pointsIC.size() - 1).toJavaPoint2D());
                bridgeCC.moveTo(start.getX(), start.getY());
                bridgeCC.lineTo(currentPointCC.getX(), currentPointCC.getY());
                bridgeCC.lineTo(end.getX(), end.getY());
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
     * @param event
     */
    @EventSubscriber(eventClass = ObservationsSelectedEvent.class)
    public void respondsTo(ObservationsSelectedEvent event) {
        Observation selectedObservation = (event.get().size() == 1) ? event.get().iterator().next() : null;
        setObservation(selectedObservation);
    }

    /**
     *
     * @param event
     */
    @EventSubscriber(eventClass = ObservationsChangedEvent.class)
    public void respondsTo(ObservationsChangedEvent event) {
        Collection<Observation> matchingObservation = Collections2.filter(event.get(), matchObservationPredicate);
        if (!matchingObservation.isEmpty()) {
            setObservation(matchingObservation.iterator().next());
        }
    }

    /**
     *
     * @param observation
     */
    public void setObservation(Observation observation) {
        Observation oldObservation = this.observation;
        this.observation = observation;
        areaMeasurementPaths.clear();
        relatedObservations.clear();
        resetUI();
        if (observation != null) {

            // DAOTX: Lookup selected observation from database. Otherwise, you may not get all the
            // related observations
            ObservationDAO observationDAO = toolBelt.getAnnotationDAOFactory().newObservationDAO();
            observationDAO.startTransaction();
            observation = observationDAO.find(observation);
            relatedObservations.addAll(observation.getVideoFrame().getObservations());
            observationDAO.endTransaction();
            observationDAO.close();

            Collection<Association> associations = Collections2.filter(observation.getAssociations(),
                AreaMeasurement.IS_AREA_MEASUREMENT_PREDICATE);
            for (Association association : associations) {
                try {
                    areaMeasurementPaths.add(associationTransform.apply(association));
                }
                catch (Exception e) {
                    log.warn("Unable to parse coordinates from the association, " + association);
                }
            }
        }

    }

    private void updateAreaMeasurementPath(AreaMeasurementPath areaMeasurementPath, JXLayer<? extends T> jxl) {

        AreaMeasurement areaMeasurement = areaMeasurementPath.areaMeasurement;
        GeneralPath generalPath = areaMeasurementPath.generalPath;
        generalPath.reset();

        List<org.mbari.geometry.Point2D<Integer>> coordinates = areaMeasurement.getCoordinates();

        double sumX = 0;
        double sumY = 0;
        for (int i = 0; i < coordinates.size(); i++) {
            org.mbari.geometry.Point2D<Integer> coordinate = coordinates.get(i);
            Point2D imagePoint = new Point2D.Double(coordinate.getX(), coordinate.getY());
            Point2D componentPoint = jxl.getView().convertToComponent(imagePoint);
            if (i == 0) {
                generalPath.moveTo(componentPoint.getX(), componentPoint.getY());
            }
            else {
                generalPath.lineTo(componentPoint.getX(), componentPoint.getY());
            }
            sumX = sumX + componentPoint.getX();
            sumY = sumY + componentPoint.getY();
        }

        // Close path
        org.mbari.geometry.Point2D<Integer> coordinate = coordinates.get(0);
        Point2D imagePoint = new Point2D.Double(coordinate.getX(), coordinate.getY());
        Point2D componentPoint = jxl.getView().convertToComponent(imagePoint);
        generalPath.lineTo(componentPoint.getX(), componentPoint.getY());

        // set comment point
        double cx = (sumX / coordinates.size()) + 5;
        double cy = (sumY / coordinates.size());
        areaMeasurementPath.commentPoint.setLocation(cx, cy);

    }

    class AreaMeasurementPath {

        final GeneralPath generalPath = new GeneralPath();
        final Point2D commentPoint = new Point2D.Float();
        final AreaMeasurement areaMeasurement;

        AreaMeasurementPath(AreaMeasurement areaMeasurement) {
            this.areaMeasurement = areaMeasurement;
        }
    }
}
