package vars.annotation.ui.commandqueue.impl;

import org.bushe.swing.event.EventBus;
import vars.DAO;
import vars.annotation.Observation;
import vars.annotation.ObservationDAO;
import vars.annotation.VideoFrame;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.eventbus.ObservationsChangedEvent;
import vars.annotation.ui.eventbus.VideoFramesChangedEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author Brian Schlining
 * @since 2011-10-17
 */
public class RemoveImageReferencesCmd implements Command {

    private final Collection<DataBean> originalData = new ArrayList<DataBean>();

    public RemoveImageReferencesCmd(Collection<Observation> observations) {
        for (Observation observation : observations) {
            originalData.add(new DataBean(observation.getVideoFrame().getCameraData().getImageReference(),
                    observation.getPrimaryKey()));
        }
    }

    @Override
    public void apply(ToolBelt toolBelt) {
        Collection<VideoFrame> videoFrames = new HashSet<VideoFrame>(originalData.size());
        ObservationDAO dao = toolBelt.getAnnotationDAOFactory().newObservationDAO();
        dao.startTransaction();
        for (DataBean bean : originalData) {
            Observation newObs = dao.findByPrimaryKey(bean.observationPrimaryKey);
            newObs.getVideoFrame().getCameraData().setImageReference(null);
            videoFrames.add(newObs.getVideoFrame());
        }
        dao.endTransaction();
        dao.close();
        EventBus.publish(new VideoFramesChangedEvent(null, videoFrames));
    }

    @Override
    public void unapply(ToolBelt toolBelt) {
        Collection<VideoFrame> videoFrames = new HashSet<VideoFrame>(originalData.size());
        ObservationDAO dao = toolBelt.getAnnotationDAOFactory().newObservationDAO();
        dao.startTransaction();
        for (DataBean bean : originalData) {
            Observation observation = dao.findByPrimaryKey(bean.observationPrimaryKey);
            if (observation != null) {
                observation.getVideoFrame().getCameraData().setImageReference(bean.imageReference);
                videoFrames.add(observation.getVideoFrame());
            }
        }
        dao.endTransaction();
        dao.close();
        EventBus.publish(new VideoFramesChangedEvent(null, videoFrames));
    }

    @Override
    public String getDescription() {
        return "Remove image references from " + originalData.size() + " observations";
    }

    private class DataBean {
        final String imageReference;
        final Object observationPrimaryKey;

        private DataBean(String imageReference, Object observationPrimaryKey) {
            this.imageReference = imageReference;
            this.observationPrimaryKey = observationPrimaryKey;
        }
    }
}
