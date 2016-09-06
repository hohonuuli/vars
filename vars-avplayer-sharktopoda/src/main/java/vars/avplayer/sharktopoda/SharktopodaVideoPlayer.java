package vars.avplayer.sharktopoda;

import org.mbari.util.Tuple2;
import org.mbari.vcr4j.sharktopoda.SharktopodaError;
import org.mbari.vcr4j.sharktopoda.SharktopodaState;
import vars.ToolBelt;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;
import vars.avplayer.VideoController;
import vars.avplayer.VideoPlayer;
import vars.avplayer.VideoPlayerDialogUI;
import vars.shared.rx.RXEventBus;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Created by brian on 9/1/16.
 */
public class SharktopodaVideoPlayer implements VideoPlayer<SharktopodaState, SharktopodaError> {


    @Override
    public CompletableFuture<Tuple2<VideoArchive, VideoController<SharktopodaState, SharktopodaError>>> openVideoArchive(ToolBelt toolBelt, Object... args) {
        return null;
    }

    public CompletableFuture<Tuple2<VideoArchive, VideoController<SharktopodaState, SharktopodaError>>> openVideoArchive(ToolBelt toolBelt, String movieLocation,
                                                                                                                         String platformName,
                                                                                                                         Integer sequenceNumber) {

        CompletableFuture.supplyAsync(() -> {

            // TODO
            return null;
        });


    }

    @Override
    public VideoPlayerDialogUI<SharktopodaState, SharktopodaError> getConnectionDialog(ToolBelt toolBelt, RXEventBus eventBus) {
        return null;
    }

    @Override
    public boolean canPlay(String mimeType) {
        return false;
    }

    @Override
    public String getName() {
        return null;
    }

    public Optional<VideoArchive> findByLocation(String location, AnnotationDAOFactory daoFactory) {
        VideoArchiveDAO dao = daoFactory.newVideoArchiveDAO();
        dao.startTransaction();
        VideoArchive videoArchive = dao.findByName(location);
        dao.endTransaction();
        return Optional.ofNullable(videoArchive);
    }

    private VideoArchive createVideoArchive(VideoParams videoParams, AnnotationDAOFactory daoFactory) {
        VideoArchive videoArchive = null;
        if (videoParams.getPlatformName().isPresent() && videoParams.getSequenceNumber().isPresent()) {
            String location = videoParams.getMovieLocation();
            int sequenceNumber = videoParams.getSequenceNumber().get();
            String platform = videoParams.getPlatformName().get();
            VideoArchiveDAO dao = daoFactory.newVideoArchiveDAO();
            dao.startTransaction();
            videoArchive = dao.findOrCreateByParameters(platform, sequenceNumber, location);
            dao.endTransaction();
        }
        return videoArchive;
    }

    protected VideoArchive getOrCreateVideoArchive(VideoParams videoParams, AnnotationDAOFactory daoFactory) {
        return findByLocation(videoParams.getMovieLocation(), daoFactory)
                .orElseGet(() -> createVideoArchive(videoParams, daoFactory));
    }
}
