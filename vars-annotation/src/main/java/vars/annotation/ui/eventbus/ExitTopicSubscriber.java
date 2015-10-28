package vars.annotation.ui.eventbus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.ui.Lookup;
import vars.avplayer.ImageCaptureService;
import vars.avplayer.VideoControlService;

/**
 * @author Brian Schlining
 * @since 2010-12-23
 */
public class ExitTopicSubscriber extends vars.shared.ui.event.ExitTopicSubscriber {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public void onEvent(String topic, Object data) {

        // Clean up NATIVE resources when we exit
        log.info("Closing ImageCaptureService");
        try {
            ImageCaptureService imageCaptureService = (ImageCaptureService) Lookup.getImageCaptureServiceDispatcher().getValueObject();
            imageCaptureService.dispose();
        }
        catch (Throwable e) {
            log.warn("An error occurred while closing the image capture services", e);
        }

        log.info("Closing VideoControlService");
        try {
            VideoControlService videoControlService = (VideoControlService) Lookup.getVideoControlServiceDispatcher().getValueObject();
            videoControlService.kill();
        }
        catch (Exception e) {
             log.warn("An error occurred while closing the video control services", e);
        }

        super.onEvent(topic, data);
    }


}
