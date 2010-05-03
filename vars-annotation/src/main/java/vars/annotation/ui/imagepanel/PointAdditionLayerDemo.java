/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui.imagepanel;

import java.awt.BorderLayout;
import java.net.URL;
import javax.swing.JFrame;
import org.jdesktop.jxlayer.JXLayer;
import org.mbari.swing.JImageUrlCanvas;

/**
 *
 * @author brian
 */
public class PointAdditionLayerDemo {

    public static void main(String[] args) {

        // Layout components
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Load image
        URL url = frame.getClass().getResource("/images/BrianSchlining.jpg");
        final JImageUrlCanvas label = new JImageUrlCanvas(url);

        // Create JXLayer
        PointAdditionLayerUI<JImageUrlCanvas> layerUI = new PointAdditionLayerUI<JImageUrlCanvas>();
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
