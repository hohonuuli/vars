package vars.annotation.ui.commandqueue.impl;

import org.bushe.swing.event.EventBus;
import vars.DAO;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.AnnotationFactory;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;
import vars.annotation.VideoFrame;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.eventbus.VideoArchiveChangedEvent;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Brian Schlining
 * @since 2011-10-10
 */
public class RemoveObservationsCmd implements Command {

    private final Collection<DataBean> deletedData = new ArrayList<DataBean>();

    public RemoveObservationsCmd(Collection<Observation> originalObservations) {
        for (Observation observation : originalObservations) {
            deletedData.add(new DataBean(observation));
        }
    }

    @Override
    public void apply(ToolBelt toolBelt) {
        final DAO dao = toolBelt.getAnnotationDAOFactory().newDAO();
        dao.startTransaction();

        for (DataBean bean : deletedData) {
            Observation observation = dao.find(bean.observation);
            if (observation != null) {
                VideoFrame videoFrame = observation.getVideoFrame();
                videoFrame.removeObservation(observation);
                dao.remove(observation);
                if (videoFrame.getObservations().size() == 0) {
                    VideoArchive videoArchive = videoFrame.getVideoArchive();
                    videoArchive.removeVideoFrame(videoFrame);
                    dao.remove(videoFrame);
                }
            }
        }

        dao.endTransaction();
        dao.close();
        EventBus.publish(new VideoArchiveChangedEvent(null,
                (VideoArchive) Lookup.getVideoArchiveDispatcher().getValueObject()));
    }

    @Override
    public void unapply(ToolBelt toolBelt) {
        final AnnotationDAOFactory daoFactory = toolBelt.getAnnotationDAOFactory();
        final AnnotationFactory factory = toolBelt.getAnnotationFactory();
        final DAO dao = daoFactory.newDAO();
        final VideoArchiveDAO videoArchiveDAO = daoFactory.newVideoArchiveDAO(dao.getEntityManager());

        dao.startTransaction();

        for (DataBean bean : deletedData) {
            VideoArchive videoArchive = videoArchiveDAO.findByName(bean.videoArchiveName);
            if (videoArchive != null) {
                VideoFrame videoFrame = videoArchive.findVideoFrameByTimeCode(bean.videoFrameTimecode);
                if (videoFrame == null) {
                    videoFrame = factory.newVideoFrame();
                    videoFrame.setTimecode(bean.videoFrameTimecode);
                    videoArchive.addVideoFrame(videoFrame);
                }
                videoFrame.addObservation(bean.observation);
            }
        }

        dao.endTransaction();
        dao.close();
        EventBus.publish(new VideoArchiveChangedEvent(null,
                (VideoArchive) Lookup.getVideoArchiveDispatcher().getValueObject()));
    }

    @Override
    public String getDescription() {
        return "Delete " + deletedData.size() + " observations";
    }


    private class DataBean {
        private final String videoArchiveName;
        private final String videoFrameTimecode;
        private final Observation observation;

        private DataBean(Observation observation) {
            this.observation = observation;
            VideoFrame videoFrame = observation.getVideoFrame();
            VideoArchive videoArchive = videoFrame.getVideoArchive();
            videoArchiveName = videoArchive.getName();
            videoFrameTimecode = videoFrame.getTimecode();
        }
    }


}
