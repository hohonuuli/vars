package vars.avplayer.kipro;

import org.mbari.vcr4j.kipro.QuadError;
import org.mbari.vcr4j.kipro.QuadState;
import vars.ToolBelt;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;
import vars.avplayer.IVideoParams;
import vars.avplayer.OpenVideoArchiveDialog;
import vars.avplayer.OpenVideoArchivePanel;
import vars.avplayer.SimpleVideoParams;

import javax.swing.*;
import java.awt.*;

/**
 * @author Brian Schlining
 * @since 2016-04-20T10:50:00
 */
public class QuadVideoPlayerDialogUI extends OpenVideoArchiveDialog<QuadState, QuadError> {


    private JPanel topPanel;
    private JTextField hostTextField;


    public QuadVideoPlayerDialogUI(Window parent, ToolBelt toolBelt) {
        super(parent, toolBelt);
        initialize();
        pack();
    }

    private void initialize() {
        setPreferredSize(new Dimension(475, 550));
        getContentPane().add(getTopPanel(), BorderLayout.NORTH);
    }

    @Override
    protected VideoArchive openVideoArchiveByParams() {
        VideoArchiveDAO dao = getToolBelt().getAnnotationDAOFactory().newVideoArchiveDAO();
        dao.startTransaction();
        OpenVideoArchivePanel p = getCenterPanel();
        int sequenceNumber = Integer.parseInt(p.getSequenceNumberTextField().getText());
        String platform = (String) p.getCameraPlatformComboBox().getSelectedItem();
        int tapeNumber = Integer.parseInt(p.getTapeNumberTextField().getText());
        boolean isHd = p.getHdCheckBox().isSelected();
        IVideoParams videoParams = new SimpleVideoParams(platform, sequenceNumber, tapeNumber, isHd);
        String videoArchiveName = videoParams.getVideoArchiveName();
        VideoArchive videoArchive = dao.findOrCreateByParameters(platform, sequenceNumber, videoArchiveName);
        dao.endTransaction();
        return videoArchive;
    }

    public JTextField getHostTextField() {
        if (hostTextField == null) {
            hostTextField = new JTextField();
            hostTextField.setColumns(10);
        }
        return hostTextField;
    }

    public JPanel getTopPanel() {
        if (topPanel == null) {
            topPanel = new JPanel();
            BoxLayout layout = new BoxLayout(topPanel, BoxLayout.X_AXIS);
            topPanel.setLayout(layout);
            topPanel.add(new JLabel("IP Address:"));
            topPanel.add(Box.createHorizontalGlue());
            topPanel.add(getHostTextField());
        }
        return topPanel;
    }

    public String getQuadHTTPAddress() {
        String ip = getHostTextField().getText();
        if (!ip.toLowerCase().startsWith("http://")) {
            ip = "http://" + ip;
        }
        return ip;
    }
}
