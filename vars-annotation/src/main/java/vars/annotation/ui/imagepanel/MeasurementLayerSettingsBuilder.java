/*
 * @(#)MeasurementLayerSettingsBuilder.java   2012.11.26 at 08:48:29 PST
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

import org.mbari.swing.JImageUrlCanvas;
import vars.annotation.AnnotationPersistenceService;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Brian Schlining
 * @since 2012-08-13
 *
 * @param <T>
 */
public class MeasurementLayerSettingsBuilder<T extends JImageUrlCanvas> implements UISettingsBuilder {

    private final JXPainter<T> identityReferencePainter;
    private final MultiLayerUI<T> layerUI;
    private final JXPainter<T> notSelectedObservationsPainter;
    private final JPanel panel;
    private final JCheckBox showNotSelectedCheckBox;
    private final JCheckBox showPainterCheckBox;

    /**
     * Constructs ...
     *
     * @param layerUI
     * @param annotationPersistenceService
     */
    public MeasurementLayerSettingsBuilder(MultiLayerUI<T> layerUI,
            AnnotationPersistenceService annotationPersistenceService) {

        this.layerUI = layerUI;
        identityReferencePainter = new JXIdentityReferencePainter<T>(annotationPersistenceService);
        notSelectedObservationsPainter = new JXNotSelectedObservationsPainter<T>(MarkerStyle.NOTSELECTED_FAINT);

        // --- Configure Panel
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        // -- Checkbox to draw not selected observations in the same videoframe
        showNotSelectedCheckBox = new JCheckBox();
        showNotSelectedCheckBox.setText("Show Observations in Same Video Frame");
        showNotSelectedCheckBox.setSelected(true);
        showNotSelectedCheckBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (showNotSelectedCheckBox.isSelected()) {
                    MeasurementLayerSettingsBuilder.this.layerUI.addPainter(notSelectedObservationsPainter);
                }
                else {
                    MeasurementLayerSettingsBuilder.this.layerUI.removePainter(notSelectedObservationsPainter);
                }
            }

        });
        layerUI.addPainter(notSelectedObservationsPainter);
        panel.add(showNotSelectedCheckBox);

        // --- Checkbox to draw observations with same identity reference
        showPainterCheckBox = new JCheckBox();
        showPainterCheckBox.setText("Show Related Observations");
        showPainterCheckBox.setSelected(false);
        showPainterCheckBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (showPainterCheckBox.isSelected()) {
                    MeasurementLayerSettingsBuilder.this.layerUI.addPainter(identityReferencePainter);
                }
                else {
                    MeasurementLayerSettingsBuilder.this.layerUI.removePainter(identityReferencePainter);
                }
            }
        });
        showPainterCheckBox.setSelected(false);
        panel.add(showPainterCheckBox);

    }

    /**
     */
    @Override
    public void clearPainters() {
        if (showPainterCheckBox.isSelected()) {
            layerUI.addPainter(identityReferencePainter);
        }
        else {
            layerUI.removePainter(identityReferencePainter);
        }

        if (showNotSelectedCheckBox.isSelected()) {
            layerUI.addPainter(notSelectedObservationsPainter);
        }
        else {
            layerUI.removePainter(notSelectedObservationsPainter);
        }
    }

    /**
     * @return
     */
    @Override
    public JPanel getPanel() {
        return panel;
    }
}
