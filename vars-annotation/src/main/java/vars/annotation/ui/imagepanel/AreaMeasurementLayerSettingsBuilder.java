package vars.annotation.ui.imagepanel;

import org.mbari.swing.JImageUrlCanvas;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Brian Schlining
 * @since 2012-08-13
 */
public class AreaMeasurementLayerSettingsBuilder<T extends JImageUrlCanvas> implements UISettingsBuilder {

    private final JPanel panel;
    private final MultiLayerUI<T> layerUI;

    public AreaMeasurementLayerSettingsBuilder(MultiLayerUI<T> layerUI) {
        this.layerUI = layerUI;

        // --- Configure Panel
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(new JLabel("No Settings Available"));
    }

    public JPanel getPanel() {
        return panel;
    }

    @Override
    public void clearPainters() {
        // Nothing to do
    }
}
