/*
 * @(#)AreaMeasurementLayerSettingsBuilder.java   2013.02.04 at 04:42:57 PST
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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.mbari.swing.JImageUrlCanvas;

/**
 * Builds a JPanel for allowing user adjustment of the settings for an AreaMeasurementLayerUI
 *
 * @author Brian Schlining
 * @since 2012-08-13
 *
 * @param <T>
 */
public class AreaMeasurementLayerSettingsBuilder<T extends JImageUrlCanvas> implements UISettingsBuilder {

    private JXPainter<T> notSelectedAreaMeasurementsPainter = new JXNotSelectedAreaMeasurementPainter<T>();
    private final MultiLayerUI<T> layerUI;
    private final JPanel panel;
    private JCheckBox showNotSelectedCheckBox;

    /**
     * Constructs ...
     *
     * @param layerUI
     */
    public AreaMeasurementLayerSettingsBuilder(MultiLayerUI<T> layerUI) {
        this.layerUI = layerUI;

        // --- Configure Panel
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        // --- CheckBox to draw not selected annotations
        showNotSelectedCheckBox = new JCheckBox();
        showNotSelectedCheckBox.setText("Show All Area Measurements");
        showNotSelectedCheckBox.setSelected(false);
        showNotSelectedCheckBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (showNotSelectedCheckBox.isSelected()) {
                    AreaMeasurementLayerSettingsBuilder.this.layerUI.addPainter(notSelectedAreaMeasurementsPainter);
                }
                else {
                    AreaMeasurementLayerSettingsBuilder.this.layerUI.removePainter(notSelectedAreaMeasurementsPainter);
                }
            }

        });
        showNotSelectedCheckBox.setSelected(false);
        panel.add(showNotSelectedCheckBox);

    }

    /**
     */
    @Override
    public void clearPainters() {
        if (showNotSelectedCheckBox.isSelected()) {
            layerUI.addPainter(notSelectedAreaMeasurementsPainter);
        }
        else {
            layerUI.removePainter(notSelectedAreaMeasurementsPainter);
        }
    }

    /**
     * @return
     */
    public JPanel getPanel() {
        return panel;
    }
}
