package vars.avplayer;

import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

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
    public static final String PREF_PLATFORM_NAME = "platform-name";
    protected final Logger log = LoggerFactory.getLogger(getClass());

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

    protected OpenType getOpenType() {
        Enumeration<AbstractButton> e = getCenterPanel().getButtonGroup().getElements();
        OpenType openType = null;
        while (e.hasMoreElements()) {
            AbstractButton b = e.nextElement();
            if (b.isSelected()) {
                openType = OpenType.valueOf(b.getName());
                break;
            }
        }
        return openType == null ? OpenType.BY_PARAMS : openType;
    }

    public VideoArchive openVideoArchive() {

        VideoArchive videoArchive = null;
        switch (getOpenType()) {
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
        if (videoArchive != null) {
            savePlatformPreferences(videoArchive.getVideoArchiveSet().getPlatformName());
        }
        return videoArchive;

    }

    private void savePlatformPreferences(String platform) {
        if (platform != null && !platform.isEmpty()) {
            Preferences prefs = Preferences.userNodeForPackage(getClass());
            prefs.put(PREF_PLATFORM_NAME, platform);
            try {
                prefs.flush();
            }
            catch (BackingStoreException e) {
                log.warn("Failed to save preference of '" + PREF_PLATFORM_NAME + "'");
            }
        }
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

    /**
     * We read the last used platform form the preferences and set it.
     * @param b
     */
    @Override
    public void setVisible(boolean b) {
        if (b) {
            Preferences prefs = Preferences.userNodeForPackage(getClass());
            String platformName = prefs.get(PREF_PLATFORM_NAME, "");
            if (platformName.isEmpty()) {
                try {
                    platformName = getCenterPanel().listCameraPlatforms()[0];
                }
                catch (Exception e) {
                    // Bummer
                }
            }
            if (!platformName.isEmpty()) {
                getCenterPanel().getCameraPlatformByNameComboBox().setSelectedItem(platformName);
                getCenterPanel().getCameraPlatformComboBox().setSelectedItem(platformName);
            }
        }
        super.setVisible(b);
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
