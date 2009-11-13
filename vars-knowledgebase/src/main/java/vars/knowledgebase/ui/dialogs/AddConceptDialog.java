/*
 * @(#)AddConceptDialog.java   2009.10.02 at 04:53:59 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.knowledgebase.ui.dialogs;

import com.google.inject.Inject;
import foxtrot.Job;
import foxtrot.Worker;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import org.bushe.swing.event.EventBus;
import org.mbari.swing.JFancyButton;
import org.mbari.util.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.UserAccount;
import vars.VARSException;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameTypes;
import vars.knowledgebase.History;
import vars.knowledgebase.HistoryFactory;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.ui.KnowledgebaseFrame;
import vars.knowledgebase.ui.Lookup;
import vars.knowledgebase.ui.Toolbelt;
import vars.knowledgebase.ui.actions.ApproveHistoryTask;
import vars.shared.ui.AllConceptNamesComboBox;
import vars.shared.ui.GlobalLookup;

/**
 * @author brian
 */
public class AddConceptDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 6993327643414741677L;
    private static final Logger log = LoggerFactory.getLogger(AddConceptDialog.class);
    private final AddConceptDialogController controller;
    final Toolbelt toolBelt;
 
    private javax.swing.JTextField authorField;
    private javax.swing.JLabel authorLabel;
    private javax.swing.JButton cancelButton;
    private Concept concept;
    private javax.swing.JComboBox conceptComboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nodcField;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel parentLabel;
    private javax.swing.JComboBox rankLevelComboBox;
    private javax.swing.JComboBox rankNameComboBox;
    private javax.swing.JTextArea referenceText;
    private javax.swing.JTextArea titleText;


    /**
     * Creates new form AddConceptDialog
     *
     * @param approveHistoryTask
     * @param knowledgebaseDAOFactory
     * @param knowledgebaseFactory
     * @param queryDAO
     */
    @Inject
    public AddConceptDialog(Toolbelt toolBelt) {
        super((Frame) Lookup.getApplicationFrameDispatcher().getValueObject(), true);
        if (toolBelt == null) {
            throw new IllegalArgumentException("ToolBelt argument can not be null");
        }
        this.toolBelt = toolBelt;
        controller = new AddConceptDialogController(toolBelt);
        initComponents();
        initModel();
        setLocationRelativeTo((Frame) Lookup.getApplicationFrameDispatcher().getValueObject());
        pack();
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {    
        setVisible(false);
        setConcept(null);
    }                                                                     

    private void cancelButtonKeyReleased(java.awt.event.KeyEvent evt) {    
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            cancelButtonActionPerformed(null);
        }
    }    

    /**
     * @return  the conceptComboBox
     */
    public javax.swing.JComboBox getConceptComboBox() {
        return conceptComboBox;
    }

     private void initComponents() {
        nameLabel = new javax.swing.JLabel();
        authorLabel = new javax.swing.JLabel();
        authorField = new javax.swing.JTextField();
        nameField = new javax.swing.JTextField();
        parentLabel = new javax.swing.JLabel();
        conceptComboBox = new AllConceptNamesComboBox(toolBelt.getQueryDAO());
        cancelButton = new JFancyButton();
        okButton = new JFancyButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        titleText = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        nodcField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        referenceText = new javax.swing.JTextArea();
        rankNameComboBox = new javax.swing.JComboBox();
        rankLevelComboBox = new javax.swing.JComboBox();

        setTitle("VARS - Create a New Concept");
        nameLabel.setText("Name:");

        authorLabel.setText("Parent:");

        authorField.setToolTipText("(OPTIONAL) The author is the person who first described this species in the scientific literature");

        nameField.setToolTipText("The primary name to be used for this concept.");

        parentLabel.setText("Author:");

        getConceptComboBox().setModel(getConceptComboBox().getModel());
        getConceptComboBox().setSelectedItem(ConceptName.NAME_DEFAULT);
        getConceptComboBox().setToolTipText("The parent concept. This concept will be a child of this parent.");

        cancelButton.setText("Cancel");
        cancelButton.setIcon(new ImageIcon(getClass().getResource("/images/vars/knowledgebase/delete2.png")));

        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        cancelButton.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cancelButtonKeyReleased(evt);
            }
        });

        okButton.setText("OK");
        okButton.setIcon(new ImageIcon(getClass().getResource("/images/vars/knowledgebase/check2.png")));
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        okButton.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                okButtonKeyReleased(evt);
            }
        });

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        titleText.setColumns(20);
        titleText.setWrapStyleWord(true);
        titleText.setLineWrap(true);
        titleText.setEditable(false);
        titleText.setRows(5);
        titleText.setText("Create/edit a concept. You must provide a name and select the parent concept. All other fields are optional.");
        titleText.setFocusable(false);
        jScrollPane1.setViewportView(titleText);

        jLabel1.setText("Nodc Code:");

        nodcField.setToolTipText("(OPTIONAL) The nodc code is also called the Taxonomic Serial Number. More information can be found at http://www.itis.usda.gov");

        jLabel2.setText("Rank Name:");

        jLabel3.setText("Rank Level:");

        jLabel4.setText("Reference:");

        referenceText.setColumns(20);
        referenceText.setRows(5);
        referenceText.setToolTipText("(OPTIONAL) A reference to literature that contains a description of this concept.");
        jScrollPane2.setViewportView(referenceText);

        rankNameComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "kingdom", "phylum", "class", "order", "family", "genus", "species" }));
        rankNameComboBox.setToolTipText("(OPTIONAL) The taxonomic rank");

        rankLevelComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "infra", "sub", "super" }));
        rankLevelComboBox.setToolTipText("(OPTIONAL) This modifies the taxonomic rank");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(okButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cancelButton))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel1)
                            .add(jLabel2)
                            .add(jLabel3)
                            .add(jLabel4)
                            .add(authorLabel)
                            .add(nameLabel)
                            .add(parentLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, nameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, nodcField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, rankNameComboBox, 0, 275, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, rankLevelComboBox, 0, 275, Short.MAX_VALUE)
                            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                            .add(authorField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                .add(getConceptComboBox(), 0, 275, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(13, 13, 13)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(nameLabel)
                    .add(nameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(authorLabel)
                    .add(getConceptComboBox(), org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(parentLabel)
                    .add(authorField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(nodcField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(rankNameComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(rankLevelComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel4)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cancelButton)
                    .add(okButton))
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void initModel() {

        final Dispatcher conceptDispatcher = Lookup.getSelectedConceptDispatcher();

        /*
        * Listen for the node in the tree that's been selected to set the
        * selection in the combobox
        */
        conceptDispatcher.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(final PropertyChangeEvent evt) {
                final Concept selectedConcept = (Concept) evt.getNewValue();
                String conceptName = ConceptName.NAME_DEFAULT;
                if (selectedConcept != null) {
                    conceptName = selectedConcept.getPrimaryConceptName().getName();
                }

                getConceptComboBox().getModel().setSelectedItem(conceptName);
            }

        });

        /*
         * It's important to do this. Otherwise when the dialog is first displayed
         * the conceptComboBox will have 'object' selected no matter what node is
         * being edited.
         */
        final Concept selectedConcept = (Concept) conceptDispatcher.getValueObject();
        String conceptName = ConceptName.NAME_DEFAULT;
        if (selectedConcept != null) {
            conceptName = selectedConcept.getPrimaryConceptName().getName();
        }

        getConceptComboBox().getModel().setSelectedItem(conceptName);


    }

    private boolean isValidString(String s) {
        return (s != null) && (s.length() > 0) && !s.matches("\\A\\s+");
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {    //GEN-FIRST:event_okButtonActionPerformed
        setVisible(false);

        if (concept == null) {
            concept = controller.createConcept();
        }

        try {
            if (concept != null) {
                controller.updateValues(concept);
            }
        }
        catch (Exception ex) {
            log.error("Update failed for " + concept, ex);
            EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, ex);
        }

        final Frame frame = (Frame) Lookup.getApplicationFrameDispatcher().getValueObject();
        if ((frame != null) && (frame instanceof KnowledgebaseFrame) && (concept != null)) {
            final String name = concept.getPrimaryConceptName().getName();
            Worker.post(new Job() {

                public Object run() {
                    ((KnowledgebaseFrame) frame).refreshTreeAndOpenNode(name);

                    return null;
                }


            });
        }

        setConcept(null);

    }                                                                  

    private void okButtonKeyReleased(java.awt.event.KeyEvent evt) {    
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            okButtonActionPerformed(null);
        }
    }    

    public void setConcept(Concept concept) {
        this.concept = concept;

        if (concept == null) {
            nameField.setEnabled(true);
            nameField.setText("");
            authorField.setText("");
            nodcField.setText("");
            rankLevelComboBox.setSelectedIndex(0);
            rankNameComboBox.setSelectedIndex(0);
            referenceText.setText("");
            setTitle("VARS - Create a New Concept");
        }
        else {
            nameField.setEnabled(false);
            nameField.setText(concept.getPrimaryConceptName().getName());
            authorField.setText(concept.getPrimaryConceptName().getAuthor());
            nodcField.setText(concept.getNodcCode());
            rankLevelComboBox.setSelectedItem(concept.getRankLevel());
            rankNameComboBox.setSelectedItem(concept.getRankName());
            referenceText.setText(concept.getReference());
            getConceptComboBox().setSelectedItem(concept.getParentConcept().getPrimaryConceptName().getName());
            setTitle("VARS - Edit an Existing Concept");
        }

    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            nameField.requestFocus();
        }

        super.setVisible(b);
    }

    private class AddConceptDialogController {

        private final Toolbelt toolBelt;

        /**
         * Constructs ...
         */
        public AddConceptDialogController(Toolbelt toolBelt) {
            this.toolBelt = toolBelt;

        }

        public Concept createConcept() {

            /*
             * Get the parent concept
             */
            Concept concept = null;
            Concept parentConcept = null;
            ConceptDAO dao = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
            try {
                
                dao.startTransaction();
                parentConcept = dao.findByName((String) getConceptComboBox().getSelectedItem());
                dao.endTransaction();
            }
            catch (Exception ex) {
                String msg = "Failed to lookup '" + getConceptComboBox().getSelectedItem() +
                             "'. Canceling your request";
                log.error(msg, ex);
                EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, msg);

                return concept;
            }

            if (parentConcept == null) {

                // TODO brian: Make sure that there are no existing root concepts
                throw new VARSException("No parent Concept was specified. You MUST Specify a parent Concept");
            }

            /*
             * Check userAccount status
             */
            UserAccount userAccount = (UserAccount) GlobalLookup.getUserAccountDispatcher().getValueObject();
            boolean okToProceed = (userAccount != null);

            String primaryName = nameField.getText();
            if (okToProceed && (primaryName != null)) {

                // Do not add a concept with a name that already exists in the database
                Concept existingConcept = null;
                try {

                    dao.startTransaction();
                    existingConcept = dao.findByName(primaryName);
                    dao.endTransaction();
                }
                catch (Exception e) {
                    if (log.isErrorEnabled()) {
                        log.error("Failed to lookup '" + primaryName + "' in the knowledgebase", e);
                    }

                    EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR,
                                     "Unable to complete " + "your request. An error occured while attempting " +
                                     "to query the database");
                    okToProceed = false;
                }

                if (okToProceed && (existingConcept != null)) {
                    EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR,
                                     "Unable to complete your " + "request. The name, '" + primaryName +
                                     "' already exists in the knowledgebase.");
                    okToProceed = false;
                }

                // Add the concept to the database;

                if (okToProceed) {
                    KnowledgebaseFactory knowledgebaseFactory = toolBelt.getKnowledgebaseFactory();
                    concept = knowledgebaseFactory.newConcept();

                    // Set reuired fields
                    ConceptName conceptName = knowledgebaseFactory.newConceptName();
                    conceptName.setName(primaryName);
                    conceptName.setNameType(ConceptNameTypes.PRIMARY.toString());
                    concept.addConceptName(conceptName);
                    concept.setOriginator(userAccount.getUserName());
                    

                    try {
                        dao.startTransaction();
                        parentConcept = dao.merge(parentConcept);
                        parentConcept.addChildConcept(concept);
                        dao.persist(concept);
                        dao.endTransaction();
                    }
                    catch (Exception e) {
                        log.error("Failed to insert " + concept, e);
                        EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR,
                                         "Unable to complete " + "your request. There was a problem with the " +
                                         "database transaction.");
                        parentConcept.removeChildConcept(concept);
                        okToProceed = false;
                    }
                }

                // Generate a history for the new Concept
                History history = null;
                if (okToProceed) {
                    history = toolBelt.getHistoryFactory().add(userAccount, concept);
                    parentConcept.getConceptMetadata().addHistory(history);

                    if (log.isDebugEnabled()) {
                        log.debug("Adding " + history + " to " + parentConcept);
                    }

                    try {
                        dao.startTransaction();
                        parentConcept = dao.merge(parentConcept);
                        parentConcept.getConceptMetadata().addHistory(history);
                        dao.persist(parentConcept);
                        dao.endTransaction();
                    }
                    catch (Exception e) {
                        log.error("Failed to update " + parentConcept, e);
                        EventBus.publish(Lookup.TOPIC_WARNING,
                                         "There is a problem " +
                                         "with the database connection. Unable to add history" +
                                         " information to the database.");
                    }

                    EventBus.publish(Lookup.TOPIC_APPROVE_HISTORY, history);
                }


            }

            return concept;
        }

        void updateValues(Concept concept) {

            final UserAccount userAccount = (UserAccount) GlobalLookup.getUserAccountDispatcher().getValueObject();
            final HistoryFactory historyFactory = toolBelt.getHistoryFactory();
            final ApproveHistoryTask approveHistoryTask = toolBelt.getApproveHistoryTask();
            final List<History> histories = new ArrayList<History>();

            /*
            * Modify the parent concept
            */
            final String parentName = (String) getConceptComboBox().getSelectedItem();
            Concept oldParentConcept = (Concept) concept.getParentConcept();
            ConceptDAO conceptDAO = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
            conceptDAO.startTransaction();
            final Concept newParentConcept = conceptDAO.findByName(parentName);
            boolean hasDescendent = concept.hasDescendent(parentName);
            conceptDAO.endTransaction();

            /*
             * Make sure that you didn't tyr to add it to a descendant
             */
            if (hasDescendent) {
                EventBus.publish(Lookup.TOPIC_WARNING,
                                 "The parent that you specified, '" + parentName + "', is already a child" + " of '" +
                                 concept.getPrimaryConceptName().getName() +
                                 "'. This is not allowed. Your request to move" + " the concept is being ignored.");
            }
            else if (!newParentConcept.equals(oldParentConcept) && !newParentConcept.equals(concept)) {

                conceptDAO.startTransaction();

                oldParentConcept = conceptDAO.merge(oldParentConcept);
                concept = conceptDAO.merge(concept);
                if (oldParentConcept != null) {
                    oldParentConcept.removeChildConcept(concept);
                    conceptDAO.remove(concept);
                }

                newParentConcept.addChildConcept(concept);
                History history1 = historyFactory.replaceParentConcept(userAccount, oldParentConcept,
                    newParentConcept);
                concept.getConceptMetadata().addHistory(history1);
                conceptDAO.persist(history1);
                conceptDAO.endTransaction();

                histories.add(history1);
            }

            // Set optional fields
            final String oldNodcCode = concept.getNodcCode();
            final String nodcCode = isValidString(nodcField.getText()) ? nodcField.getText() : null;
            if (((nodcCode != null) && !nodcCode.equals(oldNodcCode)) ||
                    ((nodcCode == null) && (oldNodcCode != null))) {
                History history2 = historyFactory.replaceNodcCode(userAccount, oldNodcCode, nodcCode);

                conceptDAO.startTransaction();
                concept = conceptDAO.merge(concept);
                concept.getConceptMetadata().addHistory(history2);
                conceptDAO.persist(history2);
                concept.setNodcCode(nodcCode);
                conceptDAO.endTransaction();

                //histories.add(history2);
            }

            final String oldRankName = concept.getRankName();
            final String rankName = isValidString((String) rankNameComboBox.getSelectedItem())
                                    ? (String) rankNameComboBox.getSelectedItem() : null;
            if (((rankName != null) && !rankName.equals(oldRankName)) ||
                    ((rankName == null) && (oldRankName != null))) {
                History history3 = historyFactory.replaceRankName(userAccount, oldRankName, rankName);

                conceptDAO.startTransaction();
                concept = conceptDAO.merge(concept);
                concept.getConceptMetadata().addHistory(history3);
                concept.setRankName(rankName);
                conceptDAO.persist(history3);
                conceptDAO.endTransaction();

                //histories.add(history3);
            }

            final String oldRankLevel = concept.getRankLevel();
            final String rankLevel = isValidString((String) rankLevelComboBox.getSelectedItem())
                                     ? (String) rankLevelComboBox.getSelectedItem() : null;
            if (((rankLevel != null) && !rankLevel.equals(oldRankLevel)) ||
                    ((rankLevel == null) && (oldRankLevel != null))) {
                History history4 = historyFactory.replaceRankLevel(userAccount, oldRankLevel, rankLevel);

                conceptDAO.startTransaction();
                concept = conceptDAO.merge(concept);
                concept.getConceptMetadata().addHistory(history4);
                concept.setRankLevel(rankLevel);
                conceptDAO.persist(history4);
                conceptDAO.endTransaction();

                //histories.add(history4);
            }

            final String oldReference = concept.getReference();
            final String reference = isValidString(referenceText.getText()) ? referenceText.getText() : null;
            if (((reference != null) && !reference.equals(oldReference)) ||
                    ((reference == null) && (oldReference != null))) {
                History history5 = historyFactory.replaceReference(userAccount, oldReference, reference);

                conceptDAO.startTransaction();
                concept = conceptDAO.merge(concept);
                concept.getConceptMetadata().addHistory(history5);
                concept.setReference(reference);
                conceptDAO.persist(history5);
                conceptDAO.endTransaction();

                //histories.add(history5);
            }

            conceptDAO.startTransaction();
            concept = conceptDAO.merge(concept);
            final String author = authorField.getText();
            final ConceptName primaryName = (ConceptName) concept.getPrimaryConceptName();
            if (isValidString(author)) {
                primaryName.setAuthor(author);
            }
            else {
                primaryName.setAuthor(null);
            }
            conceptDAO.endTransaction();
            
            /*
             *  TODO We aren't automatically approving all histories. This sometimes causes a 
             *  concurrent modification exception in the knowledbase. So we add all the histories
             *  but only approve any change in parents.
             */
            for (History history : histories) {
                EventBus.publish(Lookup.TOPIC_APPROVE_HISTORY, history);
            }

        }
    }
}
