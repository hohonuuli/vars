package vars.avplayer.rs422;

import org.mbari.awt.event.NonDigitConsumingKeyListener;
import org.mbari.util.Tuple2;
import org.mbari.vcr4j.udp.UDPError;
import org.mbari.vcr4j.udp.UDPState;
import vars.ToolBelt;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;
import vars.avplayer.OpenVideoArchiveDialog;
import vars.avplayer.OpenVideoArchivePanel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.prefs.Preferences;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

/**
 * @author Brian Schlining
 * @since 2016-04-19T13:46:00
 */
public class UDPVideoPlayerDialogUI extends OpenVideoArchiveDialog<UDPState, UDPError> {

    private JPanel topPanel;
    private JTextField hostTextField;
    private JTextField portTextField;


    public UDPVideoPlayerDialogUI(Window parent, ToolBelt toolBelt) {
        super(parent, toolBelt);
        initialize();
        pack();
    }

    private void initialize() {
        setPreferredSize(new Dimension(475, 550));
        getContentPane().add(getTopPanel(), BorderLayout.NORTH);
    }

    public JPanel getTopPanel() {
        if (topPanel == null) {
            topPanel = new JPanel();
            topPanel.setBorder(new TitledBorder(null, "Remote Timecode Server", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            
            JLabel lblHost = new JLabel("Host:");
            JLabel lblPort = new JLabel("Port:");
            
            GroupLayout gl_topPanel = new GroupLayout(topPanel);
            gl_topPanel.setHorizontalGroup(
            	gl_topPanel.createParallelGroup(Alignment.LEADING)
            		.addGroup(gl_topPanel.createSequentialGroup()
            			.addContainerGap()
            			.addGroup(gl_topPanel.createParallelGroup(Alignment.TRAILING)
            				.addComponent(lblHost)
            				.addComponent(lblPort))
            			.addPreferredGap(ComponentPlacement.UNRELATED)
            			.addGroup(gl_topPanel.createParallelGroup(Alignment.LEADING)
            				.addComponent(getPortTextField(), GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
            				.addComponent(getHostTextField(), GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE))
            			.addContainerGap())
            );
            gl_topPanel.setVerticalGroup(
            	gl_topPanel.createParallelGroup(Alignment.LEADING)
            		.addGroup(Alignment.TRAILING, gl_topPanel.createSequentialGroup()
            			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            			.addGroup(gl_topPanel.createParallelGroup(Alignment.BASELINE)
            				.addComponent(lblHost)
            				.addComponent(getHostTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            			.addPreferredGap(ComponentPlacement.RELATED)
            			.addGroup(gl_topPanel.createParallelGroup(Alignment.BASELINE)
            				.addComponent(getPortTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            				.addComponent(lblPort))
            			.addGap(6))
            );
            topPanel.setLayout(gl_topPanel);
        }
        return topPanel;
    }

    public JTextField getHostTextField() {
        if (hostTextField == null) {
            hostTextField = new JTextField();
            hostTextField.setColumns(10);
        }
        return hostTextField;
    }

    public JTextField getPortTextField() {
        if (portTextField == null) {
            portTextField = new JTextField();
            portTextField.addKeyListener(new NonDigitConsumingKeyListener());
            portTextField.setColumns(10);
        }
        return portTextField;
    }

    @Override
    protected VideoArchive openVideoArchiveByParams() {
        VideoArchiveDAO dao = getToolBelt().getAnnotationDAOFactory().newVideoArchiveDAO();
        dao.startTransaction();
        OpenVideoArchivePanel p = getCenterPanel();
        int sequenceNumber = Integer.parseInt(p.getSequenceNumberTextField().getText());
        String platform = (String) p.getCameraPlatformComboBox().getSelectedItem();
        int tapeNumber = Integer.parseInt(p.getTapeNumberTextField().getText());
        RS422VideoParams videoParams = new RS422VideoParams(platform, sequenceNumber, tapeNumber, p.getHdCheckBox().isSelected());
        String videoArchiveName = videoParams.getVideoArchiveName();
        VideoArchive videoArchive = dao.findOrCreateByParameters(platform, sequenceNumber, videoArchiveName);
        dao.endTransaction();
        Preferences prefs = Preferences.userNodeForPackage(OpenVideoArchiveDialog.class);
        prefs.put(PREF_PLATFORM_NAME, platform);
        return videoArchive;
    }

    public Tuple2<String, Integer> getRemoteConnectionParams() {
        return new Tuple2<>(getHostTextField().getText(),
                Integer.parseInt(getPortTextField().getText()));
    }
}
