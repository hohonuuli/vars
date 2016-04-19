package vars.avplayer;

import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoState;
import vars.ToolBelt;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;
import vars.shared.ui.dialogs.StandardDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Enumeration;

/**
 * @author Brian Schlining
 * @since 2016-04-19T11:16:00
 */
public abstract class OpenVideoArchiveDialog<S extends VideoState, E extends VideoError>
        extends StandardDialog
        implements VideoPlayerDialogUI<S, E> {

    private final ToolBelt toolBelt;
    private Runnable okRunnable = () -> {};
    private Runnable cancelRunnable = () -> this.setVisible(false);
    private OpenVideoArchivePanel centerPanel;
    private enum OpenType { BY_PARAMS, BY_NAME, EXISTING; }

    public OpenVideoArchiveDialog(final Window parent, ToolBelt toolBelt) {
        super(parent);
        this.toolBelt = toolBelt;
        initialize();
        getRootPane().setDefaultButton(getOkayButton());
    }

    private void initialize() {
        setPreferredSize(new Dimension(475, 475));
        getOkayButton().addActionListener(e -> okRunnable.run());
        getCancelButton().addActionListener(e -> cancelRunnable.run());
        getContentPane().add(getCenterPanel(), BorderLayout.CENTER);
    }

    protected ToolBelt getToolBelt() {
        return toolBelt;
    }

    protected OpenVideoArchivePanel getCenterPanel() {
        if (centerPanel == null) {
            centerPanel = new OpenVideoArchivePanel(toolBelt);
            KeyListener keyListener = new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        getOkayButton().doClick();
                    }
                    super.keyReleased(e);
                }
            };
            centerPanel.getHdCheckBox().addKeyListener(keyListener);
            centerPanel.getTapeNumberTextField().addKeyListener(keyListener);
            centerPanel.getTapeNumberTextField().addKeyListener(keyListener);
            centerPanel.getSequenceNumberByNameTextField().addKeyListener(keyListener);
        }
        return centerPanel;
    }

    @Override
    public void onCancel(Runnable fn) {
        cancelRunnable = fn;
    }

    @Override
    public void onOkay(Runnable fn) {
        okRunnable = fn;
    }

    public VideoArchive openVideoArchive() {

        Enumeration<AbstractButton> e = getCenterPanel().getButtonGroup().getElements();
        OpenType openType = null;
        while (e.hasMoreElements()) {
            AbstractButton b = e.nextElement();
            if (b.isSelected()) {
                openType = OpenType.valueOf(b.getName());
                break;
            }
        }

        VideoArchive videoArchive = null;
        switch (openType) {
            case BY_NAME:
                videoArchive = openVideoArchiveByName();
                break;
            case BY_PARAMS:
                videoArchive = openVideoArchiveByParams();
                break;
            case EXISTING:
                videoArchive = openExistingVideoArchive();
                break;
            default:
        }
        return videoArchive;

    }

    protected VideoArchive openVideoArchiveByName() {
        OpenVideoArchivePanel cp = getCenterPanel();
        VideoArchiveDAO dao = toolBelt.getAnnotationDAOFactory().newVideoArchiveDAO();
        dao.startTransaction();
        String videoArchiveName = cp.getNameTextField().getText();
        int sequenceNumber = Integer.parseInt(cp.getSequenceNumberByNameTextField().getText());
        String platform = (String) cp.getCameraPlatformComboBox().getSelectedItem();
        VideoArchive videoArchive = dao.findOrCreateByParameters(platform, sequenceNumber, videoArchiveName);
        dao.endTransaction();
        return videoArchive;
    }

    protected abstract VideoArchive openVideoArchiveByParams();

    protected VideoArchive openExistingVideoArchive() {
        OpenVideoArchivePanel cp = getCenterPanel();
        VideoArchiveDAO dao = toolBelt.getAnnotationDAOFactory().newVideoArchiveDAO();
        dao.startTransaction();
        String name = (String) cp.getExistingNamesComboBox().getSelectedItem();
        VideoArchive videoArchive = dao.findByName(name);
        dao.endTransaction();
        return videoArchive;
    }
}
