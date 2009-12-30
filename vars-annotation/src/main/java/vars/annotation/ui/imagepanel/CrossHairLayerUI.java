/*
 * @(#)CrossHairLayerUI.java   2009.12.29 at 03:21:13 PST
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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.AbstractLayerUI;

/**
 * Draws a crosshair over a JComponent. Use as:
 * {@code
 * JXLayer<JComponent> layer = new JXLayer<JComponent>(component);
 * layer.setUI(new CrossHairLayerUI());
 * }
 * @author brian
 */
public class CrossHairLayerUI<T extends JComponent> extends AbstractLayerUI<T> {

    private GeneralPath crosshair = new GeneralPath();

    @Override
    protected void paintLayer(Graphics2D gd, JXLayer<? extends T> jxl) {
        super.paintLayer(gd, jxl);
        gd.setXORMode(Color.WHITE);
        gd.draw(crosshair);
        gd.setPaintMode();
    }

    @Override
    protected void processMouseMotionEvent(MouseEvent me, JXLayer<? extends T> jxl) {
        super.processMouseMotionEvent(me, jxl);

        if (me.getID() == MouseEvent.MOUSE_MOVED || me.getID() == MouseEvent.MOUSE_DRAGGED) {
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
