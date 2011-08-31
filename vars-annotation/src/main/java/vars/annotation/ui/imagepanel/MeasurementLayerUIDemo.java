package vars.annotation.ui.imagepanel;

import com.google.inject.Injector;
import org.jdesktop.jxlayer.JXLayer;
import org.mbari.swing.JImageUrlCanvas;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.net.URL;

/**
 * @author Brian Schlining
 * @since 2011-08-30
 */
public class MeasurementLayerUIDemo {

    public static void main(String[] args) {

        final Injector injector = (Injector) Lookup.getGuiceInjectorDispatcher().getValueObject();
        ToolBelt toolBelt = injector.getInstance(ToolBelt.class);

        // Layout components
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Load image
        URL url = frame.getClass().getResource("/images/BrianSchlining.jpg");
        final JImageUrlCanvas label = new JImageUrlCanvas(url);

        // Create JXLayer
        MeasurementLayerUI<JImageUrlCanvas> layerUI = new MeasurementLayerUI<JImageUrlCanvas>(toolBelt);
        JXLayer<JImageUrlCanvas> layer = new JXLayer<JImageUrlCanvas>(label);
        layer.setUI(layerUI);

//        frame.addComponentListener(new ComponentAdapter() {
//
//            public void componentResized(ComponentEvent e) {
//                label.setSize(frame.getWidth(), frame.getHeight());
//                frame.repaint();
//            }
//
//        });

        frame.add(layer, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
}
