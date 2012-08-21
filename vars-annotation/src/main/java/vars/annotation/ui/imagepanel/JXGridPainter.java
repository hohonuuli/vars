package vars.annotation.ui.imagepanel;

import Jama.Matrix;
import org.jdesktop.jxlayer.JXLayer;
import org.mbari.geometry.Point2D;

import static java.lang.Math.*;

import javax.swing.JComponent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Brian Schlining
 * @since 2012-08-02
 */
public class JXGridPainter<A extends JComponent> extends AbstractJXPainter<A> {

    private int pixelWidth;
    private int pixelHeight;
    private double angle;
    private double pixelDistance; // distance on a side for a square
    private int centerX;
    private int centerY;

    @Override
    public void paintLayer(Graphics2D g2, JXLayer<? extends A> jxl) {

        // --- Paint
        g2.setStroke(new BasicStroke(2));
        g2.setPaint(Color.WHITE);

        List<GeneralPath> lines = buildLines();
        for(GeneralPath line: lines) {
            g2.draw(line);
        }

    }


    private List<GeneralPath> buildLines() {

        int numberOfLinesX = (int) Math.floor(pixelWidth / pixelDistance);
        double[] xx = new double[numberOfLinesX];
        for (int i = 0; i < numberOfLinesX; i++) {
            xx[i] = pixelDistance * i;
        }

        int numberOfLinesY = (int) Math.floor(pixelHeight / pixelDistance);
        double[] yy = new double[numberOfLinesY];
        for (int i = 0; i < numberOfLinesY; i++) {
            yy[i] = pixelDistance * i;
        }

        Matrix rotMat = new Matrix(new double[][] {
                {1, 0, 0, 0},
                {0, cos(angle), -sin(angle), 0},
                {0, sin(angle), cos(angle), 0},
                {0, 0, 0, 1}
        });

        // --- Caculate top x
        Matrix topX = new Matrix(new double[][] {
                xx,
                narray(xx.length, 0),
                narray(xx.length, 0),
                narray(xx.length, 1)
        });
        Matrix virtualTopX = rotMat.times(topX);

        // --- Calculate bottom x
        Matrix bottomX = new Matrix(new double[][]{
                xx,
                narray(xx.length, pixelHeight),
                narray(xx.length, 0),
                narray(xx.length, 1)
        });
        Matrix virtualBottomX = rotMat.times(bottomX);

        List<GeneralPath> lines = new ArrayList<GeneralPath>();
        GeneralPath line = new GeneralPath();
        for (int i = 0; i < numberOfLinesX; i++) {
            double xtop = virtualTopX.get(0, i);
            double ytop = virtualTopX.get(1, i);
            double xbottom = virtualBottomX.get(0, i);
            double ybottom = virtualBottomX.get(1, i);
            line.moveTo(xtop, ytop);
            line.lineTo(xbottom, ybottom);
        }

        lines.add(line);

        return lines;
    }

    private double[] narray(int n, double value) {
        double[] v = new double[n];
        for (int i = 0; i < n; i++) {
            v[i] = value;
        }
        return v;
    }





    public void setPixelWidth(int pixelWidth) {
        this.pixelWidth = pixelWidth;
        calculateCenter();
    }

    public void setPixelHeight(int pixelHeight) {
        this.pixelHeight = pixelHeight;
        calculateCenter();
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void setPixelDistance(double pixelDistance) {
        this.pixelDistance = pixelDistance;
    }

    public void calculateCenter() {
        centerX = pixelWidth / 2;
        centerY = pixelHeight / 2;
    }

    private class PointPair {
        Point2D p1;
        Point2D p2;
    }
}
