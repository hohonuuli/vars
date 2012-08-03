package vars.annotation.ui.imagepanel;


import com.google.common.base.Predicate;
import org.jdesktop.jxlayer.JXLayer;
import org.mbari.awt.AwtUtilities;
import org.mbari.swing.JImageUrlCanvas;
import vars.annotation.Observation;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A Painter that will draw Observations using a given MarkerStyle
 *
 * @author Brian Schlining
 * @since 2012-08-03
 */
public class JXObservationsPainter<T extends JImageUrlCanvas>
        extends AbstractJXPainter<T> {

    private Set<Observation> observations = Collections.synchronizedSet(new HashSet<Observation>());
    private final MarkerStyle markerStyle;
    private final boolean drawTimecode;
    private final boolean drawConceptName;

    public JXObservationsPainter(MarkerStyle markerStyle, boolean drawConceptName, boolean drawTimecode) {
        this.markerStyle = markerStyle;
        this.drawTimecode = drawTimecode;
        this.drawConceptName = drawConceptName;
    }

    public void setObservations(Collection<Observation> observations) {
        synchronized (this.observations) {
            this.observations.clear();
            this.observations.addAll(observations);
        }
        setDirty(true);
    }

    @Override
    public void paintLayer(Graphics2D g2, JXLayer<? extends T> jxl) {
        super.paintLayer(g2, jxl);
        for (Observation observation : observations) {
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
                    if (drawConceptName) {
                        x = x + 5;
                        g2.drawString(observation.getConceptName(), x, y);
                    }

                    // Write time code
                    if (drawTimecode) {
                        FontMetrics fontMetrics = g2.getFontMetrics();
                        String timecode = observation.getVideoFrame().getTimecode();
                        Rectangle2D rect = fontMetrics.getStringBounds(timecode, g2);
                        y = (int) (y + rect.getHeight() + 5);
                        g2.drawString(timecode, x, y);
                    }

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
    }

}
