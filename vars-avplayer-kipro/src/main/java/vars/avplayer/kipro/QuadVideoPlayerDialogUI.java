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
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * @author Brian Schlining
 * @since 2016-04-20T10:50:00
 */
public class QuadVideoPlayerDialogUI extends OpenVideoArchiveDialog<QuadState, QuadError> {


    private JPanel topPanel;
    private JTextField hostTextField;
    public static final String PREF_HTTP_ADDRESS = "aja-kipro-quad-address";


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
    public void setVisible(boolean b) {
        Preferences prefs = Preferences.userNodeForPackage(getClass());
        String address = prefs.get(PREF_HTTP_ADDRESS, "");
        if (address != null && !address.isEmpty()) {
            getHostTextField().setText(address);
        }
        super.setVisible(b);
    }

    @Override
    public VideoArchive openVideoArchive() {
        saveQuadHTTPAddress(getQuadHTTPAddress());
        return super.openVideoArchive();
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
            topPanel.setBorder(new TitledBorder(null, "KiPro Quad Remote Address",
                    TitledBorder.LEADING, TitledBorder.TOP, null, null));
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



    private void saveQuadHTTPAddress(String address) {
        if (address != null && !address.isEmpty()) {
            Preferences prefs = Preferences.userNodeForPackage(getClass());
            prefs.put(PREF_HTTP_ADDRESS, address);
            try {
                prefs.flush();
            }
            catch (BackingStoreException e) {
                log.warn("Failed to save preference of '" + PREF_HTTP_ADDRESS + "'");
            }
        }
    }
}
