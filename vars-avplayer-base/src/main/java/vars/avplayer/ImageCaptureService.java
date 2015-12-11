/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.avplayer;

import java.awt.Image;
import java.io.File;

/**
 *
 * @author brian
 */
public interface ImageCaptureService {

    /**
     * Some capture code returns an Image which in turn must be saved to disk as a PNG. Other code
     * writes the PNG directly to disk first, then it must be read back to get the Java Image
     * object
     * @return true if a PNG is written directly to disk first.
     */
    boolean isPngAutosaved();

    Image capture(File file) throws ImageCaptureException;

    /**
     *
     * @return THe image captured from the QuickTime source
     * @throws ImageCaptureException
     */
    Image capture(String timecode) throws ImageCaptureException;

    /**
     * Cleanup resources
     */
    void dispose();

    void showSettingsDialog();
}
