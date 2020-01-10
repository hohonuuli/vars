package vars.avplayer.rs422;


import org.mbari.vcr4j.rs422.RS422Error;
import org.mbari.vcr4j.rs422.RS422State;
import org.mbari.vcr4j.rxtx.RXTX;
import org.mbari.vcr4j.rxtx.RXTXUtilities;
import org.mbari.vcr4j.rxtx.RXTXVideoIO;
import vars.ToolBelt;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;
import vars.avplayer.OpenVideoArchiveDialog;
import vars.avplayer.OpenVideoArchivePanel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * @author Brian Schlining
 * @since 2016-04-19T10:40:00
 */
public class RS422VideoPlayerDialogUI extends OpenVideoArchiveDialog<RS422State, RS422Error> {

    private JPanel topPanel;
    private JComboBox<String> serialPortComboBox;

    public RS422VideoPlayerDialogUI(final Window parent, final ToolBelt toolBelt) {
        super(parent, toolBelt);
        initialize();
        pack();

    }

    private void initialize() {
        getContentPane().add(getTopPanel(), BorderLayout.NORTH);
    }

    private JComboBox<String> getSerialPortComboBox() {
        if (serialPortComboBox == null) {
            String[] ports = RXTXVideoIO.getSerialPorts()
                    .stream()
                    .sorted()
                    .toArray(String[]::new);
//            String[] ports = RXTXUtilities.getAvailableSerialPorts()
//                    .stream()
//                    .map(CommPortIdentifier::getName)
//                    .sorted()
//                    .toArray(String[]::new);

            serialPortComboBox = new JComboBox<>(ports);
        }
        
        return serialPortComboBox;
    }

    private JPanel getTopPanel() {
        if (topPanel == null) {
            topPanel = new JPanel();
            topPanel.setLayout(new BorderLayout(0, 0));
            topPanel.setBorder(new TitledBorder(null, "Serial Port", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            topPanel.add(getSerialPortComboBox(), BorderLayout.CENTER);
        }
        return topPanel;
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
        return videoArchive;
    }

    public String getSerialPortName() {
        return (String) getSerialPortComboBox().getSelectedItem();
    }
}
