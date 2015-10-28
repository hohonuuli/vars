package vars.annotation.ui.video;

import vars.avplayer.VideoControlService;

/**
 * Utility class for creating a {@link VideoControlService} from
 * a String videoConnectionID (as stored in preferences)
 *
 * @author Brian Schlining
 * @since Sep 17, 2010
 */
public class VideoControlServiceFactory {

    public static VideoControlService newVideoControlService(String videoConnectionId) {
        VideoControlService videoControlService = null;
        try {
            Double frameRate = 29.97;
            if (videoConnectionId.contains(":")) {
                videoControlService = new UDPVideoControlService();
                String[] parts = videoConnectionId.split(":");
                String host = parts[0];
                Integer port = Integer.valueOf(parts[1]);
                videoControlService.connect(host, port, frameRate);
            }
            else {
                videoControlService = new RS422VideoControlService();
                videoControlService.connect(videoConnectionId, frameRate);
            }
        }
        catch (Exception e) {
            videoControlService = new DoNothingVideoControlService();
        }
        return videoControlService;
    }
}
