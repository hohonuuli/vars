/*
 * Copyright 2005 MBARI
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1
 * (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/*
Created on Jan 10, 2005
 *
TODO To change the template for this generated file go to
Window - Preferences - Java - Code Style - Code Templates
 */
package org.mbari.vars.annotation.ui;

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
import org.mbari.vars.annotation.ui.dialogs.NewConceptButtonTabDialog;
import org.mbari.vars.annotation.ui.dispatchers.PersonDispatcher;
import org.mbari.vars.annotation.ui.dispatchers.PreferencesDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.ui.ToolBelt;
import vars.jpa.VarsUserPreferences;

/**
 * Intended to replace ConceptButtonPanel. Still a work in progress.
 */
public class ConceptButtonPanel extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = -8103461282255784621L;
    private static final Logger log = LoggerFactory.getLogger(ConceptButtonPanel.class);

    private JCheckBox dragLockCb;


    private JButton lockButton = null;

    private final ImageIcon lockedIcon;
    
    private final ImageIcon showIndexIcon;
    private final ImageIcon hideIndexIcon;

    /**
     *     The button to move a new tab
     */
    private final JButton moveTabButton = null;


    private ActionAdapter newTabAction;

    /**
     *     The button to create a new tab
     */
    private JButton newTabButton = null;

    private ActionAdapter removeTabAction;

    /**
     *     The button to remove a new tab
     */
    private JButton removeTabButton = null;

    private ActionAdapter renameTabAction;

    /**
     *     The button to rename a new tab
     */
    private JButton renameTabButton = null;


    private boolean locked = false;

    /**
     *     The JTabbedPane to hold the ConceptButtons
     */
    private JTabbedPane tabbedPane;

    private final ImageIcon unlockedIcon;

    private Preferences userPreferences = null;

    private JPanel buttonPanel = null;
    
    private JButton showOverviewButton = null;
    
    private boolean showOverview = false;
    
    private ConceptButtonOverviewPanel overviewPanel = null;

    private final ToolBelt toolBelt;
    
    /**
     *
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
     * <p><!-- Method description --></p>
     *
     */
    public void changeUserPreferences() {
        userPreferences = PreferencesDispatcher.getInstance().getPreferences();

        if (userPreferences != null) {
            loadTabsFromPreferences();
        }
        else {
            getTabbedPane().removeAll();
            getOverviewPanel().removeAll();
        }
    }

    /**
     *     @return  the lockButton
     *     @uml.property  name="lockButton"
     */
    protected JButton getLockButton() {
        if (lockButton == null) {
            lockButton = new JFancyButton();
            lockButton.setText("");
            lockButton.setToolTipText("Buttons on the tabs can not be reordered when locked");
            lockButton.setIcon(unlockedIcon);

            final Dispatcher dispatcher =
                Dispatcher.getDispatcher(NewObservationUsingConceptNameButton.DISPATCHER_KEY_DRAG_LOCK);

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
                        panel.loadPreferences(); // refreshes the panel
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
    
    
    protected ConceptButtonOverviewPanel getOverviewPanel() {
        if (overviewPanel == null) {
            overviewPanel = new ConceptButtonOverviewPanel();
        }
        return overviewPanel;
    }

    /**
     *     @return  Returns the moveTabButton.
     *     @uml.property  name="moveTabButton"
     */
    protected JButton getMoveTabButton() {
        if (moveTabButton == null) {}

        return moveTabButton;
    }

    /**
     *     @return  Returns the newTabAction.
     *     @uml.property  name="newTabAction"
     */
    protected ActionAdapter getNewTabAction() {
        if (newTabAction == null) {
            newTabAction = new NewTabAction();
            newTabAction.setEnabled(false);
        }

        return newTabAction;
    }

    /**
     *     @return  Returns the newTabButton.
     *     @uml.property  name="newTabButton"
     */
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

    /**
     *     @return  Returns the removeTabAction.
     *     @uml.property  name="removeTabAction"
     */
    protected ActionAdapter getRemoveTabAction() {
        if (removeTabAction == null) {
            removeTabAction = new RemoveTabAction();
            removeTabAction.setEnabled(false);

            //removeTabAction.setIcon(new ImageIcon(getClass().getResource("/images/vars/knowledgebase/delete2.png")));
        }

        return removeTabAction;
    }

    /**
     *     @return  Returns the removeTabButton.
     *     @uml.property  name="removeTabButton"
     */
    protected JButton getRemoveTabButton() {
        if (removeTabButton == null) {
            removeTabButton = new JFancyButton(getRemoveTabAction());
            removeTabButton.setText("");
            removeTabButton.setToolTipText("Remove Tab");
            removeTabButton.setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/delete2-16.png")));
        }

        return removeTabButton;
    }

    /**
     *     @return  Returns the renameTabAction.
     *     @uml.property  name="renameTabAction"
     */
    protected ActionAdapter getRenameTabAction() {
        if (renameTabAction == null) {
            renameTabAction = new ActionAdapter() {

                /**
                 *
                 */
                private static final long serialVersionUID = -2690503024126090264L;

                public void doAction() {}
            };
        }

        return renameTabAction;
    }

    /**
     *     @return  Returns the renameTabButton.
     *     @uml.property  name="renameTabButton"
     */
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

    /**
     *     @return  Returns the tabbedPane.
     *     @uml.property  name="tabbedPane"
     */
    protected JTabbedPane getTabbedPane() {
        if (tabbedPane == null) {
            tabbedPane = new JTabbedPane(SwingConstants.BOTTOM);
        }

        return tabbedPane;
    }

    /**
     * <p><!-- Method description --></p>
     *
     */
    private void initialize() {

        
        setOpaque(true);
        setBackground(UIManager.getColor("control"));
        setLayout(new BorderLayout());
        add(getTabbedPane(), BorderLayout.CENTER);
        add(getButtonPanel(), BorderLayout.EAST);
        
        PersonDispatcher.getInstance().addObserver(new IObserver() {

            public void update(final Object obj, final Object changeCode) {
                final boolean enabled = (obj == null) ? false : true;
                getNewTabAction().setEnabled(enabled);
                getRenameTabAction().setEnabled(enabled);
                getRemoveTabAction().setEnabled(enabled);
            }

        });
        PreferencesDispatcher.getInstance().addObserver(new IObserver() {

            public void update(final Object theObervered, final Object changeCode) {
                changeUserPreferences();
            }
        });
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

    /**
     * <p><!-- Method description --></p>
     *
     */
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
         *
         */
        private static final long serialVersionUID = 2099427178247690538L;

        /**
         * <p><!-- Method description --></p>
         *
         */
        public void doAction() {
            final String currentUser = PersonDispatcher.getInstance().getPerson();
            if (currentUser == null) {
                JOptionPane.showMessageDialog(ConceptButtonPanel.this, "You must be logged in to create a new tab");

                return;
            }

            final NewConceptButtonTabDialog newConceptButtonTabDialog =
                new NewConceptButtonTabDialog("Enter New Tab Name");
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
         *
         */
        private static final long serialVersionUID = -6358272395282466591L;

        /**
         * <p><!-- Method description --></p>
         *
         */
        public void doAction() {
            final String currentUser = PersonDispatcher.getInstance().getPerson();
            if (currentUser != null) {
                final int response = JOptionPane.showConfirmDialog(ConceptButtonPanel.this,
                                         "This action will remove the currently selected tab.  Are you sure?");
                switch (response) {
                    case JOptionPane.YES_OPTION :

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

                    case JOptionPane.NO_OPTION :
                        break;

                    case JOptionPane.CANCEL_OPTION :
                        break;
                }
            }
        }
    }

    private class RenameTabAction extends ActionAdapter {

        /**
         *
         */
        private static final long serialVersionUID = 8853058093763546773L;

        /**
         * <p><!-- Method description --></p>
         *
         */
        public void doAction() {
            final String currentUser = PersonDispatcher.getInstance().getPerson();
            if (currentUser == null) {
                return;
            }
        }
    }
}
