/*
 * @(#)MultiLayerUI.java   2012.11.26 at 08:48:27 PST
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
import org.jdesktop.jxlayer.plaf.AbstractLayerUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JComponent;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Brian Schlining
 * @since 2012-08-02
 *
 * @param <T>
 */
public class MultiLayerUI<T extends JComponent> extends AbstractLayerUI<T> {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private Queue<JXPainter<T>> painters = new LinkedBlockingQueue<JXPainter<T>>();

    /**
     * Used to listen to the setDirty flag set by the painters
     */
    private final PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equalsIgnoreCase("dirty")) {
                setDirty(true);
            }
        }
    };

    /**
     *
     * @param painter
     */
    public void addPainter(JXPainter<T> painter) {
        painters.add(painter);
        painter.getPropertyChangeSupport().addPropertyChangeListener(propertyChangeListener);
        setDirty(true);
    }

    /**
     */
    public void clearPainters() {
        for (JXPainter painter : painters) {
            painter.getPropertyChangeSupport().removePropertyChangeListener(propertyChangeListener);
        }
        painters.clear();
        setDirty(true);
    }

    @Override
    protected void paintLayer(Graphics2D g2, JXLayer<? extends T> jxl) {
        super.paintLayer(g2, jxl);
        for (JXPainter<T> painter : painters) {

            //log.debug("Painting: " + painter);
            painter.paintLayer(g2, jxl);
        }
    }

    @Override
    protected void processMouseEvent(MouseEvent me, JXLayer<? extends T> jxl) {

        //System.out.println(getClass().getSimpleName() + ".processMouseEvent -> MouseEvent: " + me.getID());
        super.processMouseEvent(me, jxl);
        boolean dirty = false;
        for (JXPainter<T> painter : painters) {
            painter.processMouseEvent(me, jxl);
            if (painter.isDirty()) {
                dirty = true;
            }
        }
        if (dirty) {
            setDirty(dirty);
        }
    }

    @Override
    protected void processMouseMotionEvent(MouseEvent me, JXLayer<? extends T> jxl) {

        //System.out.println(getClass().getSimpleName() + ".processMouseMotionEvent -> MouseEvent: " + me.getID());
        super.processMouseMotionEvent(me, jxl);
        boolean dirty = false;
        for (JXPainter<T> painter : painters) {
            painter.processMouseMotionEvent(me, jxl);
            if (painter.isDirty()) {
                dirty = true;
            }
        }
        if (dirty) {
            setDirty(dirty);
        }
    }

    /**
     *
     * @param painter
     */
    public void removePainter(JXPainter<T> painter) {
        painters.remove(painter);
        painter.getPropertyChangeSupport().removePropertyChangeListener(propertyChangeListener);
        setDirty(true);
    }
}
