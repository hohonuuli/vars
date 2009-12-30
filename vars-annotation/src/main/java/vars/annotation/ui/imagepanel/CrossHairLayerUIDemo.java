/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui.imagepanel;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jdesktop.jxlayer.JXLayer;

/**
 *
 * @author brian
 */
public class CrossHairLayerUIDemo {

    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("DEMO");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new BorderLayout());

        URL url = frame.getClass().getResource("/images/BrianSchlining.jpg");
        BufferedImage image = ImageIO.read(url);
        JLabel label = new JLabel(new ImageIcon(image));

        CrossHairLayerUI<JComponent> layerUI = new CrossHairLayerUI<JComponent>();
        JXLayer<JComponent> layer = new JXLayer<JComponent>(label);
        layer.setUI(layerUI);

        panel.add(layer, BorderLayout.CENTER);
        frame.add(panel, BorderLayout.CENTER);

        frame.setSize(image.getWidth(), image.getHeight());
        frame.setLocationByPlatform(true);
        frame.setVisible(true);


    }

}
