/*
 * @(#)AnnotationLayerSettingsBuilder.java   2012.11.26 at 08:48:38 PST
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

import mbarix4j.swing.JImageUrlCanvas;

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
public class AnnotationLayerSettingsBuilder<T extends JImageUrlCanvas> implements UISettingsBuilder {

    private JXPainter<T> notSelectedObservationsPainter = new JXNotSelectedObservationsPainter<T>();
    private final MultiLayerUI<T> layerUI;
    private JPanel panel;
    private JCheckBox showNotSelectedCheckBox;

    /**
     * Constructs ...
     *
     * @param layerUI
     */
    public AnnotationLayerSettingsBuilder(MultiLayerUI<T> layerUI) {
        this.layerUI = layerUI;

        // --- Configure Panel
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        // --- CheckBox to draw not selected annotations
        showNotSelectedCheckBox = new JCheckBox();
        showNotSelectedCheckBox.setText("Show All Observations");
        showNotSelectedCheckBox.setSelected(true);
        showNotSelectedCheckBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (showNotSelectedCheckBox.isSelected()) {
                    AnnotationLayerSettingsBuilder.this.layerUI.addPainter(notSelectedObservationsPainter);
                }
                else {
                    AnnotationLayerSettingsBuilder.this.layerUI.removePainter(notSelectedObservationsPainter);
                }
            }

        });
        showNotSelectedCheckBox.setSelected(true);
        panel.add(showNotSelectedCheckBox);
    }

    /**
     */
    @Override
    public void clearPainters() {
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
