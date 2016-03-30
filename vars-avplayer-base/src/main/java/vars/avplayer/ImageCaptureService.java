/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.avplayer;


import java.awt.Image;
import java.io.File;
import java.util.Optional;

/**
 * Base interface for services that capture images.
 * @author brian
 */
public interface ImageCaptureService {


    /**
     * The imagecapture service should:
     * 1. Alwasy write to the file, unless it's null. Then write to a temporary file and delete it.
     * 2. Alwasy return the image data
     * @param file
     * @return
     * @throws ImageCaptureException
     */
    Optional<Image> capture(File file) throws ImageCaptureException;



    /**
     * Cleanup resources
     */
    void dispose();

    void showSettingsDialog();
}
