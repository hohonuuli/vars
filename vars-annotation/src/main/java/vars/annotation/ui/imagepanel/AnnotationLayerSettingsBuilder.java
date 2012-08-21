package vars.annotation.ui.imagepanel;

import org.mbari.swing.JImageUrlCanvas;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Brian Schlining
 * @since 2012-08-13
 */
public class AnnotationLayerSettingsBuilder<T extends JImageUrlCanvas> implements UISettingsBuilder {

    private JPanel panel;
    private final MultiLayerUI<T> layerUI;
    private JXPainter<T> notSelectedObservationsPainter = new JXNotSelectedObservationsPainter<T>();
    private JCheckBox showNotSelectedCheckBox;

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
                } else {
                    AnnotationLayerSettingsBuilder.this.layerUI.removePainter(notSelectedObservationsPainter);
                }
            }
        });
        showNotSelectedCheckBox.setSelected(true);
        panel.add(showNotSelectedCheckBox);
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public void clearPainters() {
        if (showNotSelectedCheckBox.isSelected()) {
            layerUI.addPainter(notSelectedObservationsPainter);
        }
        else {
            layerUI.removePainter(notSelectedObservationsPainter);
        }
    }

}
