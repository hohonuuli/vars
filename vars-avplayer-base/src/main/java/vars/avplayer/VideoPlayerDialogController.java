package vars.avplayer;

import org.mbari.util.Tuple2;
import vars.annotation.VideoArchive;

import java.util.Optional;

/**
 * Created by brian on 1/13/14.
 * @deprecated use VideoPlayerOld instead
 */
public interface VideoPlayerDialogController {

    Tuple2<VideoArchive, VideoPlayerController> openVideoArchive();

    Optional<VideoArchive> findByLocation(String movieLocation);
}
