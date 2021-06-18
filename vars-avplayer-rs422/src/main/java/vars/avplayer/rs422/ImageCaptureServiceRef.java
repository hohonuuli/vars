package vars.avplayer.rs422;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// import vars.avfoundation.AVFImageCaptureServiceImpl;
import vars.avplayer.ImageCaptureService;
import vars.avplayer.noop.NoopImageCaptureService;

/**
 * @author Brian Schlining
 * @since 2016-04-06T10:17:00
 */
public class ImageCaptureServiceRef {

    private static ImageCaptureService imageCaptureService;
    private static final Logger log = LoggerFactory.getLogger(ImageCaptureServiceRef.class);


    public static ImageCaptureService getImageCaptureService() {
        if (imageCaptureService == null) {
            try {
                // imageCaptureService = new AVFImageCaptureServiceImpl();
            }
            catch (UnsatisfiedLinkError e) {
                log.warn("Failed to instantiate AVFoundation Image Capture.");
                imageCaptureService = new NoopImageCaptureService();
            }
        }
        return imageCaptureService;
    }
}
