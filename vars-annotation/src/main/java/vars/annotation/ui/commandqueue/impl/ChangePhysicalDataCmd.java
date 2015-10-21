package vars.annotation.ui.commandqueue.impl;

import com.google.common.base.Preconditions;
import org.bushe.swing.event.EventBus;
import vars.annotation.ImmutablePhysicalData;
import vars.annotation.PhysicalData;
import vars.annotation.PhysicalDataDAO;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.eventbus.ObservationsChangedEvent;
import vars.annotation.ui.eventbus.VideoFramesChangedEvent;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author Brian Schlining
 * @since 2015-10-19T10:38:00
 */
public class ChangePhysicalDataCmd implements Command {

    private final PhysicalData oldPhysicalData;
    private final PhysicalData newPhysicalData;

    public ChangePhysicalDataCmd(PhysicalData oldPhysicalData, PhysicalData newPhysicalData) {
        Preconditions.checkNotNull(oldPhysicalData);
        Preconditions.checkNotNull(newPhysicalData);
        this.oldPhysicalData = new ImmutablePhysicalData(oldPhysicalData);
        this.newPhysicalData = new ImmutablePhysicalData(newPhysicalData);
    }

    @Override
    public void apply(ToolBelt toolBelt) {
        doCommand(toolBelt, true);
    }

    @Override
    public void unapply(ToolBelt toolBelt) {
        doCommand(toolBelt, false);
    }

    private void doCommand(ToolBelt toolBelt, boolean isApply) {
        PhysicalData data = isApply ? newPhysicalData : oldPhysicalData;
        PhysicalDataDAO dao = toolBelt.getAnnotationDAOFactory().newPhysicalDataDAO();
        PhysicalData pd = dao.findByPrimaryKey(oldPhysicalData.getPrimaryKey());
        VideoFramesChangedEvent event = null;
        if (pd != null) {
            pd.setAltitude(data.getAltitude());
            pd.setDepth(data.getDepth());
            pd.setLatitude(data.getLatitude());
            pd.setLight(data.getLight());
            pd.setLongitude(data.getLongitude());
            pd.setLogDate(data.getLogDate());
            pd.setOxygen(data.getOxygen());
            pd.setSalinity(data.getSalinity());
            pd.setTemperature(data.getTemperature());
            event = new VideoFramesChangedEvent(null, Collections.singletonList(pd.getVideoFrame()));
        }
        dao.endTransaction();
        dao.close();
        if (event != null) {
            EventBus.publish(event);
        }
    }

    @Override
    public String getDescription() {
        return null;
    }
}
