package vars.annotation.ui.eventbus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.ui.StateLookup;
import vars.avplayer.VideoController;

/**
 * @author Brian Schlining
 * @since 2010-12-23
 */
public class ExitTopicSubscriber extends vars.shared.ui.event.ExitTopicSubscriber {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public void onEvent(String topic, Object data) {

        // Clean up NATIVE resources when we exit
        log.info("Closing VideoController and ImageCaptureService");
        try {
            VideoController videoController = StateLookup.getVideoController();
            if (videoController != null) {
                videoController.close();
            }
        }
        catch (Throwable e) {
            log.warn("An error occurred while closing the VideoController", e);
        }

        super.onEvent(topic, data);
    }


}
