package vars.annotation.ui.commandqueue.impl;

import com.google.common.base.Preconditions;
import org.bushe.swing.event.EventBus;
import vars.annotation.CameraData;
import vars.annotation.CameraDataDAO;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.eventbus.VideoFramesChangedEvent;

import java.util.Collections;

/**
 * @author Brian Schlining
 * @since 2015-10-21T17:02:00
 */
public class ChangeCameraDataCmd implements Command {

    private final CameraData oldCameraData;
    private final CameraData newCameraData;

    public ChangeCameraDataCmd(CameraData oldCameraData, CameraData newCameraData) {
        Preconditions.checkNotNull(newCameraData);
        Preconditions.checkNotNull(oldCameraData);
        this.newCameraData = newCameraData;
        this.oldCameraData = oldCameraData;
    }

    @Override
    public void apply(ToolBelt toolBelt) {
        doCommand(toolBelt, true);
    }

    @Override
    public void unapply(ToolBelt toolBelt) {
        doCommand(toolBelt, false);
    }

    @Override
    public String getDescription() {
        return null;
    }

    private void doCommand(ToolBelt toolBelt, boolean isApply) {
        CameraData data = isApply ? newCameraData : oldCameraData;
        CameraDataDAO dao = toolBelt.getAnnotationDAOFactory().newCameraDataDAO();
        CameraData cd = dao.findByPrimaryKey(oldCameraData.getPrimaryKey());
        VideoFramesChangedEvent event = null;
        if (cd != null) {
            cd.setDirection(data.getDirection());
            cd.setFieldWidth(data.getFieldWidth());
            cd.setFocus(data.getFocus());
            cd.setHeading(data.getHeading());
            cd.setImageReference(data.getImageReference());
            cd.setIris(data.getIris());
            cd.setLogDate(data.getLogDate());
            cd.setName(data.getName());
            cd.setPitch(data.getPitch());
            cd.setRoll(data.getRoll());
            cd.setViewHeight(data.getViewHeight());
            cd.setViewUnits(data.getViewUnits());
            cd.setViewWidth(data.getViewWidth());
            cd.setX(data.getX());
            cd.setXYUnits(data.getXYUnits());
            cd.setY(data.getY());
            cd.setZ(data.getZ());
            cd.setZoom(data.getZoom());
            cd.setZUnits(data.getZUnits());
            event = new VideoFramesChangedEvent(null, Collections.singletonList(cd.getVideoFrame()));
        }
        dao.endTransaction();
        dao.close();
        if (event != null) {
            EventBus.publish(event);
        }
    }
}
