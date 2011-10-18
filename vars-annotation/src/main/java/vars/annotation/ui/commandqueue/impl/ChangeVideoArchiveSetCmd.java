package vars.annotation.ui.commandqueue.impl;

import org.bushe.swing.event.EventBus;
import vars.annotation.VideoArchiveSet;
import vars.annotation.VideoArchiveSetDAO;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.eventbus.VideoArchiveSetChangedEvent;


/**
 * @author Brian Schlining
 * @since 2011-10-18
 */
public class ChangeVideoArchiveSetCmd implements Command {

    private final Object primaryKey;
    private final DataBean original;
    private final DataBean modified;

    public ChangeVideoArchiveSetCmd(String shipName, String platformName, char formatCode, VideoArchiveSet videoArchiveSet) {
        primaryKey = videoArchiveSet.getPrimaryKey();
        original = new DataBean(videoArchiveSet);
        modified = new DataBean(shipName, platformName, formatCode);
    }



    @Override
    public void apply(ToolBelt toolBelt) {
        doChange(toolBelt, true);
    }

    @Override
    public void unapply(ToolBelt toolBelt) {
        doChange(toolBelt, false);
    }

    private void doChange(ToolBelt toolBelt, boolean isApply) {
        DataBean values = isApply ? modified : original;
        VideoArchiveSetDAO dao = toolBelt.getAnnotationDAOFactory().newVideoArchiveSetDAO();
        dao.startTransaction();
        VideoArchiveSet vas = dao.findByPrimaryKey(primaryKey);
        vas.setShipName(values.shipName);
        vas.setPlatformName(values.platformName);
        vas.setFormatCode(values.formatCode);
        dao.endTransaction();
        dao.close();
        EventBus.publish(new VideoArchiveSetChangedEvent(null, vas));
    }

    @Override
    public String getDescription() {
        return "Change VideoArchiveSet properties to " + original.shipName + ", " + original.platformName +
                ", " + original.formatCode;
    }

    private class DataBean {
        final String shipName;
        final String platformName;
        final char formatCode;

        DataBean(VideoArchiveSet videoArchiveSet) {
            this(videoArchiveSet.getShipName(),
                    videoArchiveSet.getPlatformName(), videoArchiveSet.getFormatCode());

        }

        DataBean(String shipName, String platformName, char formatCode) {
            this.shipName = shipName;
            this.platformName = platformName;
            this.formatCode = formatCode;
        }

    }
}
