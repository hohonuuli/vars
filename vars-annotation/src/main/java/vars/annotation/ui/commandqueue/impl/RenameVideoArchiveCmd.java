package vars.annotation.ui.commandqueue.impl;

import org.bushe.swing.event.EventBus;
import vars.VARSException;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;
import vars.annotation.ui.StateLookup;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.eventbus.VideoArchiveChangedEvent;

/**
 * @author Brian Schlining
 * @since 2011-11-15
 */
public class RenameVideoArchiveCmd implements Command {

    private final String oldName;
    private final String newName;
    private boolean newNameApplied = false;

    public RenameVideoArchiveCmd(String oldName, String newName) {
        this.oldName = oldName;
        this.newName = newName;
    }

    @Override
    public void apply(ToolBelt toolBelt) {
        if (!newNameApplied) {
            doCommand(toolBelt, oldName, newName);
        }
    }

    @Override
    public void unapply(ToolBelt toolBelt) {
        if (newNameApplied) { // Don't undo unless we actually did the change
            doCommand(toolBelt, newName, oldName);
        }
    }

    private void doCommand(ToolBelt toolBelt, String from, String to) {
        VideoArchiveDAO videoArchiveDAO = toolBelt.getAnnotationDAOFactory().newVideoArchiveDAO();
        videoArchiveDAO.startTransaction();
        VideoArchive newVideoArchive = videoArchiveDAO.findByName(to);
        if (newVideoArchive == null) {
            VideoArchive oldVideoArchive = videoArchiveDAO.findByName(from);
            if (oldVideoArchive != null) {
                oldVideoArchive.setName(to);
                videoArchiveDAO.endTransaction();
                videoArchiveDAO.close();
                newNameApplied = !newNameApplied;
                EventBus.publish(new VideoArchiveChangedEvent(null, oldVideoArchive));
            }
            else {
                EventBus.publish(StateLookup.TOPIC_WARNING, "Unable to find a VideoArchive named " + from + " in the database");
            }
        }
        else {
            videoArchiveDAO.endTransaction();
            videoArchiveDAO.close();
            throw new VARSException("Unable to rename the VideoArchive from " + from + " to " + to +
                     ". A VideoArchive named " + to + " already exists.");
        }
    }

    @Override
    public String getDescription() {
        return "Rename a VideoArchive from " + oldName + " to " + newName;
    }
}
