/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui.video;

import java.awt.image.BufferedImage;

/**
 *
 * @author brian
 */
public interface ImageCaptureService {

    BufferedImage capture(String timecode);

}
