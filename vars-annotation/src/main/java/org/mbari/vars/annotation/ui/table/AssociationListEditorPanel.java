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


package org.mbari.vars.annotation.ui.table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.ImageObserver;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.ToolTipManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.awt.layout.VerticalFlowLayout;
import org.mbari.swing.JFancyButton;
import org.mbari.swing.ListListModel;
import org.mbari.vars.annotation.model.Association;
import vars.annotation.AssociationList;
import vars.annotation.ISimpleConcept;
import org.mbari.vars.annotation.model.Observation;
import org.mbari.vars.annotation.model.dao.AssociationDAO;
import org.mbari.vars.dao.DAOEventQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * @author  <a href="mailto:brian@mbari.org">Brian Schlining </a>
 * @version  $Id: AssociationListEditorPanel.java,v 1.12 2004/02/27 19:25:56
 *          brian Exp $
 * @stereotype  thing
 */
public class AssociationListEditorPanel extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = 4495657018990703758L;
    private static final Logger log = LoggerFactory.getLogger(AssociationListEditorPanel.class);

    /**
     *     Description of the Field
     *     @uml.property  name="hEIGHT"
     */
    public final int HEIGHT = ImageObserver.HEIGHT;

    /**
     *     Description of the Field
     *     @uml.property  name="wIDTH"
     */
    public final int WIDTH = ImageObserver.WIDTH;

    /**
     *     @uml.property  name="jList"
     *     @uml.associationEnd
     */
    JList jList;

    /**
     *     @uml.property  name="jScrollPane"
     *     @uml.associationEnd
     */
    JScrollPane jScrollPane;

    /**
     *     @uml.property  name="associationEditorPanel"
     *     @uml.associationEnd
     */
    protected AssociationEditorPanel associationEditorPanel = null;

    /**
     *     @uml.property  name="verticalFlowLayout1"
     *     @uml.associationEnd  multiplicity="(1 1)"
     */
    VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();

    /**
     *     @uml.property  name="listEditorPanel"
     *     @uml.associationEnd  multiplicity="(1 1)"
     */
    JPanel listEditorPanel = new JPanel();

    /**
     *     @uml.property  name="addAction"
     *     @uml.associationEnd
     */
    private ActionAdapter addAction;

    /**
     *     @uml.property  name="buttonAdd"
     *     @uml.associationEnd
     */
    private JButton buttonAdd = null;

    /**
     *     @uml.property  name="buttonEdit"
     *     @uml.associationEnd
     */
    private JButton buttonEdit = null;

    /**
     *     @uml.property  name="buttonPanel"
     *     @uml.associationEnd
     */
    private JPanel buttonPanel;

    /**
     *     @uml.property  name="buttonRemove"
     *     @uml.associationEnd
     */
    private JButton buttonRemove = null;

    /**
     *     @uml.property  name="changeListener"
     */
    private PropertyChangeListener changeListener;

    /**
     *     @uml.property  name="editAction"
     *     @uml.associationEnd
     */
    private ActionAdapter editAction;

    /**
     *     @uml.property  name="editingAssociation"
     */
    private boolean editingAssociation;

    /**
     *     @uml.property  name="observation"
     *     @uml.associationEnd
     */
    private Observation observation;

    /**
     *     @uml.property  name="removeAction"
     *     @uml.associationEnd
     */
    private ActionAdapter removeAction;

    /**
     * Constructor for the AssociationListEditorPanel object
     */
    public AssociationListEditorPanel() {
        initialize();
    }

    // Enable the buttons if it is appropriate to do so, based on the state of
    // the jList

    /**
     * <p><!-- Method description --></p>
     *
     */
    private void enableButtons() {
        getButtonAdd().setEnabled(true);

        if ((getJList().getSelectedIndex() == -1) || (jList.getModel().getSize() == 0)) {
            getButtonRemove().setEnabled(false);
            getButtonEdit().setEnabled(false);
        }
        else {
            getButtonRemove().setEnabled(true);
            getButtonEdit().setEnabled(true);
        }
    }

    /**
     *     <p><!-- Method description --></p>
     *     @return
     *     @uml.property  name="addAction"
     */
    ActionAdapter getAddAction() {
        if (addAction == null) {
            addAction = new ActionAdapter() {

                /**
                 *
                 */
                private static final long serialVersionUID = 4167992105629488599L;

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
     *     @uml.property  name="associationEditorPanel"
     */
    public AssociationEditorPanel getAssociationEditorPanel() {
        if (associationEditorPanel == null) {
            associationEditorPanel = new AssociationEditorPanel();
            associationEditorPanel.validate();
            associationEditorPanel.addActionListener(new ActionListener() {

                public void actionPerformed(final ActionEvent e) {
                    setEditingAssociation(false);
                }
            });
        }

        return associationEditorPanel;
    }

    /**
     *     <p><!-- Method description --></p>
     *     @return
     *     @uml.property  name="buttonAdd"
     */
    private JButton getButtonAdd() {
        if (buttonAdd == null) {
            buttonAdd = new JFancyButton();
            buttonAdd.setOpaque(false);
            buttonAdd.setPreferredSize(new Dimension(32, 32));
            buttonAdd.setToolTipText("Add Association");
            buttonAdd.setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/add.png")));
            buttonAdd.addActionListener(getAddAction());
        }

        return buttonAdd;
    }

    /**
     *     <p><!-- Method description --></p>
     *     @return
     *     @uml.property  name="buttonEdit"
     */
    private JButton getButtonEdit() {
        if (buttonEdit == null) {
            buttonEdit = new JFancyButton();
            buttonEdit.addActionListener(getEditAction());
            buttonEdit.setOpaque(false);
            buttonEdit.setPreferredSize(new Dimension(32, 32));
            buttonEdit.setToolTipText("Edit Association");
            buttonEdit.setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/edit.png")));
            buttonEdit.setEnabled(false);
        }

        return buttonEdit;
    }

    /**
     *     <p><!-- Method description --></p>
     *     @return
     *     @uml.property  name="buttonPanel"
     */
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

    /**
     *     <p><!-- Method description --></p>
     *     @return
     *     @uml.property  name="buttonRemove"
     */
    private JButton getButtonRemove() {
        if (buttonRemove == null) {
            buttonRemove = new JFancyButton();
            buttonRemove.setOpaque(false);
            buttonRemove.setBackground(Color.red);
            buttonRemove.setPreferredSize(new Dimension(32, 32));
            buttonRemove.setToolTipText("Remove Association");
            buttonRemove.setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/remove.png")));
            buttonRemove.addActionListener(getRemoveAction());
        }

        return buttonRemove;
    }

    /**
     *     This change listener can be added to any object whose state change requires the JList to repaint.
     *     @return
     *     @uml.property  name="changeListener"
     */
    PropertyChangeListener getChangeListener() {
        if (changeListener == null) {
            changeListener = new PropertyChangeListener() {

                public void propertyChange(final PropertyChangeEvent evt) {
                    final ListListModel listModel = (ListListModel) getJList().getModel();
                    listModel.refreshView();
                }
            };
        }

        return changeListener;
    }

    /**
     *     <p><!-- Method description --></p>
     *     @return
     *     @uml.property  name="editAction"
     */
    ActionAdapter getEditAction() {
        if (editAction == null) {
            editAction = new ActionAdapter() {

                /**
                 *
                 */
                private static final long serialVersionUID = 4634422451695299506L;

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

    /**
     *     <p><!-- Method description --></p>
     *     @return
     *     @uml.property  name="jList"
     */
    private JList getJList() {
        if (jList == null) {
            jList = new JList();
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

    /**
     *     <p><!-- Method description --></p>
     *     @return
     *     @uml.property  name="jScrollPane"
     */
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
     *     Retrieve the observation whose assocations are being edited.
     *     @return
     *     @uml.property  name="observation"
     */
    public Observation getObservation() {
        return observation;
    }

    /**
     *     The removeAction removes the association selected in the list from the database, the view, and the datamodel.
     *     @return
     *     @uml.property  name="removeAction"
     */
    ActionAdapter getRemoveAction() {
        if (removeAction == null) {
            removeAction = new ActionAdapter() {

                /**
                 *
                 */
                private static final long serialVersionUID = 5987519694176839512L;

                public void doAction() {

                    // if any item is selected, remove it
                    final JList j = getJList();
                    if (j.getSelectedIndex() > -1) {

                        // Get the association object from the listModel
                        final ListListModel listModel = (ListListModel) j.getModel();
                        final Association association = (Association) j.getSelectedValue();

                        // If for some reason a null value gets in the list
                        // just remove it.
                        if (association == null) {
                            listModel.remove(j.getSelectedIndex());

                            return;
                        }

                        // Get the parent of the selected association

                        final ISimpleConcept parent = association.getParent();

                        // Remove references to the selected association from
                        // the parent
                        // and the listModel
                        listModel.remove(j.getSelectedIndex());

                        // Delete the new Association into the database.
                        DAOEventQueue.flush();

                        try {
                            AssociationDAO.getInstance().delete(association);
                            parent.removeAssociation(association);
                        }
                        catch (final Exception ex) {
                            if (log.isErrorEnabled()) {
                                log.error("Unable to delete an Association (" + association +
                                          ") record from the VARS database.", ex);
                            }
                        }

                    }
                }
            };
        }

        return removeAction;
    }

    /**
     * <p><!-- Method description --></p>
     *
     */
    void initialize() {
        listEditorPanel.setOpaque(false);
        layoutPanel();
        this.setFocusable(false);
        addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(final FocusEvent e) {

                // System.out.println("focus gained");
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
     * This method is requried for focus management. All components are remove
     * from the vewi at certain times so taht they can't be focused on. but they
     * bet added back later
     *
     */
    private void layoutPanel() {

        /*
         *  String rows = "pref:grow, 2dlu, pref";
         *  String columns = "pref:grow, pref, pref, pref";
         *  FormLayout layout = new FormLayout(columns, rows);
         *  setLayout(layout);
         *  CellConstraints cc = new CellConstraints();
         *  getJScrollPane().setPreferredSize(new Dimension(5, 5));
         *  getJList().setPreferredSize(new Dimension(5, 5));
         *  add(getJScrollPane(), cc.xywh(1, 1, 4, 1, "fill, fill"));
         *  getButtonAdd().setVisible(true);
         *  add(getButtonAdd(), cc.xy(2, 3));
         *  add(getButtonEdit(), cc.xy(3, 3));
         *  add(getButtonRemove(), cc.xy(4, 3));
         */
        setLayout(new BorderLayout());
        add(getJScrollPane(), BorderLayout.CENTER);
        add(getButtonPanel(), BorderLayout.SOUTH);
    }

    /**
     * Sets the <code>AssociationList</code> object that is to be edited. This
     * method wraps the <code>AssociationList</code> in a
     * <code>ListListModel</code> object that can be used by
     * <code>JList</code>.
     *
     *
     * @param  value             The <code>AssociationList</code> to be edited
     */
    void setAssociationList(final AssociationList value) {
        final ListModel listModel = new ListListModel(value);
        getJList().setModel(listModel);

        // this change in jList will not be deteced by the
        // listSelectionListeners,
        // so make a manual call to enableButtons
        enableButtons();
        setEditingAssociation(false);
    }

    /**
     *     Toggles which panel is visible. If true, the AssociationEditorPanel is visible. If false, the AssociationListEditorPanel is visible.
     *     @param isEditingItem  true if editing a single association.
     *     @uml.property  name="editingAssociation"
     */
    public void setEditingAssociation(final boolean isEditingItem) {
        this.editingAssociation = isEditingItem;

        if (isEditingItem) {

            // Remove the ListEditorPanel from the view
            listEditorPanel.setVisible(false);
            this.removeAll();

            /*
             *  CellConstraints cc = new CellConstraints();
             *  this.add(associationEditorPanel, cc.xywh(1, 1, 4, 2, "fill, fill"));
             */
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
     *     @param  newObs
     *     @uml.property  name="observation"
     */
    public void setObservation(final Observation newObs) {

        // Remvoe listeners from existing
        if (observation != null) {
            observation.getAssociationList().removePropertyChangeListener(getChangeListener());
            observation.removePropertyChangeListener("conceptName", getChangeListener());
        }

        if (newObs != null) {
            final AssociationList a = newObs.getAssociationList();
            a.addPropertyChangeListener(getChangeListener());
            newObs.addPropertyChangeListener("conceptName", getChangeListener());
            setAssociationList(a);
        }
        else {

            // TODO 20040430 brian: implement handiling if observation is null
        }

        observation = newObs;
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
        super.setVisible(b);

        if (!b) {

            // Resize the row so that it fits the association list
            //final ObservationTable table = ObservationTableDispatcher.getInstance().getObservationTable();
            //final int selectedRow = table.getSelectedRow();
            // table.packRows(selectedRow, selectedRow, 0);
        }
    }

    /*
     *  # AssociationListCellRenderer lnkAssociationListCellRenderer;
     */
}
