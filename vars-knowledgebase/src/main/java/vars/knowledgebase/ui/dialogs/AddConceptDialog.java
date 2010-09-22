/*
 * @(#)AddConceptDialog.java   2010.05.05 at 09:34:40 PDT
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



package vars.knowledgebase.ui.dialogs;

import com.google.inject.Inject;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import org.bushe.swing.event.EventBus;
import org.mbari.util.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.CacheClearedEvent;
import vars.CacheClearedListener;
import vars.UserAccount;
import vars.VARSException;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameTypes;
import vars.knowledgebase.History;
import vars.knowledgebase.HistoryFactory;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.ui.Lookup;
import vars.knowledgebase.ui.ToolBelt;
import vars.shared.ui.AllConceptNamesComboBox;
import vars.shared.ui.FancyButton;
import vars.shared.ui.GlobalLookup;

/**
 * @author brian
 */
public class AddConceptDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 6993327643414741677L;
    private static final Logger log = LoggerFactory.getLogger(AddConceptDialog.class);
    private javax.swing.JTextField authorField;
    private javax.swing.JLabel authorLabel;
    private javax.swing.JButton cancelButton;
    private Concept concept;
    private AllConceptNamesComboBox conceptComboBox;
    private final AddConceptDialogController controller;
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
    final ToolBelt toolBelt;

    /**
     * Creates new form AddConceptDialog
     *
     *
     * @param toolBelt
     */
    @Inject
    public AddConceptDialog(ToolBelt toolBelt) {
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
        toolBelt.getPersistenceCache().addCacheClearedListener(new CacheClearedListener() {
            public void afterClear(CacheClearedEvent evt) {
                conceptComboBox.updateConceptNames();
            }

            public void beforeClear(CacheClearedEvent evt) {
                // DO nothing
            }
        });
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
        conceptComboBox = new AllConceptNamesComboBox(toolBelt.getQueryPersistenceService());
        cancelButton = new FancyButton();
        okButton = new FancyButton();
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

        authorField.setToolTipText(
            "(OPTIONAL) The author is the person who first described this species in the scientific literature");

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
        titleText.setText(
            "Create/edit a concept. You must provide a name and select the parent concept. All other fields are optional.");
        titleText.setFocusable(false);
        jScrollPane1.setViewportView(titleText);

        jLabel1.setText("Nodc Code:");

        nodcField.setToolTipText(
            "(OPTIONAL) The nodc code is also called the Taxonomic Serial Number. More information can be found at http://www.itis.usda.gov");

        jLabel2.setText("Rank Name:");

        jLabel3.setText("Rank Level:");

        jLabel4.setText("Reference:");

        referenceText.setColumns(20);
        referenceText.setRows(5);
        referenceText.setToolTipText(
            "(OPTIONAL) A reference to literature that contains a description of this concept.");
        jScrollPane2.setViewportView(referenceText);

        rankNameComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
            "", "kingdom", "phylum", "class", "order", "family", "genus", "species"
        }));
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
    }    // </editor-fold>//GEN-END:initComponents

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

        try {
            if (concept == null) {
                concept = controller.createConcept();
            }
            else {
                controller.updateConcept(concept);
            }
        }
        catch (Exception ex) {
            log.error("User operation failed for " + concept, ex);
            EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, ex);
        }

        setConcept(null);

    }

    private void okButtonKeyReleased(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            okButtonActionPerformed(null);
        }
    }

    /**
     *
     * @param concept
     */
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

    /**
     *
     * @param b
     */
    @Override
    public void setVisible(boolean b) {
        if (b) {
            nameField.requestFocus();
        }

        super.setVisible(b);
    }

    private class AddConceptDialogController {

        private final ToolBelt toolBelt;

        /**
         * Constructs ...
         *
         * @param toolBelt
         */
        public AddConceptDialogController(ToolBelt toolBelt) {
            this.toolBelt = toolBelt;

        }

        /**
         * @return
         */
        public Concept createConcept() {

            /*
             * Get the parent concept
             */
            Concept concept = null;

            // DAOTX
            ConceptDAO dao = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
            dao.startTransaction();
            Concept parentConcept = dao.findByName((String) getConceptComboBox().getSelectedItem());

            if (parentConcept == null) {
                // TODO brian: Make sure that there are no existing root concepts
                throw new VARSException("No parent Concept was specified. You MUST Specify a parent Concept");
            }

            /*
             * Check userAccount status
             */
            UserAccount userAccount = (UserAccount) GlobalLookup.getUserAccountDispatcher().getValueObject();

            String primaryName = nameField.getText();
            History history = null;
            if ((userAccount != null) && (primaryName != null)) {

                // Do not add a concept with a name that already exists in the database.
                // NOTE: findByName can be case insensitive (depending on DB config), so check case
                Concept existingConcept = dao.findByName(primaryName);
                if (existingConcept != null &&
                        !existingConcept.getPrimaryConceptName().getName().equals(primaryName)) {
                    existingConcept = null;
                }

                // Add the concept to the database;
                if (existingConcept == null) {
                    KnowledgebaseFactory knowledgebaseFactory = toolBelt.getKnowledgebaseFactory();
                    concept = knowledgebaseFactory.newConcept();

                    // Set required fields
                    ConceptName conceptName = knowledgebaseFactory.newConceptName();
                    conceptName.setName(primaryName);
                    conceptName.setNameType(ConceptNameTypes.PRIMARY.toString());
                    concept.addConceptName(conceptName);
                    concept.setOriginator(userAccount.getUserName());

                    // Set optional fields
                    final String nodcCode = isValidString(nodcField.getText()) ? nodcField.getText() : null;
                    concept.setNodcCode(nodcCode);
                    final String rankName = isValidString((String) rankNameComboBox.getSelectedItem())
                                    ? (String) rankNameComboBox.getSelectedItem() : null;
                    concept.setRankName(rankName);
                    final String rankLevel = isValidString((String) rankLevelComboBox.getSelectedItem())
                                     ? (String) rankLevelComboBox.getSelectedItem() : null;
                    concept.setRankLevel(rankLevel);
                    final String reference = isValidString(referenceText.getText()) ? referenceText.getText() : null;
                    concept.setReference(reference);

                    final String author = authorField.getText();
                    if (isValidString(author)) {
                        conceptName.setAuthor(author);
                    }
                    else {
                        conceptName.setAuthor(null);
                    }

                    parentConcept.addChildConcept(concept);
                    dao.persist(concept);

                    // Add History
                    history = toolBelt.getHistoryFactory().add(userAccount, concept);
                    parentConcept.getConceptMetadata().addHistory(history);
                    dao.persist(history);


                }
                else {
                    EventBus.publish(Lookup.TOPIC_WARNING,
                                     "The name '" + primaryName + "' already exists in the database.");
                }

            }
            dao.endTransaction();
            dao.close();

            if (history != null) {
                EventBus.publish(Lookup.TOPIC_APPROVE_HISTORY, history);
            }

            return concept;
        }

        void updateConcept(Concept concept) {

            final UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();
            final HistoryFactory historyFactory = toolBelt.getHistoryFactory();
            final List<History> histories = new ArrayList<History>();
            final String parentName = (String) getConceptComboBox().getSelectedItem();

            /*
             * DAOTX Modify the parent concept
             */
            ConceptDAO conceptDAO = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
            conceptDAO.startTransaction();
            concept = conceptDAO.find(concept);
            Concept oldParentConcept = concept.getParentConcept();

            final Concept newParentConcept = conceptDAO.findByName(parentName);
            boolean hasDescendent = concept.hasDescendent(parentName);

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

                if (oldParentConcept != null) {
                    oldParentConcept.removeChildConcept(concept);
                }

                newParentConcept.addChildConcept(concept);
                History history1 = historyFactory.replaceParentConcept(userAccount, oldParentConcept, newParentConcept);
                concept.getConceptMetadata().addHistory(history1);
                conceptDAO.persist(history1);
                histories.add(history1);
            }

            // Set optional fields
            final String oldNodcCode = concept.getNodcCode();
            final String nodcCode = isValidString(nodcField.getText()) ? nodcField.getText() : null;
            if (((nodcCode != null) && !nodcCode.equals(oldNodcCode)) ||
                    ((nodcCode == null) && (oldNodcCode != null))) {
                History history2 = historyFactory.replaceNodcCode(userAccount, oldNodcCode, nodcCode);

                concept.getConceptMetadata().addHistory(history2);
                conceptDAO.persist(history2);
                concept.setNodcCode(nodcCode);

                histories.add(history2);
            }

            final String oldRankName = concept.getRankName();
            final String rankName = isValidString((String) rankNameComboBox.getSelectedItem())
                                    ? (String) rankNameComboBox.getSelectedItem() : null;
            if (((rankName != null) && !rankName.equals(oldRankName)) ||
                    ((rankName == null) && (oldRankName != null))) {
                History history3 = historyFactory.replaceRankName(userAccount, oldRankName, rankName);

                concept.getConceptMetadata().addHistory(history3);
                concept.setRankName(rankName);
                conceptDAO.persist(history3);

                histories.add(history3);
            }

            final String oldRankLevel = concept.getRankLevel();
            final String rankLevel = isValidString((String) rankLevelComboBox.getSelectedItem())
                                     ? (String) rankLevelComboBox.getSelectedItem() : null;
            if (((rankLevel != null) && !rankLevel.equals(oldRankLevel)) ||
                    ((rankLevel == null) && (oldRankLevel != null))) {
                History history4 = historyFactory.replaceRankLevel(userAccount, oldRankLevel, rankLevel);

                concept.getConceptMetadata().addHistory(history4);
                concept.setRankLevel(rankLevel);
                conceptDAO.persist(history4);

                histories.add(history4);
            }

            final String oldReference = concept.getReference();
            final String reference = isValidString(referenceText.getText()) ? referenceText.getText() : null;
            if (((reference != null) && !reference.equals(oldReference)) ||
                    ((reference == null) && (oldReference != null))) {
                History history5 = historyFactory.replaceReference(userAccount, oldReference, reference);
                concept.getConceptMetadata().addHistory(history5);
                concept.setReference(reference);
                conceptDAO.persist(history5);

                histories.add(history5);
            }

            final String author = authorField.getText();
            final ConceptName primaryName = (ConceptName) concept.getPrimaryConceptName();
            if (isValidString(author)) {
                primaryName.setAuthor(author);
            }
            else {
                primaryName.setAuthor(null);
            }

            conceptDAO.endTransaction();
            conceptDAO.close();

            EventBus.publish(Lookup.TOPIC_APPROVE_HISTORIES, histories);

        }
    }
}
