package vars.avplayer;

import vars.annotation.AnnotationDAOFactory;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;

import java.util.Optional;

/**
 * Created by brian on 1/22/14.
 */
public abstract class AbstractAccessUI implements VideoPlayerAccessUI {
    private final AnnotationDAOFactory daoFactory;

    protected AbstractAccessUI(AnnotationDAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    public Optional<VideoArchive> findByLocation(String location) {
        VideoArchiveDAO dao = daoFactory.newVideoArchiveDAO();
        dao.startTransaction();
        VideoArchive videoArchive = dao.findByName(location);
        dao.endTransaction();
        return Optional.ofNullable(videoArchive);
    }

    public VideoArchive createVideoArchive(VideoParams videoParams) {
        VideoArchive videoArchive = null;
        if (videoParams.getPlatformNameOpt().isPresent() && videoParams.getSequenceNumberOpt().isPresent()) {
            String location = videoParams.getMovieLocation();
            int sequenceNumber = videoParams.getSequenceNumberOpt().get();
            String platform = videoParams.getPlatformNameOpt().get();
            VideoArchiveDAO dao = daoFactory.newVideoArchiveDAO();
            dao.startTransaction();
            videoArchive = dao.findOrCreateByParameters(platform, sequenceNumber, location);
            dao.endTransaction();
        }
        return videoArchive;
    }

    protected AnnotationDAOFactory getAnnotationDAOFactory() {
        return daoFactory;
    }
}
