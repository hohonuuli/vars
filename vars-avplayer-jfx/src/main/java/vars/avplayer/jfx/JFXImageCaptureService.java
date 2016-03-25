package vars.avplayer.jfx;

import vars.avplayer.EventBus;
import vars.avplayer.ImageCaptureException;
import vars.avplayer.ImageCaptureService;
import vars.shared.rx.messages.NonFatalExceptionMsg;

import java.awt.*;
import java.io.File;
import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2016-03-25T13:28:00
 */
public class JFXImageCaptureService implements ImageCaptureService {

    private final JFXMovieJFrameController controller;


    public JFXImageCaptureService(JFXMovieJFrameController controller) {
        this.controller = controller;
    }

    @Override
    public Optional<Image> capture(File file) throws ImageCaptureException {
        Image image = null;
        if (controller != null) {
            try {
                image = controller.frameCapture(file);
            } catch (Exception e) {
                EventBus.send(new NonFatalExceptionMsg("Failed to capture image to " + file.getAbsolutePath(), e));
            }
        }
        return Optional.ofNullable(image);
    }


    @Override
    public void dispose() {
        // Do nothing
    }

    @Override
    public void showSettingsDialog() {
        // Do nothing
    }
}
