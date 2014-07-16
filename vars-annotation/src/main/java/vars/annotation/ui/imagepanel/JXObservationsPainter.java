/*
 * @(#)JXObservationsPainter.java   2012.11.26 at 08:48:31 PST
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

import org.jdesktop.jxlayer.JXLayer;
import org.mbari.awt.AwtUtilities;
import org.mbari.swing.JImageUrlCanvas;
import vars.annotation.Observation;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
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
 *
 * @param <T>
 */
public class JXObservationsPainter<T extends JImageUrlCanvas> extends AbstractJXPainter<T> {

    private Set<Observation> observations = Collections.synchronizedSet(new HashSet<Observation>());
    private final boolean drawConceptName;
    private final boolean drawTimecode;
    private IMarkerStyle markerStyle;

    /**
     * Constructs ...
     *
     * @param markerStyle
     * @param drawConceptName
     * @param drawTimecode
     */
    public JXObservationsPainter(IMarkerStyle markerStyle, boolean drawConceptName, boolean drawTimecode) {
        this.markerStyle = markerStyle;
        this.drawTimecode = drawTimecode;
        this.drawConceptName = drawConceptName;
    }

    public IMarkerStyle getMarkerStyle() {
        return markerStyle;
    }

    public void setMarkerStyle(IMarkerStyle markerStyle) {
        this.markerStyle = markerStyle;
        setDirty(true);
    }

    /**
     *
     * @return A copy of the internal collection of observations
     */
    public Set<Observation> getObservations() {
        return new HashSet<Observation>(observations);
    }

    /**
     *
     * @param g2
     * @param jxl
     */
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

                    g2.setStroke(markerStyle.getStroke());
                    g2.setPaint(markerStyle.getColor());

                    // Draw the annotation
                    int armLength = markerStyle.getArmLength();
                    GeneralPath gp = new GeneralPath();
                    gp.moveTo(x - armLength, y - armLength);
                    gp.lineTo(x + armLength, y + armLength);
                    gp.moveTo(x + armLength, y - armLength);
                    gp.lineTo(x - armLength, y + armLength);
                    g2.draw(gp);

                    // Write the concept name
                    g2.setFont(markerStyle.getFont());
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

                }
            }
        }
    }

    /**
     * Set the internal collection of observations to be drawn. A copy of the collection is used.
     * @param observations
     */
    public void setObservations(Collection<Observation> observations) {
        synchronized (this.observations) {
            this.observations.clear();
            this.observations.addAll(observations);
        }
        setDirty(true);
    }
}
