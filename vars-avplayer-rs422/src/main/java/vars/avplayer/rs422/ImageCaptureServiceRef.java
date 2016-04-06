package vars.avplayer.rs422;

import vars.avfoundation.AVFImageCaptureServiceImpl;

/**
 * @author Brian Schlining
 * @since 2016-04-06T10:17:00
 */
public class ImageCaptureServiceRef {

    private static AVFImageCaptureServiceImpl imageCaptureService;


    public static AVFImageCaptureServiceImpl getImageCaptureService() {
        if (imageCaptureService == null) {
            imageCaptureService = new AVFImageCaptureServiceImpl();
        }
        return imageCaptureService;
    }
}
