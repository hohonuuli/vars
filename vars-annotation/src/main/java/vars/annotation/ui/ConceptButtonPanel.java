/*
 * @(#)ConceptButtonPanel.java   2009.11.16 at 09:10:47 PST
 *
 * Copyright 2009 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.annotation.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.swing.JFancyButton;
import org.mbari.util.Dispatcher;
import org.mbari.util.IObserver;
import vars.annotation.ui.dialogs.NewConceptButtonTabDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vars.UserAccount;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.buttons.NewObservationUsingConceptNameButton;
import vars.jpa.VarsUserPreferences;

/**
 * Intended to replace ConceptButtonPanel. Still a work in progress.
 */
public class ConceptButtonPanel extends JPanel {

    private JPanel buttonPanel = null;
    private JButton lockButton = null;
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     *     The button to move a new tab
     */
    private final JButton moveTabButton = null;

    /**
     *     The button to create a new tab
     */
    private JButton newTabButton = null;
    private ConceptButtonOverviewPanel overviewPanel = null;

    /**
     *     The button to remove a new tab
     */
    private JButton removeTabButton = null;

    /**
     *     The button to rename a new tab
     */
    private JButton renameTabButton = null;
    private boolean locked = false;
    private JButton showOverviewButton = null;
    private Preferences userPreferences = null;
    private boolean showOverview = false;
    private JCheckBox dragLockCb;
    private final ImageIcon hideIndexIcon;
    private final ImageIcon lockedIcon;
    private ActionAdapter newTabAction;
    private ActionAdapter removeTabAction;
    private ActionAdapter renameTabAction;
    private final ImageIcon showIndexIcon;

    /**
     *     The JTabbedPane to hold the ConceptButtons
     */
    private JTabbedPane tabbedPane;
    private final ToolBelt toolBelt;
    private final ImageIcon unlockedIcon;

    /**
     * Constructs ...
     *
     * @param toolBelt
     */
    public ConceptButtonPanel(final ToolBelt toolBelt) {
        super();
        this.toolBelt = toolBelt;
        lockedIcon = new ImageIcon(getClass().getResource("/images/vars/annotation/lock-16.png"));
        unlockedIcon = new ImageIcon(getClass().getResource("/images/vars/annotation/lock_open-16.png"));
        showIndexIcon = new ImageIcon(getClass().getResource("/images/vars/annotation/16px/index_up.png"));
        hideIndexIcon = new ImageIcon(getClass().getResource("/images/vars/annotation/16px/index_down.png"));
        initialize();
    }

    /**
     */
    public void changeUserPreferences() {
        
        userPreferences = (Preferences) Lookup.getPreferencesDispatcher().getValueObject();

        if (userPreferences != null) {
            loadTabsFromPreferences();
        }
        else {
            getTabbedPane().removeAll();
            getOverviewPanel().removeAll();
        }
    }

    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
            buttonPanel.add(getNewTabButton());
            buttonPanel.add(getRenameTabButton());
            buttonPanel.add(getRemoveTabButton());
            buttonPanel.add(getLockButton());
            buttonPanel.add(getShowOverviewButton());
            buttonPanel.add(Box.createVerticalGlue());
        }

        return buttonPanel;
    }

    protected JButton getLockButton() {
        if (lockButton == null) {
            lockButton = new JFancyButton();
            lockButton.setText("");
            lockButton.setToolTipText("Buttons on the tabs can not be reordered when locked");
            lockButton.setIcon(unlockedIcon);

            final Dispatcher dispatcher = Dispatcher.getDispatcher(
                NewObservationUsingConceptNameButton.DISPATCHER_KEY_DRAG_LOCK);

            /*
             * If the button is locked then we want to prevent the
             * reordering of buttons. We do this by setting a Boolean value in
             * a Dispatcher. The buttons will ignore drag events when the boolean locked
             * is true.
             */
            lockButton.addActionListener(new ActionListener() {

                public void actionPerformed(final ActionEvent e) {
                    locked = !locked;

                    final Boolean isDragLocked = Boolean.valueOf(locked);

                    dispatcher.setValueObject(isDragLocked);
                }

            });

            /*
             * Listen to the state of the lock and set the icon appropriately
             */
            dispatcher.addPropertyChangeListener(new PropertyChangeListener() {

                public void propertyChange(final PropertyChangeEvent evt) {
                    final boolean locked = ((Boolean) evt.getNewValue()).booleanValue();

                    if (locked) {
                        lockButton.setIcon(lockedIcon);
                    }
                    else {
                        lockButton.setIcon(unlockedIcon);
                    }
                }

            });

        }

        return lockButton;
    }

    protected JButton getMoveTabButton() {
        if (moveTabButton == null) {}

        return moveTabButton;
    }

    protected ActionAdapter getNewTabAction() {
        if (newTabAction == null) {
            newTabAction = new NewTabAction();
            newTabAction.setEnabled(false);
        }

        return newTabAction;
    }

    protected JButton getNewTabButton() {
        if (newTabButton == null) {
            newTabButton = new JFancyButton(getNewTabAction());
            newTabButton.setText("");
            newTabButton.setToolTipText("Create New Tab");
            newTabButton.setMnemonic(KeyEvent.VK_UNDEFINED);
            newTabButton.setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/add2-16.png")));
        }

        return newTabButton;
    }

    protected ConceptButtonOverviewPanel getOverviewPanel() {
        if (overviewPanel == null) {
            overviewPanel = new ConceptButtonOverviewPanel(toolBelt);
        }

        return overviewPanel;
    }

    protected ActionAdapter getRemoveTabAction() {
        if (removeTabAction == null) {
            removeTabAction = new RemoveTabAction();
            removeTabAction.setEnabled(false);
        }

        return removeTabAction;
    }

    protected JButton getRemoveTabButton() {
        if (removeTabButton == null) {
            removeTabButton = new JFancyButton(getRemoveTabAction());
            removeTabButton.setText("");
            removeTabButton.setToolTipText("Remove Tab");
            removeTabButton.setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/delete2-16.png")));
        }

        return removeTabButton;
    }

    protected ActionAdapter getRenameTabAction() {
        if (renameTabAction == null) {
            renameTabAction = new ActionAdapter() {

                public void doAction() {}
            };
        }

        return renameTabAction;
    }

    protected JButton getRenameTabButton() {
        if (renameTabButton == null) {
            renameTabButton = new JFancyButton(getRenameTabAction());
            renameTabButton.setText("");
            renameTabButton.setToolTipText("Rename Tab");
            renameTabButton.setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/replace2-16.png")));
            renameTabButton.setEnabled(false);
        }

        return renameTabButton;
    }

    protected JButton getShowOverviewButton() {
        if (showOverviewButton == null) {
            showOverviewButton = new JFancyButton();
            showOverviewButton.setText("");
            showOverviewButton.setIcon(showIndexIcon);
            showOverviewButton.setToolTipText("Show overview tab");
            showOverviewButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    showOverview = !showOverview;

                    if (showOverview) {
                        final ConceptButtonOverviewPanel panel = getOverviewPanel();

                        showOverviewButton.setIcon(hideIndexIcon);
                        panel.loadPreferences();    // refreshes the panel
                        getTabbedPane().add("  OVERVIEW  ", panel);
                        showOverviewButton.setToolTipText("Hide overview tab");
                        getTabbedPane().setSelectedComponent(panel);

                    }
                    else {
                        showOverviewButton.setIcon(showIndexIcon);
                        getTabbedPane().remove(getOverviewPanel());
                        showOverviewButton.setToolTipText("Show overview tab");
                    }

                }
            });

        }

        return showOverviewButton;
    }

    protected JTabbedPane getTabbedPane() {
        if (tabbedPane == null) {
            tabbedPane = new JTabbedPane(SwingConstants.BOTTOM);
        }

        return tabbedPane;
    }

    private void initialize() {


        setOpaque(true);
        setBackground(UIManager.getColor("control"));
        setLayout(new BorderLayout());
        add(getTabbedPane(), BorderLayout.CENTER);
        add(getButtonPanel(), BorderLayout.EAST);
        
        Lookup.getUserAccountDispatcher().addPropertyChangeListener(new PropertyChangeListener() {
            
            public void propertyChange(PropertyChangeEvent evt) {
                final boolean enabled = (evt.getNewValue() == null) ? false : true;

                getNewTabAction().setEnabled(enabled);
                getRenameTabAction().setEnabled(enabled);
                getRemoveTabAction().setEnabled(enabled);
                
            }
        });
        
        
        Lookup.getPreferencesDispatcher().addPropertyChangeListener(new PropertyChangeListener() {
            
            public void propertyChange(PropertyChangeEvent evt) {
                changeUserPreferences();
            }
        });

    }

    private void loadTabsFromPreferences() {
        getTabbedPane().removeAll();

        final Preferences cpPrefs = userPreferences.node("CP");

        if (cpPrefs != null) {
            String[] tabNames = null;

            try {
                tabNames = cpPrefs.childrenNames();
            }
            catch (final BackingStoreException bse) {
                log.error("Problem loading user tabs.", bse);
            }

            for (int i = 0; i < tabNames.length; i++) {
                final Preferences tabPrefs = cpPrefs.node(tabNames[i]);
                final String tabName = tabPrefs.get("tabName", "dummy");
                final ConceptButtonDropPanel panel = new ConceptButtonDropPanel(cpPrefs.node(tabNames[i]), toolBelt);

                getTabbedPane().add(tabName, panel);
            }
        }

        setVisible(true);
    }

    private class NewTabAction extends ActionAdapter {


        /**
         */
        public void doAction() {
            
            final UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher();

            if (userAccount == null) {
                JOptionPane.showMessageDialog(ConceptButtonPanel.this, "You must be logged in to create a new tab");

                return;
            }

            final NewConceptButtonTabDialog newConceptButtonTabDialog = new NewConceptButtonTabDialog(
                "Enter New Tab Name");
            String tabName = null;

            if (newConceptButtonTabDialog != null) {
                tabName = newConceptButtonTabDialog.getReturnValue();
            }

            if (tabName != null) {

                // First thing to do is check to make sure no tab is already there with that name
                if ((userPreferences != null) && (userPreferences.node("CP") != null)) {
                    final Preferences cpPrefs = userPreferences.node("CP");
                    String[] cpTabs = null;

                    try {
                        cpTabs = cpPrefs.childrenNames();
                    }
                    catch (final BackingStoreException bse) {
                        log.error("Caught an Exception", bse);
                    }

                    boolean alreadyThere = false;

                    for (int j = 0; j < cpTabs.length; j++) {
                        final Preferences tabPrefs = cpPrefs.node("tab" + j);

                        if (tabPrefs.get("tabName", "").compareTo(tabName) == 0) {
                            alreadyThere = true;
                        }
                    }

                    if (!alreadyThere) {
                        final Preferences newTabPrefs = cpPrefs.node("tab" + cpTabs.length);

                        newTabPrefs.put("tabName", tabName);

                        final ConceptButtonDropPanel dropPanel = new ConceptButtonDropPanel(newTabPrefs, toolBelt);

                        getTabbedPane().add(tabName, dropPanel);
                    }
                    else {
                        JOptionPane.showMessageDialog(ConceptButtonPanel.this, "A tab by that name already exists");
                    }
                }
            }

            newConceptButtonTabDialog.dispose();
        }
    }


    private class RemoveTabAction extends ActionAdapter {


        /**
         * <p><!-- Method description --></p>
         *
         */
        public void doAction() {
            
            final UserAccount userAccount = (UserAccount)  Lookup.getUserAccountDispatcher();

            if (userAccount != null) {
                final int response = JOptionPane.showConfirmDialog(ConceptButtonPanel.this,
                    "This action will remove the currently selected tab.  Are you sure?");

                switch (response) {
                case JOptionPane.YES_OPTION:

                    // Grab the CP node from user prefs
                    final Preferences cpPrefs = userPreferences.node("CP");

                    // Get the current tab number
                    final int currentTabNumber = getTabbedPane().getSelectedIndex();

                    // Remove the node at the current tab number
                    try {
                        cpPrefs.node("tab" + currentTabNumber).removeNode();
                    }
                    catch (final BackingStoreException bse) {
                        log.error("Problem removing a tab.", bse);
                    }

                    // Now I have to rename all the rest of the tabs to move them up
                    for (int i = (currentTabNumber + 1); i < getTabbedPane().getTabCount(); i++) {
                        VarsUserPreferences.copyPrefs(cpPrefs.node("tab" + i), cpPrefs.node("tab" + (i - 1)));

                        try {
                            cpPrefs.node("tab" + i).removeNode();
                        }
                        catch (final BackingStoreException bse) {
                            log.error("Problem removing a tab from preferences.", bse);
                        }
                    }

                    loadTabsFromPreferences();

                    break;

                case JOptionPane.NO_OPTION:
                    break;

                case JOptionPane.CANCEL_OPTION:
                    break;
                }
            }
        }
    }


    private class RenameTabAction extends ActionAdapter {

        public void doAction() {
            
            final UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher();

            if (userAccount == null) {
                return;
            }
        }
    }
}
