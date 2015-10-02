package vars.avplayer;



import javax.swing.*;

/**
 * Created by brian on 1/7/14.
 */
public class EmptyVideoPlayerController implements VideoPlayerController {

    ImageCaptureService imageCaptureService = new FakeImageCaptureServiceImpl();
    VideoControlService videoControlService = new AbstractVideoControlService() {
        @Override
        public void connect(Object... args) {
            // Do nothing
        }

        @Override
        public JDialog getConnectionDialog() {
            return null;
        }

        @Override
        public void seek(String timecode) {

        }

        @Override
        public VideoTime requestVideoTime() {
            return null;
        }
    };

    @Override
    public VideoControlService getVideoControlService() {
        return videoControlService;
    }

    @Override
    public ImageCaptureService getImageCaptureService() {
        return imageCaptureService;
    }

    @Override
    public String getMovieLocation() {
        return null;
    }

    @Override
    public void close() {

    }
}
