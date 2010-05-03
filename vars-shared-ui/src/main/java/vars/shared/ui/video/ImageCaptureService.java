/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.shared.ui.video;

import java.awt.Image;

/**
 *
 * @author brian
 */
public interface ImageCaptureService {

    /**
     *
     * @return THe image captured from the QuickTime source
     * @throws org.mbari.framegrab.GrabberException
     */
    Image capture(String timecode) throws ImageCaptureException;

    /**
     * Cleanup resources
     */
    void dispose();
}
