package vars.annotation.ui.imagepanel;

import org.jdesktop.jxlayer.JXLayer;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * @author Brian Schlining
 * @since 2012-08-02
 */
public class MultiLayerUIDemo {

    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("DEMO");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new BorderLayout());

        URL url = frame.getClass().getResource("/images/BrianSchlining.jpg");
        BufferedImage image = ImageIO.read(url);
        JLabel label = new JLabel(new ImageIcon(image));

        MultiLayerUI<JComponent> layerUI = new MultiLayerUI<JComponent>();
        JXPainter painter = new JXCrossHairPainter();
        layerUI.addPainter(painter);
        layerUI.addPainter(new JXTargetPainter<JComponent>());

        JXGridPainter<JComponent> gridPainter = new JXGridPainter<JComponent>();
        gridPainter.setPixelHeight(image.getHeight());
        gridPainter.setPixelWidth(image.getWidth());
        gridPainter.setPixelDistance(20);
        gridPainter.setAngle(45D * Math.PI / 180D);
        layerUI.addPainter(gridPainter);


        JXLayer<JComponent> layer = new JXLayer<JComponent>(label);
        layer.setUI(layerUI);

        panel.add(layer, BorderLayout.CENTER);
        frame.add(panel, BorderLayout.CENTER);

        frame.setSize(image.getWidth(), image.getHeight());
        frame.setLocationByPlatform(true);
        frame.setVisible(true);


    }
}
