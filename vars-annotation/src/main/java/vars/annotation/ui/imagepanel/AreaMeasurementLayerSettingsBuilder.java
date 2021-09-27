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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.*;

import mbarix4j.swing.JImageUrlCanvas;

/**
 * Builds a JPanel for allowing user adjustment of the settings for an AreaMeasurementLayerUI
 *
 * @author Brian Schlining
 * @since 2012-08-13
 *
 * @param <T>
 */
public class AreaMeasurementLayerSettingsBuilder<T extends JImageUrlCanvas> implements UISettingsBuilder {

    private JXAreaMeasurementPainter<T> notSelectedAreaMeasurementsPainter = new JXNotSelectedAreaMeasurementPainter<T>();
    private final MultiLayerUI<T> layerUI;
    private final JPanel panel;
    private JCheckBox showNotSelectedCheckBox;
    private JButton showColorChooserButton;
    private JButton showHLineDialogButton;
    private JXHorizontalLinePainterDialog horizontalLinePainterDialog;

    /**
     * Constructs ...
     *
     * @param layerUI
     */
    public AreaMeasurementLayerSettingsBuilder(AreaMeasurementLayerUI2<T> layerUI) {
        this.layerUI = layerUI;
        horizontalLinePainterDialog = new JXHorizontalLinePainterDialog(layerUI.getHorizontalLinePainter());
        horizontalLinePainterDialog.pack();

        // --- Configure Panel
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        // --- CheckBox to draw not selected annotations
        panel.add(getShowNotSelectedCheckBox());
        panel.add(getShowColorChooserButton());
        panel.add(getShowHLineDialogButton());

    }

    protected JCheckBox getShowNotSelectedCheckBox() {
        if (showNotSelectedCheckBox == null) {
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
        }
        return showNotSelectedCheckBox;
    }

    protected JButton getShowColorChooserButton() {
        if (showColorChooserButton == null) {
            showColorChooserButton = new JButton("Select Color");
            showColorChooserButton.setToolTipText("Select background area measurements color");
            showColorChooserButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Color color = JColorChooser.showDialog(panel, "Select background area measurements color",
                            notSelectedAreaMeasurementsPainter.getPaint());
                    notSelectedAreaMeasurementsPainter.setPaint(color);

                }
            });
        }
        return showColorChooserButton;
    }

    protected JButton getShowHLineDialogButton() {
        if (showHLineDialogButton == null) {
            showHLineDialogButton = new JButton("Add Lines");
            showHLineDialogButton.setToolTipText("Add horizonal lines");
            showHLineDialogButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    horizontalLinePainterDialog.setVisible(true);
                }
            });
        }
        return showHLineDialogButton;
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
