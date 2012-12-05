/*
 * @(#)JXCrossHairPainter.java   2012.11.26 at 08:48:33 PST
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

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;

/**
 * JXPainter that draws the current mouse location as cross hairs
 *
 * @author Brian Schlining
 * @since 2012-08-02
 *
 * @param <A>
 */
public class JXCrossHairPainter<A extends JComponent> extends AbstractJXPainter<A> {

    private GeneralPath crosshair = new GeneralPath();
    private final Stroke stroke = new BasicStroke(1);

    /**
     *
     * @param gd
     * @param jxl
     */
    @Override
    public void paintLayer(Graphics2D gd, JXLayer<? extends A> jxl) {
        gd.setStroke(stroke);
        gd.setPaint(Color.GRAY);
        gd.setXORMode(Color.WHITE);
        gd.draw(crosshair);
        gd.setPaintMode();
    }

    /**
     *
     * @param me
     * @param jxl
     */
    @Override
    public void processMouseMotionEvent(MouseEvent me, JXLayer jxl) {
        if ((me.getID() == MouseEvent.MOUSE_MOVED) || (me.getID() == MouseEvent.MOUSE_DRAGGED)) {
            Point point = SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), jxl);
            int w = jxl.getWidth();
            int h = jxl.getHeight();

            /*
            * Create crosshair
            */
            crosshair.reset();

            if (point.y <= h) {
                crosshair.moveTo(0, point.y);
                crosshair.lineTo(w, point.y);
            }

            if (point.x <= w) {
                crosshair.moveTo(point.x, 0);
                crosshair.lineTo(point.x, h);
            }

            // mark the ui as dirty and needed to be repainted
            setDirty(true);
        }
    }
}
