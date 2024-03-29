/*
 * @(#)AssociationListEditorPanel.java   2009.12.16 at 04:21:21 PST
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



package vars.annotation.ui.roweditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.ToolTipManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.bushe.swing.event.EventBus;
import mbarix4j.awt.event.ActionAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.LinkComparator;
import vars.annotation.Association;
import vars.annotation.Observation;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.commandqueue.CommandEvent;
import vars.annotation.ui.commandqueue.impl.RemoveAssociationsCmd;
import vars.shared.ui.FancyButton;

/**
 * <p>
 * Adds or removes Associations from an VideoFrame
 * </p>
 *
 * <h2><u>UML </u></h2>
 *
 * <pre>
 *                          1
 *  [JPanel] [Observation]---[AssociationList]
 *    A         /1
 *    |
 *   1|
 *  [AssociationEditorPanel]---[AssociationListEditorPanel]
 *       |
 *       |
 *       |1
 *  [AssociationListCellRenderer]
 * </pre>
 *
 */
public class AssociationListEditorPanel extends JPanel {

    protected AssociationEditorPanel associationEditorPanel = null;
    private JButton buttonAdd = null;
    private JButton buttonEdit = null;
    private JButton buttonRemove = null;
    private final Logger log = LoggerFactory.getLogger(getClass());
    JPanel listEditorPanel = new JPanel();
    private ActionAdapter addAction;
    private JPanel buttonPanel;
    private ActionAdapter editAction;
    private boolean editingAssociation;
    JList jList;
    JScrollPane jScrollPane;
    private Observation observation;
    private ActionAdapter removeAction;
    private final ToolBelt toolBelt;

    /**
     * Constructor for the AssociationListEditorPanel object
     *
     * @param toolBelt
     */
    public AssociationListEditorPanel(ToolBelt toolBelt) {
        this.toolBelt = toolBelt;
        initialize();
    }

    private void enableButtons() {

        getButtonAdd().setEnabled(observation != null);

        boolean enable = !((getJList().getSelectedIndex() == -1) || (jList.getModel().getSize() == 0));
        getButtonRemove().setEnabled(enable);
        getButtonEdit().setEnabled(enable);

    }

    ActionAdapter getAddAction() {
        if (addAction == null) {
            addAction = new ActionAdapter() {


                public void doAction() {

                    // Add the AssociationEditorPanel
                    try {
                        getAssociationEditorPanel().setTarget(observation, null);
                    }
                    catch (final Exception ex) {
                        log.error("Failed to set target values in the AssociationEditorPanel", ex);
                    }

                    setEditingAssociation(true);
                }
            };
        }

        return addAction;
    }

    /**
     *     Gets the associationEditorPanel attribute of the AssociationListEditorPanel object
     *     @return   The associationEditorPanel value
     */
    public AssociationEditorPanel getAssociationEditorPanel() {
        if (associationEditorPanel == null) {
            associationEditorPanel = new AssociationEditorPanel(toolBelt);
            associationEditorPanel.validate();
            associationEditorPanel.addActionListener(new ActionListener() {

                public void actionPerformed(final ActionEvent e) {
                    setEditingAssociation(false);
                }
            });
        }

        return associationEditorPanel;
    }

    private JButton getButtonAdd() {
        if (buttonAdd == null) {
            buttonAdd = new FancyButton();
            buttonAdd.setOpaque(false);
            buttonAdd.setPreferredSize(new Dimension(32, 32));
            buttonAdd.setToolTipText("Add Association");
            buttonAdd.setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/add.png")));
            buttonAdd.addActionListener(getAddAction());
        }

        return buttonAdd;
    }

    private JButton getButtonEdit() {
        if (buttonEdit == null) {
            buttonEdit = new FancyButton();
            buttonEdit.addActionListener(getEditAction());
            buttonEdit.setOpaque(false);
            buttonEdit.setPreferredSize(new Dimension(32, 32));
            buttonEdit.setToolTipText("Edit Association");
            buttonEdit.setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/edit.png")));
            //buttonEdit.setEnabled(false);
        }

        return buttonEdit;
    }

    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
            buttonPanel.add(Box.createHorizontalGlue());
            buttonPanel.add(getButtonAdd());
            buttonPanel.add(getButtonEdit());
            buttonPanel.add(getButtonRemove());
        }

        return buttonPanel;
    }

    private JButton getButtonRemove() {
        if (buttonRemove == null) {
            buttonRemove = new FancyButton();
            buttonRemove.setOpaque(false);
            buttonRemove.setBackground(Color.red);
            buttonRemove.setPreferredSize(new Dimension(32, 32));
            buttonRemove.setToolTipText("Remove Association");
            buttonRemove.setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/remove.png")));
            buttonRemove.addActionListener(getRemoveAction());
        }

        return buttonRemove;
    }


    ActionAdapter getEditAction() {
        if (editAction == null) {
            editAction = new ActionAdapter() {


                public void doAction() {
                    try {
                        getAssociationEditorPanel().setTarget(observation, (Association) getJList().getSelectedValue());
                    }
                    catch (final Exception ex) {
                        log.error("Failed to set values in the AssociationEditorPanel", ex);
                    }

                    setEditingAssociation(true);
                }
            };
        }

        return editAction;
    }

    private JList getJList() {
        if (jList == null) {
            jList = new JList(new DefaultListModel());
            jList.addFocusListener(new FocusAdapter() {

                @Override
                public void focusGained(final FocusEvent evt) {
                    final JList list = (JList) evt.getComponent();
                    final ListSelectionModel selModel = list.getSelectionModel();
                    if (selModel.isSelectionEmpty()) {
                        selModel.setAnchorSelectionIndex(0);
                        selModel.setLeadSelectionIndex(0);
                    }
                }

            });

            // Turn on buttons when an item is selected
            jList.addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(final ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    enableButtons();
                }

            });
            jList.setFocusable(true);

            // set a selected index so the user has something selected when they
            // focus
            // on this list
            jList.setSelectedIndex(0);
            jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            // Create a custom List Cell Renderer that calls remoteToString
            // instead of defaulting to toString for Associations
            final AssociationListCellRenderer rendererAssoList = new AssociationListCellRenderer();
            jList.setCellRenderer(rendererAssoList);
            ToolTipManager.sharedInstance().registerComponent(jList);
        }

        return jList;
    }

    private JScrollPane getJScrollPane() {
        if (jScrollPane == null) {
            jScrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                          ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            // don't focus on these, it messes up the focus cycle
            jScrollPane.getVerticalScrollBar().setFocusable(false);
            jScrollPane.getHorizontalScrollBar().setFocusable(false);
            jScrollPane.setViewportView(getJList());
        }

        return jScrollPane;
    }

    /**
     *     Retrieve the observation whose associations are being edited.
     *     @return
     */
    public Observation getObservation() {
        return observation;
    }

    /**
     *     The removeAction removes the association selected in the list from the database, the view, and the datamodel.
     *     @return
     */
    ActionAdapter getRemoveAction() {
        if (removeAction == null) {
            removeAction = new ActionAdapter() {


                public void doAction() {

                    // if any item is selected, remove it
                    final JList j = getJList();
                    if (j.getSelectedIndex() > -1) {

                        // Get the association object from the listModel
                        final DefaultListModel listModel = (DefaultListModel) j.getModel();
                        final Association association = (Association) j.getSelectedValue();

                        // If for some reason a null value gets in the list
                        // just remove it.
                        if (association == null) {
                            listModel.remove(j.getSelectedIndex());
                            return;
                        }

                        // Remove references to the selected association from
                        // the parent and the listModel
                        listModel.remove(j.getSelectedIndex());
                        Command command = new RemoveAssociationsCmd(new ArrayList<Association>() {{ add(association); }});
                        CommandEvent commandEvent = new CommandEvent(command);
                        EventBus.publish(commandEvent);
                    }
                }
            };
        }

        return removeAction;
    }

    void initialize() {
        listEditorPanel.setOpaque(false);
        layoutPanel();
        this.setFocusable(false);
        addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(final FocusEvent e) {

                if (getJList().getModel().getSize() == 0) {
                    getAddAction().doAction();
                }
            }

        });
    }

    /**
     * @return  true if editing a single association. False if editing
     *             the AssociationList.
     */
    public boolean isEditingAssociation() {
        return editingAssociation;
    }

    /**
     * This method is required for focus management. All components are remove
     * from the view at certain times so that they can't be focused on. but they
     * bet added back later
     *
     */
    private void layoutPanel() {
        setLayout(new BorderLayout());
        add(getJScrollPane(), BorderLayout.CENTER);
        add(getButtonPanel(), BorderLayout.SOUTH);
    }


    /**
     *     Toggles which panel is visible. If true, the AssociationEditorPanel is visible. If false, the AssociationListEditorPanel is visible.
     *     @param isEditingItem  true if editing a single association.
     */
    public void setEditingAssociation(final boolean isEditingItem) {
        this.editingAssociation = isEditingItem;

        if (isEditingItem) {

            // Remove the ListEditorPanel from the view
            listEditorPanel.setVisible(false);
            this.removeAll();

            final JPanel p = getAssociationEditorPanel();
            add(p, BorderLayout.CENTER);

            // all three of these properties need to be set to true
            // so that the FocusManager will see this component as
            // being a suitable host for focus.
            p.setVisible(true);
            p.setEnabled(true);
            p.setFocusable(true);
            p.requestFocus();
        }
        else {
            getAssociationEditorPanel().setVisible(false);
            this.remove(getAssociationEditorPanel());
            layoutPanel();
            listEditorPanel.setVisible(true);
            listEditorPanel.requestFocus();
        }
    }

    /**
     *  Sets the enabled attribute of the AssociationListEditorPanel object
     *
     * @param  shouldEnable The new enabled value
     */
    @Override
    public void setEnabled(final boolean shouldEnable) {
        jList.setEnabled(shouldEnable);
        jScrollPane.setEnabled(shouldEnable);
        listEditorPanel.setEnabled(shouldEnable);

        if (shouldEnable) {
            enableButtons();
        }
        else {
            getButtonAdd().setEnabled(false);
            getButtonRemove().setEnabled(false);
            getButtonEdit().setEnabled(false);
        }
    }

    /**
     *     Sets the observation whos associations are to be edited.
     *
     * @param newObservation
     */
    public void setObservation(final Observation newObservation) {

        final DefaultListModel listModel = (DefaultListModel) getJList().getModel();
        listModel.clear();

        if (newObservation != null) {
            // Sort them before adding
            List<Association> associations = new ArrayList<Association>(newObservation.getAssociations());
            Collections.sort(associations, new LinkComparator());
            for (Association association : associations) {
                listModel.addElement(association);
            }
        }

        /*
         * This change in jList will not be detected by the listSelectionListeners,
         * so make a manual call to enableButtons
         */
        observation = newObservation;
        enableButtons();
        setEditingAssociation(false);
        repaint();

        
    }

    /**
     *  Sets the visible attribute of the AssociationListEditorPanel object
     *
     * @param  b The new visible value
     */
    @Override
    public void setVisible(boolean b) {
        getButtonEdit().setEnabled(false);
        getButtonRemove().setEnabled(false);
        getButtonAdd().setEnabled(false);
        super.setVisible(b);

    }
}
