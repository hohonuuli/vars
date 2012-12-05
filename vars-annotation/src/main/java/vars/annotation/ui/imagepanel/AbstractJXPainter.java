/*
 * @(#)AbstractJXPainter.java   2012.11.26 at 08:48:39 PST
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
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeSupport;

/**
 * Basic implementation of a JXPainter. The dirty property is implemented. All other methods are
 * empty implementations.
 *
 * @author Brian Schlining
 * @since 2012-08-02
 *
 * @param <A>
 */
public abstract class AbstractJXPainter<A extends JComponent> implements JXPainter<A> {

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private boolean dirty;

    /**
     * @return
     */
    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    /**
     * @return
     */
    @Override
    public boolean isDirty() {
        return dirty;
    }

    /**
     *
     * @param g2
     * @param jxl
     */
    @Override
    public void paintLayer(Graphics2D g2, JXLayer<? extends A> jxl) {

        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     *
     * @param me
     * @param jxl
     */
    @Override
    public void processMouseEvent(MouseEvent me, JXLayer<? extends A> jxl) {

        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     *
     * @param me
     * @param jxl
     */
    @Override
    public void processMouseMotionEvent(MouseEvent me, JXLayer<? extends A> jxl) {

        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     *
     * @param dirty
     */
    @Override
    public void setDirty(boolean dirty) {
        boolean oldDirty = this.dirty;
        this.dirty = dirty;
        propertyChangeSupport.firePropertyChange("dirty", oldDirty, dirty);
    }
}
