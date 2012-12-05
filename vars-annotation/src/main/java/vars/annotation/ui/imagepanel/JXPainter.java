/*
 * @(#)JXPainter.java   2012.11.26 at 08:48:31 PST
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
 * Adjunct interface that allows us to add additional drawing/painting to a JXLayer object based. This class is
 * designed to add custom views to a LayerUI based on some view of Model data.
 *
 * @author Brian Schlining
 * @since 2011-12-21
 * @param <A> The type passed to the JXLayerUI that is being painted
 */
public interface JXPainter<A extends JComponent> {

    PropertyChangeSupport getPropertyChangeSupport();

    boolean isDirty();

    void paintLayer(Graphics2D g2, JXLayer<? extends A> jxl);

    void processMouseEvent(MouseEvent me, JXLayer<? extends A> jxl);

    void processMouseMotionEvent(MouseEvent me, JXLayer<? extends A> jxl);

    void setDirty(boolean dirty);
}
