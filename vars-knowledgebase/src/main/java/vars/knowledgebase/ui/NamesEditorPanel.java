/*
 * @(#)NamesEditorPanel.java   2009.10.09 at 04:58:46 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.knowledgebase.ui;

import com.google.inject.Inject;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
import org.bushe.swing.event.EventBus;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.swing.ListListModel;
import org.mbari.swing.SpinningDialWaitIndicator;
import org.mbari.swing.WaitIndicator;
import org.mbari.text.IgnoreCaseToStringComparator;
import org.mbari.util.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.UserAccount;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptMetadata;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameTypes;
import vars.knowledgebase.History;
import vars.knowledgebase.HistoryFactory;
import vars.knowledgebase.KnowledgebaseDAO;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.ui.actions.ApproveHistoryTask;
import vars.knowledgebase.ui.dialogs.AddConceptNameDialog2;

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
    private JTextField authorField = null;
    private JLabel authorLabel = null;
    private EditorButtonPanel buttonPanel = null;
    private JLabel commonLabel = null;
    private JRadioButton commonRb = null;
    private JTextField nameField = null;
    private JLabel nameLabel = null;
    private JList namesList = null;
    private JPanel namesPanel = null;
    private JLabel primaryLabel = null;
    private JRadioButton primaryRb = null;
    private JPanel rbPanel = null;
    private JScrollPane scrollPane = null;
    private JLabel synonymLabel = null;
    private JRadioButton synonymRb = null;
    private JLabel typeLabel = null;
    private JPanel viewPanel = null;
    private Comparator stringComparator = new IgnoreCaseToStringComparator();
    private final ApproveHistoryTask approveHistoryTask;
    private final HistoryFactory historyFactory;
    private final KnowledgebaseDAO knowledgebaseDAO;
    private final KnowledgebaseDAOFactory knowledgebaseDAOFactory;
    private final KnowledgebaseFactory knowledgebaseFactory;
    private ItemListener rbItemListener;

    /**
     * This is the default constructor
     *
     * @param approveHistoryTask
     * @param knowledgebaseDAO
     * @param knowledgebaseDAOFactory
     * @param knowledgebaseFactory
     */
    @Inject
    public NamesEditorPanel(ToolBelt toolBelt) {
        super();
        this.approveHistoryTask = toolBelt.getApproveHistoryTask();
        this.knowledgebaseDAO = toolBelt.getKnowledgebaseDAO();
        this.knowledgebaseDAOFactory = toolBelt.getKnowledgebaseDAOFactory();
        this.knowledgebaseFactory = toolBelt.getKnowledgebaseFactory();
        this.historyFactory = new HistoryFactory(knowledgebaseFactory);
        initialize();
        setLocked(isLocked());
    }

    /**
     * This method initializes authorField
     * @return  javax.swing.JTextField
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
     */
    private JTextField getNameField() {
        if (nameField == null) {
            nameField = new JTextField();
        }

        return nameField;
    }

    private JList getNamesList() {
        if (namesList == null) {
            namesList = new JList();
            namesList.setPreferredSize(new java.awt.Dimension(0, 120));
            namesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

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
                        log.debug("Selected '" + selectedName + "' in the name editor panel");
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
                    boolean isItemSelected = (namesList.getSelectedIndex() > -1);
                    if (!isItemSelected) {
                        getUpdateButton().setEnabled(false);
                        getDeleteButton().setEnabled(false);
                    }
                }

            });
        }

        return namesList;
    }


    private JPanel getNamesPanel() {
        if (namesPanel == null) {
            namesPanel = new JPanel();
            namesPanel.setLayout(new BorderLayout());
            namesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Names",
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            namesPanel.add(getViewPanel(), java.awt.BorderLayout.SOUTH);
            namesPanel.add(getScrollPane(), BorderLayout.CENTER);
        }

        return namesPanel;
    }

 
    private JButton getNewButton() {
        return getButtonPanel().getNewButton();
    }

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
     */
    private ItemListener getRbItemListener() {
        if (rbItemListener == null) {
            rbItemListener = new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    if (!isLocked()) {
                        JRadioButton rb = getPrimaryRb();
                        boolean enable = !rb.isSelected();
                        boolean enableDelete = (enable && (getNamesList().getSelectedIndex() > -1));
                        getDeleteButton().setEnabled(enableDelete);
                        boolean enableUpdate = (getNamesList().getSelectedIndex() > -1);
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

    private JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane();
            scrollPane.setViewportView(getNamesList());
        }

        return scrollPane;
    }

    private JRadioButton getSynonymRb() {
        if (synonymRb == null) {
            synonymRb = new JRadioButton();
            synonymRb.addItemListener(getRbItemListener());
        }

        return synonymRb;
    }

    private JButton getUpdateButton() {
        return getButtonPanel().getUpdateButton();
    }

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
            viewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "",
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            viewPanel.add(nameLabel, gridBagConstraints);
            viewPanel.add(authorLabel, gridBagConstraints1);
            viewPanel.add(getNameField(), gridBagConstraints2);
            viewPanel.add(getAuthorField(), gridBagConstraints3);
            viewPanel.add(typeLabel, gridBagConstraints4);
            viewPanel.add(getRbPanel(), gridBagConstraints5);
        }

        return viewPanel;
    }

    private void initialize() {
        this.setLayout(new BorderLayout());
        this.add(getNamesPanel(), java.awt.BorderLayout.CENTER);
        this.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(getPrimaryRb());
        buttonGroup.add(getCommonRb());
        buttonGroup.add(getSynonymRb());
    }

    /**
     * @param concept The concept to set.
     */
    @Override
    public void setConcept(Concept concept) {
        super.setConcept(concept);


        /*
         * Update the list of 'other' names
         */
        List<ConceptName> conceptNames = new ArrayList<ConceptName>(concept.getConceptNames());
        List<String> names = new ArrayList<String>(conceptNames.size());
        Collections.sort(names, stringComparator);


        /*
         * We remove the ListSelectionListener here. If we don't we get errors
         * when the list is cleared.
         */
        ListListModel listModel = (ListListModel) getNamesList().getModel();
        listModel.clear();
        listModel.addAll(names);
        getNamesList().setSelectedValue(concept.getPrimaryConceptName().getName(), true);
    }

    /**
     * @param locked The locked to set.
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
     * @param name
     */
    private void setSelectedConceptName(String name) {
        String author = "";
        String type = null;
        if (name == null) {
            name = "";
            type = ConceptNameTypes.SYNONYM.toString();
        }
        else {
            ConceptName conceptName = getConcept().getConceptName(name);
            author = conceptName.getAuthor();
            type = conceptName.getNameType();
        }

        getNameField().setText(name);
        getAuthorField().setText(author);

        if (type.equals(ConceptNameTypes.PRIMARY.toString())) {
            getPrimaryRb().setSelected(true);
        }
        else if (type.equals(ConceptNameTypes.COMMON.toString())) {
            getCommonRb().setSelected(true);
        }
        else {
            getSynonymRb().setSelected(true);
        }

        /*
         * We don't allow changing the authro here for primary names. If it's a
         * primary name the update action will try
         */
        getAuthorField().setEditable(!type.equals(ConceptNameTypes.PRIMARY.toString()));
    }

    private class DeleteAction extends ActionAdapter {

        public void doAction() {

            final String selectedName = (String) getNamesList().getSelectedValue();
            final Concept concept = getConcept();
            final String primaryName = concept.getPrimaryConceptName().getName();
            final ConceptName conceptName = concept.getConceptName(selectedName);
            int value = JOptionPane.showConfirmDialog(NamesEditorPanel.this,
                "Do you want to mark '" + selectedName + "' for deletion?", "VARS - Confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);

            final UserAccount userAccount = (UserAccount) KnowledgebaseApp.DISPATCHER_USERACCOUNT.getValueObject();
            if (value == JOptionPane.YES_OPTION) {
                WaitIndicator waitIndicator = new WaitIndicator(NamesEditorPanel.this);
                final History history = historyFactory.delete(userAccount, conceptName);
                concept.getConceptMetadata().addHistory(history);

                try {
                    knowledgebaseDAOFactory.newHistoryDAO().makePersistent(history);
                }
                catch (Exception e) {
                    EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);


                }

                if ((userAccount != null) && userAccount.isAdministrator()) {
                    approveHistoryTask.approve(userAccount, history);
                }

                waitIndicator.dispose();
                EventBus.publish(Lookup.TOPIC_REFRESH_KNOWLEGEBASE, primaryName);
            }

        }
    }


    /**
         * This action sets up the AddConceptNameDialog. Most of the work is done in the dialog, not here.
         */
    private class NewAction extends ActionAdapter {

 
        private AddConceptNameDialog2 dialog;

        public void doAction() {
            getDialog().setVisible(true);
        }

        private AddConceptNameDialog2 getDialog() {
            if (dialog == null) {
                final Frame frame = (Frame) Lookup.getApplicationFrameDispatcher().getValueObject();
                dialog = new AddConceptNameDialog2(frame, true, approveHistoryTask, knowledgebaseDAOFactory,
                                                   knowledgebaseFactory);

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
                dispatcher.addPropertyChangeListener(new PropertyChangeListener() {

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
 
        UpdateAction() {
            putValue(NAME, "Update");
        }

        /**
         * <p><!-- Method description --></p>
         *
         */
        public void doAction() {

            final ConceptDAO conceptDAO = knowledgebaseDAOFactory.newConceptDAO();

            /*
             * Get the name that's selected in the UI for the current concept.
             */
            final String selectedName = (String) getNamesList().getSelectedValue();
            final Concept concept = getConcept();
            ConceptName oldConceptName = concept.getConceptName(selectedName);
            boolean okToProceed = true;

            /*
             * Retrieve the parameters from the interface
             */
            final String name = getNameField().getText();
            int value = JOptionPane.showConfirmDialog(NamesEditorPanel.this,
                "Do want to change '" + selectedName + "' to '" + name + "'?", "VARS - Confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
            final String author = getAuthorField().getText();
            String nameType = ConceptNameTypes.SYNONYM.toString();
            if (getPrimaryRb().isSelected()) {
                nameType = ConceptNameTypes.PRIMARY.toString();
            }
            else if (getCommonRb().isSelected()) {
                nameType = ConceptNameTypes.COMMON.toString();
            }


            /*
             * Exit if the values are invalid
             */
            okToProceed = (value == JOptionPane.YES_OPTION) && !name.equals(selectedName) && (name.equals("")) &&
                          (selectedName != null) && (oldConceptName != null);


            final UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();

            /*
             * Warn users if they are trying to change the primary name.
             */
            if (okToProceed && nameType.equals(ConceptNameTypes.PRIMARY.toString())) {
                value = JOptionPane.showConfirmDialog(NamesEditorPanel.this,
                        "Are you really sure that you want\n" + "to change the primary name?", "VARS - Confirm",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                okToProceed = (value == JOptionPane.YES_OPTION);
            }

            /*
             * Check that the name does not already exist in the database
             */
            Concept matchingConcept = null;
            if (okToProceed) {
                try {
                    matchingConcept = conceptDAO.findByName(name);
                }
                catch (Exception e1) {
                    if (log.isErrorEnabled()) {
                        log.error("A search for '" + name + "' in the database failed", e1);
                    }

                    EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e1);
                    okToProceed = false;
                }
            }

            if (okToProceed) {
                if ((matchingConcept != null) && !(matchingConcept.equals(concept))) {
                    EventBus.publish(Lookup.TOPIC_WARNING,
                                     "A concept with " + "the name '" + name + "' already exists.");
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

                ConceptMetadata conceptDelegate = concept.getConceptMetadata();
                log.debug("Inspecting " + conceptDelegate + "\n" + conceptDelegate.getHistories().toString() + "\n");

                /*
                 * Make the changes and update the database
                 */
                boolean success = false;
                ConceptName newConceptName = knowledgebaseFactory.newConceptName();
                newConceptName.setName(name);
                newConceptName.setAuthor(author);
                newConceptName.setNameType(nameType);

                /*
                 * Add a History object to track the change.
                 */

                History history = historyFactory.replaceConceptName(userAccount, oldConceptName, newConceptName);
                concept.getConceptMetadata().addHistory(history);
                knowledgebaseDAOFactory.newHistoryDAO().makePersistent(history);

                /*
                 * When updating a primary name we want to keep the older
                 * name, so we add a new Concept with the old values.
                 */
                if (nameType.equals(ConceptNameTypes.PRIMARY.toString())) {
                    ConceptName copyCn = knowledgebaseFactory.newConceptName();
                    copyCn.setName(oldConceptName.getName());
                    copyCn.setAuthor(oldConceptName.getAuthor());
                    copyCn.setNameType(ConceptNameTypes.SYNONYM.toString());

                    /*
                     * Have to update the original concept before adding the
                     * copy. Otherwise they will have the same names and the
                     * concept won't allow duplicate names to be added.
                     */
                    oldConceptName.setName(name);
                    concept.addConceptName(copyCn);
                }
                else {
                    oldConceptName.setName(name);
                }

                oldConceptName.setAuthor(author);
                oldConceptName.setNameType(nameType);
                Boolean ok = (Boolean) Worker.post(new Job() {

                    public Object run() {
                        Boolean ok = Boolean.FALSE;
                        try {
                            conceptDAO.update(concept);
                            ok = Boolean.TRUE;
                        }
                        catch (Exception e) {
                            if (log.isErrorEnabled()) {
                                log.error("Failed to update " + concept, e);
                            }

                            EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, "Update failed!! (Database error)");
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
                        log.debug("Changing all Observations that use '" + selectedName + "' to use '" + name + "'");
                    }

                    success = (Boolean) Worker.post(new Job() {

                        public Object run() {
                            Boolean ok = Boolean.FALSE;
                            try {
                                knowledgebaseDAO.updateConceptNameUsedByAnnotations(concept);
                                ok = Boolean.TRUE;
                            }
                            catch (Exception e) {
                                String msg = "Failed to change primary names of annotations from '" + selectedName +
                                             "' to '" + name + "'.";
                                log.error(msg);
                                EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, msg);
                            }

                            return ok;
                        }

                    });


                    /*
                     * If the annotation update was successful we can drop the old conceptname that we
                     * might have created if changing a primary name
                     */
                    if (success) {
                        ConceptName oldPrimaryName = concept.getConceptName(history.getOldValue());
                        if ((oldPrimaryName != null) &&
                                !oldPrimaryName.getNameType().equalsIgnoreCase(ConceptNameTypes.PRIMARY.toString())) {
                            concept.removeConceptName(oldPrimaryName);

                            try {
                                conceptDAO.update(concept);
                            }
                            catch (Exception ex) {
                                log.error("Failed to remove " + oldPrimaryName +
                                          " from the database. This will need to be done manually!!");
                            }
                        }
                    }


                    /*
                     * If the user is an admin go ahead and approve the change. Do this BEFORE you refresh the tree
                     * or your database transaction will fail because of a timestamp mismatch. (ie. Cache does not
                     * match you instance)
                     */

                    if ((userAccount != null) && userAccount.isAdministrator()) {
                        approveHistoryTask.approve(userAccount, history);
                    }

                    /*
                     * Clear the knowledgebasecache since it's no longer
                     * accurate and reopen the tree to the currently edited
                     * node.
                     */
                    Worker.post(new Job() {
                        public Object run() {
                            EventBus.publish(Lookup.TOPIC_REFRESH_KNOWLEGEBASE, concept.getPrimaryConceptName().getName());
                            return null;
                        }

                    });
                    

                }

                waitIndicator.dispose();
            }
        }
    }
}
