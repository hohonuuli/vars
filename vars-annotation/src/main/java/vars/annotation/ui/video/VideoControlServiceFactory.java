package vars.annotation.ui.video;

import org.bushe.swing.event.EventBus;
import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.SimpleVideoIO;
import org.mbari.vcr4j.VideoState;
import org.mbari.vcr4j.decorators.VCRSyncDecorator;
import org.mbari.vcr4j.rs422.RS422Error;
import org.mbari.vcr4j.rs422.RS422State;
import org.mbari.vcr4j.rs422.VCRVideoIO;
import org.mbari.vcr4j.rs422.decorators.RS422StatusDecorator;
import org.mbari.vcr4j.rs422.decorators.UserbitsAsTimeDecorator;
import org.mbari.vcr4j.rxtx.RXTXVideoIO;
import org.mbari.vcr4j.udp.UDPVideoIO;
import vars.annotation.ui.StateLookup;
import vars.avfoundation.AVFImageCaptureServiceImpl;
import vars.avplayer.VideoController;
import vars.avplayer.noop.NoopImageCaptureService;

/**
 * Utility class for creating a {@link VideoController} from
 * a String videoConnectionID (as stored in preferences)
 *
 * @author Brian Schlining
 * @since Sep 17, 2010
 */
public class VideoControlServiceFactory {


    // TODO this should be moved to the avplayer modules
    public static VideoController<? extends VideoState, ? extends VideoError> newVideoController(String videoConnectionID) {
        VideoController<? extends VideoState, ? extends VideoError> videoController = null;
        try {
            if (videoConnectionID.contains(":")) { // UDP

                String[] parts = videoConnectionID.split(":");
                String host = parts[0];
                Integer port = Integer.valueOf(parts[1]);

                UDPVideoIO io = new UDPVideoIO(host, port);
                new VCRSyncDecorator<>(io);
                return new VideoController<>(new NoopImageCaptureService(), io);
            }
            else {
                VCRVideoIO io = RXTXVideoIO.open(videoConnectionID);
                new VCRSyncDecorator<>(io);
                new RS422StatusDecorator(io);
                UserbitsAsTimeDecorator decorator = new UserbitsAsTimeDecorator(io);
                VideoIO<RS422State, RS422Error> io2 = new SimpleVideoIO<>(io.getConnectionID(),
                        io.getCommandSubject(),
                        io.getStateObservable(),
                        io.getErrorObservable(),
                        decorator.getIndexObservable());
                return new VideoController<>(new AVFImageCaptureServiceImpl(), io2);
            }
        }
        catch (Exception e) {
            EventBus.publish(StateLookup.TOPIC_NONFATAL_ERROR, e);
            return null;
        }
    }
}
