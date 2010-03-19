/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui.imagepanel;

import java.awt.BorderLayout;
import java.net.URL;
import javax.swing.JFrame;
import org.jdesktop.jxlayer.JXLayer;
import org.mbari.swing.JImageCanvas2;

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
        final JImageCanvas2 label = new JImageCanvas2(url);

        // Create JXLayer
        PointAdditionLayerUI<JImageCanvas2> layerUI = new PointAdditionLayerUI<JImageCanvas2>();
        JXLayer<JImageCanvas2> layer = new JXLayer<JImageCanvas2>(label);
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
