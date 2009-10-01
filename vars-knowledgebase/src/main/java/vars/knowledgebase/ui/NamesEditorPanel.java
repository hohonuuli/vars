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


package vars.knowledgebase.ui;

import foxtrot.Job;
import foxtrot.Worker;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.swing.ListListModel;
import org.mbari.swing.SpinningDialWaitIndicator;
import org.mbari.swing.WaitIndicator;
import org.mbari.util.Dispatcher;
import org.mbari.text.IgnoreCaseToStringComparator;
import org.mbari.vars.dao.DAOException;
import org.mbari.vars.knowledgebase.model.Concept;
import org.mbari.vars.knowledgebase.model.ConceptName;
import org.mbari.vars.knowledgebase.model.HistoryFactory;
import org.mbari.vars.knowledgebase.model.dao.ConceptDAO;
import org.mbari.vars.knowledgebase.model.dao.KnowledgeBaseCache;
import org.mbari.vars.knowledgebase.ui.dialogs.AddConceptNameDialog2;
import org.mbari.vars.knowledgebase.ui.actions.ApproveHistoryTask;
import org.mbari.vars.model.UserAccount;
import org.mbari.vars.util.AppFrameDispatcher;
import vars.IUserAccount;
import vars.knowledgebase.IConceptDelegate;
import vars.knowledgebase.IConceptName;
import vars.knowledgebase.IHistory;

//~--- classes ----------------------------------------------------------------

/**
 * <p><!-- Class description --></p>
 *
 * @version    $Id: NamesEditorPanel.java 295 2006-07-06 23:47:31Z hohonuuli $
 * @author     <a href="http://www.mbari.org">Monterey Bay Aquarium Research Institute</a>
 */
public class NamesEditorPanel extends EditorPanel implements ILockableEditor {

    /**
     *
     */
    private static final long serialVersionUID = -3451562418328420320L;
    private static final Logger log = LoggerFactory.getLogger(NamesEditorPanel.class);

    //~--- fields -------------------------------------------------------------

    /**
	 * @uml.property  name="rbItemListener"
	 */
    private ItemListener rbItemListener;
    /**
	 * @uml.property  name="viewPanel"
	 * @uml.associationEnd  
	 */
    private JPanel viewPanel = null;
    /**
	 * @uml.property  name="typeLabel"
	 * @uml.associationEnd  
	 */
    private JLabel typeLabel = null;
    /**
	 * @uml.property  name="synonymRb"
	 * @uml.associationEnd  
	 */
    private JRadioButton synonymRb = null;
    /**
	 * @uml.property  name="synonymLabel"
	 * @uml.associationEnd  
	 */
    private JLabel synonymLabel = null;
    /**
	 * @uml.property  name="stringComparator"
	 */
    private Comparator stringComparator = new IgnoreCaseToStringComparator();
    /**
	 * @uml.property  name="rbPanel"
	 * @uml.associationEnd  
	 */
    private JPanel rbPanel = null;
    /**
	 * @uml.property  name="primaryRb"
	 * @uml.associationEnd  
	 */
    private JRadioButton primaryRb = null;
    /**
	 * @uml.property  name="primaryLabel"
	 * @uml.associationEnd  
	 */
    private JLabel primaryLabel = null;
    /**
	 * @uml.property  name="namesPanel"
	 * @uml.associationEnd  
	 */
    private JPanel namesPanel = null;
    /**
	 * @uml.property  name="namesList"
	 * @uml.associationEnd  
	 */
    private JList namesList = null;
    /**
	 * @uml.property  name="nameLabel"
	 * @uml.associationEnd  
	 */
    private JLabel nameLabel = null;
    /**
	 * @uml.property  name="nameField"
	 * @uml.associationEnd  
	 */
    private JTextField nameField = null;
    /**
	 * @uml.property  name="commonRb"
	 * @uml.associationEnd  
	 */
    private JRadioButton commonRb = null;
    /**
	 * @uml.property  name="commonLabel"
	 * @uml.associationEnd  
	 */
    private JLabel commonLabel = null;
    /**
	 * @uml.property  name="buttonPanel"
	 * @uml.associationEnd  
	 */
    private EditorButtonPanel buttonPanel = null;
    /**
	 * @uml.property  name="authorLabel"
	 * @uml.associationEnd  
	 */
    private JLabel authorLabel = null;
    /**
	 * @uml.property  name="authorField"
	 * @uml.associationEnd  
	 */
    private JTextField authorField = null;
    /**
	 * @uml.property  name="scrollPane"
	 * @uml.associationEnd  
	 */
    private JScrollPane scrollPane = null;

    //~--- constructors -------------------------------------------------------

    /**
     * This is the default constructor
     */
    public NamesEditorPanel() {
        super();
        initialize();
        setLocked(isLocked());
    }

    //~--- get methods --------------------------------------------------------

    /**
	 * This method initializes authorField
	 * @return  javax.swing.JTextField
	 * @uml.property  name="authorField"
	 */
    private JTextField getAuthorField() {
        if (authorField == null) {
            authorField = new JTextField();
        }

        return authorField;
    }

    /**
	 * This method initializes buttonPanel
	 * @return  javax.swing.JPanel
	 * @uml.property  name="buttonPanel"
	 */
    private EditorButtonPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new EditorButtonPanel();
            
            JButton deleteButton = buttonPanel.getDeleteButton();
            deleteButton.addActionListener(new DeleteAction());
            deleteButton.setEnabled(false);
            
            JButton updateButton = buttonPanel.getUpdateButton();
            updateButton.setEnabled(false);
            updateButton.addActionListener(new UpdateAction());
            
            JButton newButton = buttonPanel.getNewButton();
            newButton.addActionListener(new NewAction());
        }

        return buttonPanel;
    }

    /**
	 * This method initializes commonRb
	 * @return  javax.swing.JRadioButton
	 * @uml.property  name="commonRb"
	 */
    private JRadioButton getCommonRb() {
        if (commonRb == null) {
            commonRb = new JRadioButton();
            commonRb.addItemListener(getRbItemListener());
        }

        return commonRb;
    }

    /**
     * This method initializes deleteButton
     *
     * @return javax.swing.JButton
     */
    private JButton getDeleteButton() {
        return getButtonPanel().getDeleteButton();
    }

    /**
	 * This method initializes nameField
	 * @return  javax.swing.JTextField
	 * @uml.property  name="nameField"
	 */
    private JTextField getNameField() {
        if (nameField == null) {
            nameField = new JTextField();
        }

        return nameField;
    }

    /**
	 * This method initializes namesList
	 * @return  javax.swing.JList
	 * @uml.property  name="namesList"
	 */
    private JList getNamesList() {
        if (namesList == null) {
            namesList = new JList();
            namesList.setPreferredSize(new java.awt.Dimension(0, 120));
            namesList.setSelectionMode(
                    javax.swing.ListSelectionModel.SINGLE_SELECTION);

            /*
             * We're using a custom model here because Swing's DefaultListModel
             * is a pain to work with.
             */
            namesList.setModel(new ListListModel(new ArrayList()));

            /*
             * When an item in the namesList is selected we need to update the
             * editor fields
             */
            namesList.addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    String selectedName = (String) getNamesList().getSelectedValue();
                    if (log.isDebugEnabled()) {
                        log.debug(
                                "Selected '" + selectedName +
                                "' in the name editor panel");
                    }

                    setSelectedConceptName(selectedName);
                }

            });

            /*
             * If no items are selected in the list then we turn off the update
             * and delete buttons
             */
            namesList.addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    boolean isItemSelected = (namesList.getSelectedIndex() >
                        -1);
                    if (!isItemSelected) {
                        getUpdateButton().setEnabled(false);
                        getDeleteButton().setEnabled(false);
                    }
                }

            });
        }

        return namesList;
    }

    /**
	 * This method initializes mainPanel
	 * @return  javax.swing.JPanel
	 * @uml.property  name="namesPanel"
	 */
    private JPanel getNamesPanel() {
        if (namesPanel == null) {
            namesPanel = new JPanel();
            namesPanel.setLayout(new BorderLayout());
            namesPanel.setBorder(
                    javax.swing.BorderFactory.createTitledBorder(null,
                    "Names",
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                            javax.swing.border.TitledBorder.DEFAULT_POSITION,
                                null, null));
            namesPanel.add(getViewPanel(), java.awt.BorderLayout.SOUTH);
            namesPanel.add(getScrollPane(), BorderLayout.CENTER);
        }

        return namesPanel;
    }

    /**
     * This method initializes addButton
     *
     * @return javax.swing.JButton
     */
    private JButton getNewButton() {
        return getButtonPanel().getNewButton();
    }

    /**
	 * This method initializes primaryRb
	 * @return  javax.swing.JRadioButton
	 * @uml.property  name="primaryRb"
	 */
    private JRadioButton getPrimaryRb() {
        if (primaryRb == null) {
            primaryRb = new JRadioButton();
            primaryRb.addItemListener(getRbItemListener());
        }

        return primaryRb;
    }

    /**
	 * The primary radiobutton toggles the state of other components. For example we don't want to allow people to delete the primary conceptname.
	 * @return
	 * @uml.property  name="rbItemListener"
	 */
    private ItemListener getRbItemListener() {
        if (rbItemListener == null) {
            rbItemListener = new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    if (!isLocked()) {
                        JRadioButton rb = getPrimaryRb();
                        boolean enable = !rb.isSelected();
                        boolean enableDelete = (enable &&
                            (getNamesList().getSelectedIndex() > -1));
                        getDeleteButton().setEnabled(enableDelete);
                        boolean enableUpdate = (getNamesList().getSelectedIndex() >
                            -1);
                        getUpdateButton().setEnabled(enableUpdate);
                        getCommonRb().setEnabled(enable);
                        getSynonymRb().setEnabled(enable);
                        rb.setEnabled(!enable);
                    }
                }
            };
        }

        return rbItemListener;
    }

    /**
	 * This method initializes rbPanel
	 * @return  javax.swing.JPanel
	 * @uml.property  name="rbPanel"
	 */
    private JPanel getRbPanel() {
        if (rbPanel == null) {
            primaryLabel = new JLabel();
            primaryLabel.setText("Primary");
            synonymLabel = new JLabel();
            synonymLabel.setText("Synonym");
            commonLabel = new JLabel();
            commonLabel.setText("Common");
            rbPanel = new JPanel();
            rbPanel.setLayout(new BoxLayout(getRbPanel(), BoxLayout.X_AXIS));
            rbPanel.add(primaryLabel, null);
            rbPanel.add(getPrimaryRb(), null);
            rbPanel.add(Box.createHorizontalStrut(10));
            rbPanel.add(commonLabel, null);
            rbPanel.add(getCommonRb(), null);
            rbPanel.add(Box.createHorizontalStrut(10));
            rbPanel.add(synonymLabel, null);
            rbPanel.add(getSynonymRb(), null);
            rbPanel.add(Box.createHorizontalStrut(10));
            rbPanel.add(Box.createHorizontalGlue());
        }

        return rbPanel;
    }

    /**
	 * This method initializes synonymRb
	 * @return  javax.swing.JRadioButton
	 * @uml.property  name="synonymRb"
	 */
    private JRadioButton getSynonymRb() {
        if (synonymRb == null) {
            synonymRb = new JRadioButton();
            synonymRb.addItemListener(getRbItemListener());
        }

        return synonymRb;
    }

    /**
     * This method initializes removeButton
     *
     * @return javax.swing.JButton
     */
    private JButton getUpdateButton() {
        return getButtonPanel().getUpdateButton();
    }

    /**
	 * This method initializes namePanel
	 * @return  javax.swing.JPanel
	 * @uml.property  name="viewPanel"
	 */
    private JPanel getViewPanel() {
        if (viewPanel == null) {
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 1;
            gridBagConstraints5.anchor = GridBagConstraints.WEST;
            gridBagConstraints5.insets = new java.awt.Insets(4, 0, 0, 20);
            gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.gridy = 2;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.insets = new java.awt.Insets(4, 10, 0, 20);
            gridBagConstraints4.anchor = GridBagConstraints.WEST;
            gridBagConstraints4.gridy = 2;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 1;
            gridBagConstraints3.insets = new java.awt.Insets(4, 0, 0, 20);
            gridBagConstraints3.anchor = GridBagConstraints.WEST;
            gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridwidth = 2;
            gridBagConstraints3.gridy = 1;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 1;
            gridBagConstraints2.anchor = GridBagConstraints.WEST;
            gridBagConstraints2.insets = new java.awt.Insets(4, 0, 0, 20);
            gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridwidth = 2;
            gridBagConstraints2.gridy = 0;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.insets = new java.awt.Insets(4, 10, 0, 20);
            gridBagConstraints1.anchor = GridBagConstraints.WEST;
            gridBagConstraints1.gridy = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(4, 10, 0, 20);
            gridBagConstraints.gridy = 0;
            typeLabel = new JLabel();
            typeLabel.setText("Type: ");
            authorLabel = new JLabel();
            authorLabel.setText("Author: ");
            nameLabel = new JLabel();
            nameLabel.setText("Name: ");
            viewPanel = new JPanel();
            viewPanel.setLayout(new GridBagLayout());
            viewPanel.setBorder(
                    javax.swing.BorderFactory.createTitledBorder(null, "",
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                        javax.swing.border.TitledBorder.DEFAULT_POSITION,
                            null, null));
            viewPanel.add(nameLabel, gridBagConstraints);
            viewPanel.add(authorLabel, gridBagConstraints1);
            viewPanel.add(getNameField(), gridBagConstraints2);
            viewPanel.add(getAuthorField(), gridBagConstraints3);
            viewPanel.add(typeLabel, gridBagConstraints4);
            viewPanel.add(getRbPanel(), gridBagConstraints5);
        }

        return viewPanel;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * This method initializes this
     *
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.add(getNamesPanel(), java.awt.BorderLayout.CENTER);
        this.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(getPrimaryRb());
        buttonGroup.add(getCommonRb());
        buttonGroup.add(getSynonymRb());
    }

    //~--- set methods --------------------------------------------------------

    /**
     * @param concept
     *            The concept to set.
     */
    @Override
    public void setConcept(Concept concept) {
        super.setConcept(concept);

        /*
         * Update the list of 'other' names
         */
        String[] names = getConcept().getConceptNamesAsStrings();
        Arrays.sort(names, stringComparator);
        ListListModel listModel = (ListListModel) getNamesList().getModel();

        /*
         * We remove the ListSelectionListener here. If we don't we get errors
         * when the list is cleared.
         */
        listModel.clear();
        listModel.addAll(Arrays.asList(names));
        getNamesList().setSelectedValue(
                getConcept().getPrimaryConceptNameAsString(), true);
    }

    /**
     * @param locked
     *            The locked to set.
     */
    @Override
    public void setLocked(boolean locked) {
        super.setLocked(locked);
        getNameField().setEnabled(!locked);
        getAuthorField().setEnabled(!locked);
        getNewButton().setEnabled(!locked);
        getUpdateButton().setEnabled(!locked);
        getDeleteButton().setEnabled(!locked);
        getPrimaryRb().setEnabled(!locked);
        getCommonRb().setEnabled(!locked);
        getSynonymRb().setEnabled(!locked);

        /*
         * Important to call this to keep the UI consistant
         */
        getRbItemListener().itemStateChanged(null);
    }

    /**
     * Actions that are taking when a name is selected in the namesList (JList)
     *
     *
     * @param name
     */
    private void setSelectedConceptName(String name) {
        String author = "";
        String type = null;
        if (name == null) {
            name = "";
            type = ConceptName.NAMETYPE_SYNONYM;
        } else {
            IConceptName conceptName = getConcept().getConceptName(name);
            author = conceptName.getAuthor();
            type = conceptName.getNameType();
        }

        getNameField().setText(name);
        getAuthorField().setText(author);

        if (type.equals(ConceptName.NAMETYPE_PRIMARY)) {
            getPrimaryRb().setSelected(true);
        } else if (type.equals(ConceptName.NAMETYPE_COMMON)) {
            getCommonRb().setSelected(true);
        } else {
            getSynonymRb().setSelected(true);
        }
        
        /*
         * We don't allow changing the authro here for primary names. If it's a 
         * primary name the update action will try 
         */
        getAuthorField().setEditable(!type.equals(ConceptName.NAMETYPE_PRIMARY));
    }

    //~--- inner classes ------------------------------------------------------

    private class DeleteAction extends ActionAdapter {

        /**
         *
         */
        private static final long serialVersionUID = -7373413523255594938L;

        public void doAction() {

            final String selectedName = (String) getNamesList().getSelectedValue();
            final Concept concept = getConcept();
            final String primaryName = concept.getPrimaryConceptNameAsString();
            final IConceptName conceptName = concept.getConceptName(selectedName);
            int value = JOptionPane.showConfirmDialog(NamesEditorPanel.this,
                "Do you want to mark '" + selectedName + "' for deletion?", "VARS - Confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);

            final IUserAccount userAccount = (IUserAccount) KnowledgebaseApp.DISPATCHER_USERACCOUNT.getValueObject();
            if (value == JOptionPane.YES_OPTION) {
                WaitIndicator waitIndicator = new WaitIndicator(NamesEditorPanel.this);
                final IHistory history = HistoryFactory.delete(userAccount, conceptName);
            	concept.addHistory(history);

                try {
                    ConceptDAO.getInstance().update(concept);
                } catch (DAOException e) {
                    AppFrameDispatcher.showErrorDialog("Failed to update database. Unable to mark '" +
                            selectedName + "' for deletion.");
                }

                if (userAccount != null && userAccount.isAdmin()) {
                    ApproveHistoryTask.approve(userAccount, history);
                }
                waitIndicator.dispose();
                ((KnowledgebaseApp) KnowledgebaseApp.DISPATCHER.getValueObject()).getKnowledgebaseFrame().refreshTreeAndOpenNode(primaryName);

            }
            
        }
    }


    /**
	 * This action sets up the AddConceptNameDialog. Most of the work is done in the dialog, not here.
	 */
    private class NewAction extends ActionAdapter {

        /**
         *
         */
        private static final long serialVersionUID = 6063954683256831686L;
        private AddConceptNameDialog2 dialog;

        public void doAction() {
            getDialog().setVisible(true);
        }

        /**
		 * @return  the dialog
		 * @uml.property  name="dialog"
		 */
        private AddConceptNameDialog2 getDialog() {
            if (dialog == null) {
                dialog = new AddConceptNameDialog2(AppFrameDispatcher.getFrame(), true);

                /*
                 * Set the currently selected concept
                 */
                Dispatcher dispatcher = KnowledgebaseApp.DISPATCHER_SELECTED_CONCEPT;
                dialog.setConcept((Concept) dispatcher.getValueObject());

                /*
                 * The dialog needs a reference to the currently selected
                 * concept. We do that by listening to the appropriate
                 * dispatcher.
                 */
                dispatcher.addPropertyChangeListener(
                        new PropertyChangeListener() {

                    public void propertyChange(PropertyChangeEvent evt) {
                        dialog.setConcept((Concept) evt.getNewValue());
                    }
                });
            }

            return dialog;
        }
    }


    /**
     * Handles updates to conceptnames.
     *
     * @author brian
     */
    private class UpdateAction extends ActionAdapter {

        /**
         *
         */
        private static final long serialVersionUID = 7974719628218732271L;

        /**
         * Constructs ...
         *
         */
        UpdateAction() {
            putValue(NAME, "Update");
        }

        /**
         * <p><!-- Method description --></p>
         *
         */
        public void doAction() {
            /*
             * Get the name that's selected in the UI for the current concept.
             */
            final String selectedName = (String) getNamesList().getSelectedValue();
            final Concept concept = getConcept();
            IConceptName oldConceptName = concept.getConceptName(selectedName);
            boolean okToProceed = true;

            /*
             * Retrieve the parameters from the interface
             */
            final String name = getNameField().getText();
            int value = JOptionPane.showConfirmDialog(NamesEditorPanel.this,
                "Do want to change '" + selectedName + "' to '" + name + "'?",
                "VARS - Confirm", JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE);
            final String author = getAuthorField().getText();
            String nameType = ConceptName.NAMETYPE_SYNONYM;
            if (getPrimaryRb().isSelected()) {
                nameType = ConceptName.NAMETYPE_PRIMARY;
            } else if (getCommonRb().isSelected()) {
                nameType = ConceptName.NAMETYPE_COMMON;
            }
            

            /*
             * Exit if the values are invalid
             */
            okToProceed = (value == JOptionPane.YES_OPTION) &&
                    !name.equals(selectedName) && (name.equals("")) &&
                        (selectedName != null) && (oldConceptName != null);
            
            
            final UserAccount userAccount = (UserAccount) KnowledgebaseApp.DISPATCHER_USERACCOUNT.getValueObject();

            /*
             * Warn users if they are trying to change the primary name.
             */
            if (okToProceed && nameType.equals(ConceptName.NAMETYPE_PRIMARY)) {
                value = JOptionPane.showConfirmDialog(NamesEditorPanel.this,
                        "Are you really sure that you want\n" + "to change the primary name?", "VARS - Confirm",
                         JOptionPane.YES_NO_OPTION,
                         JOptionPane.WARNING_MESSAGE);
                okToProceed = (value == JOptionPane.YES_OPTION);
            }

            /*
             * Check that the name does not already exist in the database
             */
            Concept matchingConcept = null;
            if (okToProceed) {
                try {
                    matchingConcept = KnowledgeBaseCache.getInstance().findConceptByName(name);
                } catch (DAOException e1) {
                    if (log.isErrorEnabled()) {
                        log.error( "A search for '" + name + "' in the database failed", e1);
                    }
                    AppFrameDispatcher.showErrorDialog( "Failed to connect to the database");
                    okToProceed = false;
                }
            }

            if (okToProceed) {
                if ((matchingConcept != null) && (matchingConcept.getId() != concept.getId())) {
                    AppFrameDispatcher.showWarningDialog( "A concept with " + "the name '" + name +
                            "' already exists.");
                    okToProceed = false;
                }
            }

            if (okToProceed) {

                /*
                 * We really want to block the entire application with a wait indicator. But we have no guarentee
                 * that this panel is contained in a Swing frame registered with the AppFrameDisplatcher. So
                 * we COA here.
                 */
                final WaitIndicator waitIndicator = new SpinningDialWaitIndicator(NamesEditorPanel.this);
                
                if (concept.isConceptDelegateLoaded()) {
                    log.debug("ConceptDelegate is not yet loaded");
                }
                
                IConceptDelegate conceptDelegate = concept.getConceptDelegate();
                log.debug("Inspecting " + conceptDelegate + "\n" +
                        conceptDelegate.getHistorySet().toString() + "\n");

                /*
                 * Make the changes and update the database
                 */
                boolean success = false;
                ConceptName newConceptName = new ConceptName();
                newConceptName.setName(name);
                newConceptName.setAuthor(author);
                newConceptName.setNameType(nameType);

                /*
                 * Add a History object to track the change.
                 */
                
                IHistory history = HistoryFactory.replaceConceptName(userAccount, oldConceptName, newConceptName);
                concept.addHistory(history);

                /*
                 * When updating a primary name we want to keep the older
                 * name, so we add a new Concept with the old values.
                 */
                if (nameType.equals(ConceptName.NAMETYPE_PRIMARY)) {
                    ConceptName copyCn = new ConceptName();
                    copyCn.setName(oldConceptName.getName());
                    copyCn.setAuthor(oldConceptName.getAuthor());
                    copyCn.setNameType(ConceptName.NAMETYPE_SYNONYM);

                    /*
                     * Have to update the original concept before adding the
                     * copy. Otherwise they will have the same names and the
                     * concept won't allow duplicate names to be added.
                     */
                    oldConceptName.setName(name);
                    concept.addConceptName(copyCn);
                } else {
                    oldConceptName.setName(name);
                }

                oldConceptName.setAuthor(author);
                oldConceptName.setNameType(nameType);
                Boolean ok = (Boolean) Worker.post(new Job() {

                    public Object run() {
                        Boolean ok = Boolean.FALSE;
                        try {
                            ConceptDAO.getInstance().update(concept);
                            ok = Boolean.TRUE;
                        } catch (DAOException e) {
                            if (log.isErrorEnabled()) {
                                log.error("Failed to update " + concept, e);
                            }

                            AppFrameDispatcher.showErrorDialog("Update failed!! (Database error)");
                        }

                        return ok;
                    }

                });
                success = ok.booleanValue();


                if (success) {

                    /*
                     * Update the annotations that might use the name. Ideally, the database would only contain
                     * primary names. But just in case we'll update common names and synonyms.
                     */

                    if (log.isDebugEnabled()) {
                        log.debug( "Changing all Observations that use '" + selectedName + "' to use '" + name + "'");
                    }

                    success = (Boolean) Worker.post(new Job() {
                        public Object run() {
                            Boolean ok = Boolean.FALSE;
                            try {
                                ConceptDAO.getInstance().updateConceptNameUsedByAnnotations(concept);
                                ok = Boolean.TRUE;
                            } catch (DAOException e) {
                                String msg = "Failed to change primary names of annotations from '" +
                                    selectedName + "' to '" + name + "'.";
                                log.error(msg);
                                AppFrameDispatcher.showErrorDialog(msg);
                            }
                            return ok;
                        }
                    });
                    
                    
                    /*
                     * If the annotation update was successful we can drop the old conceptname that we
                     * might have created if changing a primary name
                     */
                    if (success) {
                        IConceptName oldPrimaryName = concept.getConceptName(history.getOldValue());
                        if (oldPrimaryName != null && !oldPrimaryName.getNameType().equalsIgnoreCase(ConceptName.NAMETYPE_PRIMARY)) {
                            concept.removeConceptName(oldPrimaryName);
                            try {
                                ConceptDAO.getInstance().update(concept);
                            }
                            catch (DAOException ex) {
                                log.error("Failed to remove " + oldPrimaryName + " from the database. This will need to be done manually!!");
                            }
                        }
                    }


                    /*
                     * If the user is an admin go ahead and approve the change. Do this BEFORE you refresh the tree
                     * or your database transaction will fail because of a timestamp mismatch. (ie. Cache does not
                     * match you instance)
                     */

                    if (userAccount != null && userAccount.isAdmin()) {
                        ApproveHistoryTask.approve(userAccount, history);
                    }

                    /*
                     * Clear the knowledgebasecache since it's no longer
                     * accurate and reopen the tree to the currently edited
                     * node.
                     */
                    final Frame frame = AppFrameDispatcher.getFrame();
                    if ((frame != null) && (frame instanceof KnowledgebaseFrame)) {
                        Worker.post(new Job() {
                            public Object run() {
                                ((KnowledgebaseFrame) frame).refreshTreeAndOpenNode(concept.getPrimaryConceptNameAsString());
                                return null;
                            }
                        });
                    }

                }
                waitIndicator.dispose();
            }
        }
    }


    /**
	 * This method initializes scrollPane	
	 * @return  javax.swing.JScrollPane
	 * @uml.property  name="scrollPane"
	 */
    private JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane();
            scrollPane.setViewportView(getNamesList());
        }
        return scrollPane;
    }
}
