/*
 * @(#)JXGridPainter.java   2012.11.26 at 08:48:33 PST
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

import Jama.Matrix;
import org.jdesktop.jxlayer.JXLayer;
import org.mbari.geometry.Point2D;

import javax.swing.JComponent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * JXPainter that draws an overlaying perspective grid
 *
 * @author Brian Schlining
 * @since 2012-08-02
 *
 * @param <A>
 */
public class JXGridPainter<A extends JComponent> extends AbstractJXPainter<A> {

    private double angle;
    private int centerX;
    private int centerY;
    private double pixelDistance;    // distance on a side for a square
    private int pixelHeight;
    private int pixelWidth;

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
            { 1, 0, 0, 0 }, { 0, cos(angle), -sin(angle), 0 }, { 0, sin(angle), cos(angle), 0 }, { 0, 0, 0, 1 }
        });

        // --- Caculate top x
        Matrix topX = new Matrix(new double[][] {
            xx, narray(xx.length, 0), narray(xx.length, 0), narray(xx.length, 1)
        });
        Matrix virtualTopX = rotMat.times(topX);

        // --- Calculate bottom x
        Matrix bottomX = new Matrix(new double[][] {
            xx, narray(xx.length, pixelHeight), narray(xx.length, 0), narray(xx.length, 1)
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

    /**
     */
    public void calculateCenter() {
        centerX = pixelWidth / 2;
        centerY = pixelHeight / 2;
    }

    private double[] narray(int n, double value) {
        double[] v = new double[n];
        for (int i = 0; i < n; i++) {
            v[i] = value;
        }

        return v;
    }

    /**
     *
     * @param g2
     * @param jxl
     */
    @Override
    public void paintLayer(Graphics2D g2, JXLayer<? extends A> jxl) {

        // --- Paint
        g2.setStroke(new BasicStroke(2));
        g2.setPaint(Color.WHITE);

        List<GeneralPath> lines = buildLines();
        for (GeneralPath line : lines) {
            g2.draw(line);
        }

    }

    /**
     *
     * @param angle
     */
    public void setAngle(double angle) {
        this.angle = angle;
    }

    /**
     *
     * @param pixelDistance
     */
    public void setPixelDistance(double pixelDistance) {
        this.pixelDistance = pixelDistance;
    }

    /**
     *
     * @param pixelHeight
     */
    public void setPixelHeight(int pixelHeight) {
        this.pixelHeight = pixelHeight;
        calculateCenter();
    }

    /**
     *
     * @param pixelWidth
     */
    public void setPixelWidth(int pixelWidth) {
        this.pixelWidth = pixelWidth;
        calculateCenter();
    }

    private class PointPair {

        Point2D p1;
        Point2D p2;
    }
}
