package vars.annotation.ui.imagepanel;

import org.jdesktop.jxlayer.JXLayer;
import org.mbari.swing.JImageCanvas;
import org.mbari.swing.JImageUrlCanvas;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Arrays;

/**
 * @author Brian Schlining
 * @since 2014-11-19T12:22:00
 */
public class JXHorizontalLinePainterDemo {

    public static void main(String[] args) {
        // Layout components
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Load image
        URL url = frame.getClass().getResource("/images/BrianSchlining.jpg");
        final JImageUrlCanvas label = new JImageUrlCanvas(url);

        MultiLayerUI<JComponent> layerUI = new MultiLayerUI<JComponent>();
        JXLayer<JComponent> layer = new JXLayer<JComponent>(label);
        layer.setUI(layerUI);

        JXPainter<JComponent> painter = new JXHorizontalLinePainter<JComponent>(label) { {
            setDistances(Arrays.asList(0.20, 0.50, 0.80));
        }};
        layerUI.addPainter(painter);

        painter = new JXCrossHairPainter<JComponent>();
        layerUI.addPainter(painter);



        frame.add(layer, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
}
