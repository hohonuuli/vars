/*
 * @(#)DistanceMeasurementLayerUI.java   2011.07.25 at 01:22:47 PDT
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
import org.mbari.swing.JImageUrlCanvas;
import vars.annotation.Observation;
import vars.annotation.ui.ToolBelt;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeSupport;

/**
 * @author Brian Schlining
 * @since 2011-07-22
 *
 * @param <T>
 */
public class DistanceMeasurementLayerUI<T extends JImageUrlCanvas> extends AnnotationLayerUI<T> {

    private Point2D lineStart = new Point2D.Double();
    private Point2D lineEnd = new Point2D.Double();
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private boolean measuring;
    private boolean selectedLineEnd;
    private boolean selectedLineStart;
    private Observation selectedObservation;

    /**
     * Constructs ...
     *
     * @param toolBelt
     */
    public DistanceMeasurementLayerUI(ToolBelt toolBelt) {
        super(toolBelt);
    }

    /**
     * Handle to the class that handles property changes. Used to hang listeners onto the
     * 'measuring' property.
     *
     * @return
     */
    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    /**
     * @return
     */
    public Observation getSelectedObservation() {
        return selectedObservation;
    }

    /**
     * @return
     */
    public boolean isMeasuring() {
        return measuring;
    }

    @Override
    protected void paintLayer(Graphics2D g2, JXLayer<? extends T> jxl) {
        if (measuring &&
                (selectedObservation != null) &&
                (getVideoFrame() != null) &&
                selectedLineStart) {
            g2.setPaintMode();    // Make sure XOR is turned off

        }
        else {
            // Not measuring
            super.paintLayer(g2, jxl);
        }

    }

    @Override
    protected void processMouseEvent(MouseEvent me, JXLayer<? extends T> jxl) {
        super.processMouseEvent(me, jxl);

        // TODO on first click set lineStart value
        // TODO on second click set lineEnd value, generate association, set measuring property to false

    }

    @Override
    protected void processMouseMotionEvent(MouseEvent me, JXLayer<? extends T> jxl) {
        super.processMouseMotionEvent(me, jxl);

        // TODO if selectedLineStart is true draw line from lineStart to cursur
    }

    /**
     *
     * @param measuring
     */
    public void setMeasuring(boolean measuring) {
        boolean oldMeasuring = this.measuring;
        this.measuring = measuring;
        propertyChangeSupport.firePropertyChange("measuring", oldMeasuring, measuring);
    }

    /**
     *
     * @param selectedObservation
     */
    public void setSelectedObservation(Observation selectedObservation) {
        this.selectedObservation = selectedObservation;
    }
}
