/*
 * @(#)JXAreaMeasurementPainter.java   2013.02.04 at 03:23:13 PST
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
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JComponent;
import org.jdesktop.jxlayer.JXLayer;
import org.mbari.swing.JImageUrlCanvas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.ILink;
import vars.annotation.Association;

/**
 * @author Brian Schlining
 * @since 2013-02-04
 *
 * @param <T>
 */
public class JXAreaMeasurementPainter<T extends JImageUrlCanvas> extends AbstractJXPainter<T> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    /** Transform that converts an association to an AreaMeasurement object */
    private final Function<ILink, AreaMeasurementPath> associationTransform = new Function<ILink,
        AreaMeasurementPath>() {

        @Override
        public AreaMeasurementPath apply(ILink input) {
            return new AreaMeasurementPath(AreaMeasurement.LINK_TO_AREA_MEASUREMENT_TRANSFORM.apply(input));
        }
    };

    /** Need a synchronized collection */
    private final Collection<AreaMeasurementPath> areaMeasurementPaths = new CopyOnWriteArrayList<AreaMeasurementPath>();
    private final Font lineFont;
    private Color paint;
    private final Stroke stroke;

    /**
     * Constructs ...
     */
    public JXAreaMeasurementPainter() {
        this(new Font("Sans Serif", Font.PLAIN, 10), Color.GREEN, new BasicStroke(2));
    }

    /**
     * Constructs ...
     *
     * @param font
     * @param paint
     * @param stroke
     */
    public JXAreaMeasurementPainter(Font font, Color paint, Stroke stroke) {
        this.lineFont = font;
        this.paint = paint;
        this.stroke = stroke;
    }

    /**
     *
     * @param g2
     * @param jxl
     */
    public void paintLayer(Graphics2D g2, JXLayer<? extends T> jxl) {
        super.paintLayer(g2, jxl);

        // --- Draw and label existing measurements for selected observations
        g2.setPaint(paint);
        g2.setStroke(stroke);
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
    }

    /**
     *
     * @param associations
     */
    public void setAssociations(Collection<Association> associations) {
        areaMeasurementPaths.clear();
        Collection<Association> amAssociations = Collections2.filter(associations,
            AreaMeasurement.IS_AREA_MEASUREMENT_PREDICATE);
        for (Association association : amAssociations) {
            try {
                areaMeasurementPaths.add(associationTransform.apply(association));
            }
            catch (Exception e) {
                log.warn("Unable to parse coordinates from the association, " + association);
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

    public Color getPaint() {
        return paint;
    }

    public void setPaint(Color paint) {
        this.paint = paint;
    }
}
