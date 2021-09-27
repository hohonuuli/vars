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

import mbarix4j.swing.JImageUrlCanvas;
import vars.annotation.AnnotationPersistenceService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Brian Schlining
 * @since 2012-08-13
 *
 * @param <T>
 */
public class MeasurementLayerSettingsBuilder<T extends JImageUrlCanvas> implements UISettingsBuilder {

    private final JXObservationsPainter<T> identityReferencePainter;
    private final MultiLayerUI<T> layerUI;
    private final JXObservationsPainter<T> notSelectedObservationsPainter;
    private final JPanel panel;
    private JCheckBox showNotSelectedCheckBox;
    private JCheckBox showPainterCheckBox;
    private JButton showColorChooserButton;

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
        layerUI.addPainter(notSelectedObservationsPainter);
        panel.add(getShowNotSelectedCheckBox());
        panel.add(Box.createHorizontalStrut(20));

        // --- Checkbox to draw observations with same identity reference
        panel.add(getShowPainterCheckBox());

        // --- Button to adjust color of identityReferencePainter
        panel.add(getShowColorChooserButton());

    }

    protected JCheckBox getShowPainterCheckBox() {
        if (showPainterCheckBox == null) {
            showPainterCheckBox = new JCheckBox();
            showPainterCheckBox.setText("Show Related Observations");
            showPainterCheckBox.setSelected(false);
            showPainterCheckBox.addItemListener(new ItemListener() {

                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (showPainterCheckBox.isSelected()) {
                        MeasurementLayerSettingsBuilder.this.layerUI.addPainter(identityReferencePainter);
                    } else {
                        MeasurementLayerSettingsBuilder.this.layerUI.removePainter(identityReferencePainter);
                    }
                }
            });
            showPainterCheckBox.setSelected(false);
            showPainterCheckBox.setToolTipText("Shows observations with the same identity-reference from other videoframes");
        }
        return showPainterCheckBox;
    }

    protected  JCheckBox getShowNotSelectedCheckBox() {
        if (showNotSelectedCheckBox == null) {
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
                    getShowColorChooserButton().setEnabled(showNotSelectedCheckBox.isSelected());
                }

            });
        }
        return showNotSelectedCheckBox;
    }

    protected JButton getShowColorChooserButton() {
        if (showColorChooserButton == null) {
            showColorChooserButton = new JButton("Select Color");
            showColorChooserButton.setToolTipText("Select related observations color");
            showColorChooserButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    IMarkerStyle markerStyle = identityReferencePainter.getMarkerStyle();
                    Color color = JColorChooser.showDialog(panel, "Choose related observations color", markerStyle.getColor());
                    markerStyle = new MarkerStyleBean(color, markerStyle.getArmLength(), markerStyle.getFont(), markerStyle.getStroke());
                    identityReferencePainter.setMarkerStyle(markerStyle);

                }
            });
        }
        return showColorChooserButton;
    }

    /**
     */
    @Override
    public void clearPainters() {
        if (getShowPainterCheckBox().isSelected()) {
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
