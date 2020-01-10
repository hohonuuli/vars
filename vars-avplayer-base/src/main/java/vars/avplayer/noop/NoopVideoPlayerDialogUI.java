package vars.avplayer.noop;

import org.mbari.vcr4j.adapter.noop.NoopVideoError;
import org.mbari.vcr4j.adapter.noop.NoopVideoState;
import vars.ToolBelt;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;
import vars.avplayer.OpenVideoArchiveDialog;
import vars.avplayer.OpenVideoArchivePanel;
import vars.avplayer.SimpleVideoParams;

import java.awt.*;

/**
 * @author Brian Schlining
 * @since 2016-05-09T11:39:00
 */
public class NoopVideoPlayerDialogUI extends OpenVideoArchiveDialog<NoopVideoState, NoopVideoError> {


    public NoopVideoPlayerDialogUI(Window parent, ToolBelt toolBelt) {
        super(parent, toolBelt);
        setPreferredSize(new Dimension(475, 550));
        pack();
    }

    @Override
    protected VideoArchive openVideoArchiveByParams() {
        VideoArchiveDAO dao = getToolBelt().getAnnotationDAOFactory().newVideoArchiveDAO();
        dao.startTransaction();
        OpenVideoArchivePanel p = getCenterPanel();
        int sequenceNumber = Integer.parseInt(p.getSequenceNumberTextField().getText());
        String platform = (String) p.getCameraPlatformComboBox().getSelectedItem();
        int tapeNumber = Integer.parseInt(p.getTapeNumberTextField().getText());
        SimpleVideoParams videoParams = new SimpleVideoParams(platform, sequenceNumber, tapeNumber, p.getHdCheckBox().isSelected());
        String videoArchiveName = videoParams.getVideoArchiveName();
        VideoArchive videoArchive = dao.findOrCreateByParameters(platform, sequenceNumber, videoArchiveName);
        dao.endTransaction();
        return videoArchive;
    }
}
