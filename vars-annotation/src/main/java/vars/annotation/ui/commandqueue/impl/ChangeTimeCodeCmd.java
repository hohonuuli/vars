package vars.annotation.ui.commandqueue.impl;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.bushe.swing.event.EventBus;
import vars.DAO;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.annotation.VideoFrame;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.eventbus.ObservationsSelectedEvent;
import vars.annotation.ui.eventbus.ObservationsChangedEvent;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Brian Schlining
 * @since 2011-09-25
 */
public class ChangeTimeCodeCmd implements Command {

    private final Collection<DataBean> originalData;
    private final String timecode;

    public ChangeTimeCodeCmd(Collection<Observation> originalObservations, String timecode) {
        this.originalData = new ArrayList<DataBean>(Collections2.transform(originalObservations, new ObservationToDataBean()));
        this.timecode = timecode;
    }

    @Override
    public void apply(ToolBelt toolBelt) {
        doCommand(toolBelt, true);

    }

    private void doCommand(ToolBelt toolBelt, boolean isApply) {
        Collection<Observation> newObservations = new ArrayList<Observation>(originalData.size());
        DAO dao = toolBelt.getAnnotationDAOFactory().newDAO();
        dao.startTransaction();
        for (DataBean dataBean : originalData) {
            Observation observation = dao.find(dataBean.observation);
            VideoFrame sourceVideoFrame = observation.getVideoFrame();
            VideoArchive videoArchive = sourceVideoFrame.getVideoArchive();
            // Select timecode source 
            String tc = isApply ? timecode : dataBean.timecode;
            VideoFrame targetVideoFrame = videoArchive.findVideoFrameByTimeCode(tc);

            if (targetVideoFrame == null) {
                sourceVideoFrame.setTimecode(tc);
            }
            else {
                // Move observations to target
                sourceVideoFrame.removeObservation(observation);
                targetVideoFrame.addObservation(observation);
            }
            newObservations.add(observation);
        }
        dao.endTransaction();
        dao.close();
        ObservationsChangedEvent updateEvent = new ObservationsChangedEvent(null, newObservations);
        EventBus.publish(updateEvent);
    }

    @Override
    public void unapply(ToolBelt toolBelt) {
        doCommand(toolBelt, false);
    }

    @Override
    public String getDescription() {
        return "Move " + originalData.size() + " Observations to timecode = " + timecode;
    }

    /**
     * Bean for storing original data
     */
    private class DataBean {
        private final String timecode;
        private final Observation observation;

        private DataBean(String timecode, Observation observation) {
            this.timecode = timecode;
            this.observation = observation;
        }
    }

    private class ObservationToDataBean implements Function<Observation, DataBean> {
        @Override
        public DataBean apply(Observation input) {
            return new DataBean(input.getVideoFrame().getTimecode(), input);
        }
    }
}
