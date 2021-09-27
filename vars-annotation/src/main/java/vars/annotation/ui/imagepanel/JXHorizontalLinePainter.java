package vars.annotation.ui.imagepanel;

import org.jdesktop.jxlayer.JXLayer;
import mbarix4j.swing.JImageCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This painter draws horizontal line overlays on a JImageCanvas. The lines are given as percentage of image
 * height with the y origin at the top of the image.
 *
 * @author Brian Schlining
 * @since 2014-11-19T11:27:00
 */
public class JXHorizontalLinePainter<A extends JComponent> extends AbstractJXPainter<A> {

    private Stroke stroke = new BasicStroke(2);
    private Collection<HorizontalLine> horizontalLines = new ArrayList<HorizontalLine>();
    private final JImageCanvas imageCanvas;

    public JXHorizontalLinePainter(JImageCanvas imageCanvas) {
        this.imageCanvas = imageCanvas;
    }

    public Collection<Double> getDistances() {
        Collection<Double> distances = new ArrayList<Double>(horizontalLines.size());
        for (HorizontalLine p : horizontalLines) {
            distances.add(p.percent);
        }
        return distances;
    }

    /**
     * Set the horizontal lines. distances are percentage of height with zero being the
     * top of the image.
     *
     * @param distances
     */
    public void setDistances(Collection<Double> distances) {
        horizontalLines.clear();
        for (Double d : distances) {
            horizontalLines.add(new HorizontalLine(d));
        }
        setDirty(true);
    }

    @Override
    public void paintLayer(Graphics2D g2, JXLayer<? extends A> jxl) {
        g2.setStroke(stroke);
        g2.setPaint(Color.MAGENTA);
        //g2.setXORMode(Color.GREEN);
        int w = jxl.getWidth();
        int h = jxl.getHeight();
        for (HorizontalLine line : horizontalLines) {
            line.update(w);
            g2.draw(line.path);
        }
        g2.setPaintMode();
    }


    class HorizontalLine {
        final GeneralPath path = new GeneralPath();
        final double percent;

        HorizontalLine(double percent) {
            this.percent = percent;
        }

        void update(int w) {
            double iy = imageCanvas.getImageHeight() * percent;
            Point2D cp = imageCanvas.convertToComponent(new Point2D.Double(0, iy));
            path.reset();
            double y = cp.getY();
            path.moveTo(0, y);
            path.lineTo(w, y);
        }
    }
}
