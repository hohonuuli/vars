package vars.annotation.ui;

import java.awt.Image;
import java.util.Date;

import org.mbari.vcr.IVCR;

public interface VideoService {
    
    IVCR getVCR();
    
    String lookupCurrentTimecode();

    Date lookupCurrentRecordedDate();

    void openVideo(String videoArchiveName);

    /**
     * Grab an image at current position in the video
     *
     * @return The image at the current position in the vidoe
     */
    Image grabImage();

}
