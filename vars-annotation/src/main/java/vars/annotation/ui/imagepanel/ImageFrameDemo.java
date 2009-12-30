/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui.imagepanel;

import java.net.URL;
import javax.swing.JFrame;
import org.mbari.swing.SimpleImageFrame;

/**
 *
 * @author brian
 */
public class ImageFrameDemo {

    public static void main(String[] args) {
        ImageFrame frame = new ImageFrame();
        //SimpleImageFrame frame = new SimpleImageFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        URL url = frame.getClass().getResource("/images/BrianSchlining.jpg");
        frame.setImageUrl(url);
        frame.setVisible(true);
    }

}
