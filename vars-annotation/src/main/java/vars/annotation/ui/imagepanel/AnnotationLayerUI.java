/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui.imagepanel;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import org.jdesktop.jxlayer.JXLayer;
import vars.UserAccount;
import vars.annotation.Observation;
import vars.annotation.VideoFrame;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.table.ObservationTable;
import vars.annotation.ui.table.ObservationTableModel;
import vars.knowledgebase.Concept;

/**
 *
 * @author brian
 */
public class AnnotationLayerUI<T extends JComponent> extends CrossHairLayerUI<T> {

    private enum MarkerStyle {
        SELECTED(new Color(0, 255, 0, 180), new Font("Sans Serif", Font.PLAIN, 10), 14, new BasicStroke(3)),
        NOTSELECTED(new Color(255, 0, 0, 180), new Font("Sans Serif", Font.PLAIN, 8), 7, new BasicStroke(3));

        private final Color color;
        private final Font font;
        private final int armLength;
        private final Stroke stroke;

        private MarkerStyle(Color color, Font font, int armLength, Stroke stroke) {
            this.color = color;
            this.font = font;
            this.armLength = armLength;
            this.stroke = stroke;
        }

    }

    /**
     * This font is used to draw the concept name of concepts.
     */
    private final Controller controller = new Controller();
    private final ToolBelt toolBelt;
    private VideoFrame videoFrame;


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

    public AnnotationLayerUI(ToolBelt toolBelt) {
        this.toolBelt = toolBelt;
    }

    @Override
    protected void paintLayer(Graphics2D g2, JXLayer<? extends T> jxl) {
        super.paintLayer(g2, jxl);
        g2.setPaintMode();           // Make sure XOR is turned off
        
        if (videoFrame != null) {

            for (Observation observation : videoFrame.getObservations()) {
                MarkerStyle markerStyle = selectedObservations.contains(observation) ? MarkerStyle.SELECTED : MarkerStyle.NOTSELECTED;
                if (observation.getX() != null && observation.getY() != null) {
                    int x = (int) Math.round(observation.getX().doubleValue());
                    int y = (int) Math.round(observation.getY().doubleValue());

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

            if (boundingBox != null) {

                // Draw the bounding box
                g2.setXORMode(Color.WHITE);
                g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[] { 2, 2 }, 2));
                g2.draw(boundingBox);
                g2.setPaintMode();
            }
        }
    }

    public VideoFrame getVideoFrame() {
        return videoFrame;
    }

    public void setVideoFrame(VideoFrame videoFrame_) {
        /*
         * We have to look up the videoframe from the database since the reference
         * passed may be stale
         */
        videoFrame = videoFrame_ == null ? null :toolBelt.getAnnotationDAOFactory().newDAO().find(videoFrame_);
        observations.clear();
        selectedObservations.clear();
        boundingBox = null;
        setDirty(true);
        if (videoFrame != null) {
            observations.addAll(Collections2.filter(videoFrame.getObservations(), new Predicate<Observation>() {
                public boolean apply(Observation input) {
                    return input.getX() != null && input.getY() != null;
                }
            }));
            setDirty(true);
        }
    }

    @Override
    protected void processMouseEvent(MouseEvent me, JXLayer<? extends T> jxl) {
        super.processMouseEvent(me, jxl);
        Point point = SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), jxl);
        switch (me.getID()) {
            case MouseEvent.MOUSE_PRESSED:
                clickPoint.setLocation( (int) Math.round(point.getX()), (int) Math.round(point.getY()));
                break;
            case MouseEvent.MOUSE_RELEASED:
                if (me.getButton() != MouseEvent.BUTTON1) {
                    selectedObservations.clear();
                    //selectedAnnotationSupport.notifyListeners(new SelectionEvent());
                    setDirty(true);
                }
                else {
                    selectedObservations.clear();
                    if (boundingBox == null) {
                        /*
                         * If the mouse is NOT being dragged then create a new Observation
                         */
                        controller.newObservation(point);

                    }
                    else {
                        Rectangle r = boundingBox.getBounds();
                        boundingBox = null;

                        for (Observation observation : observations) {
                            if (r.contains(observation.getX(), observation.getY())) {
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


    private class Controller {

        void newObservation(Point point) {
            UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();
            if (userAccount != null && videoFrame != null) {
                Observation observation = toolBelt.getAnnotationFactory().newObservation();
                Concept root = toolBelt.getAnnotationPersistenceService().findRootConcept();
                observation.setConceptName(root.getPrimaryConceptName().getName());
                observation.setObservationDate(new Date());
                observation.setObserver(userAccount.getUserName());
                observation.setX(point.getX());
                observation.setY(point.getY());
                // The persistence controller will trigger an update to setVideoFrame on this class
                toolBelt.getPersistenceController().insertObservation(videoFrame, observation);
            }
        }

        void sendSelectionNotification() {
            ObservationTable table = (ObservationTable) Lookup.getObservationTableDispatcher().getValueObject();
            ObservationTableModel model = (ObservationTableModel) ((JTable) table).getModel();
            ListSelectionModel selectionModel = ((JTable) table).getSelectionModel();
            selectionModel.clearSelection();
            for (Observation observation : selectedObservations) {
                int row = model.getObservationRow(observation);
                selectionModel.addSelectionInterval(row, row);
            }
        }

    }

}
