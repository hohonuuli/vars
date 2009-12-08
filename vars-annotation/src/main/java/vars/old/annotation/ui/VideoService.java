package vars.old.annotation.ui;

import java.awt.Image;
import java.io.File;
import java.util.Date;

import org.mbari.vcr.IVCR;

public interface VideoService {
    
    IVCR getVCR();
    
    void setVCR(IVCR vcr);
    
    String lookupCurrentTimecode();

    Date lookupCurrentRecordedDate();

    void openVideo(String videoArchiveName);

    /**
     * Grab an image at current position in the video
     *
     * @return The image at the current position in the vidoe
     */
    Image grabImage();
    
    /**
     * Grab an image at the current position in the video and save it to the specified file
     * @param file
     */
    void grabImageAndSaveTo(File file);

}
