/*
 * @(#)AddConceptNameDialog2.java   2009.10.28 at 11:03:10 PDT
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

import foxtrot.Task;
import foxtrot.Worker;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.bushe.swing.event.EventBus;
import org.mbari.swing.SpinningDialWaitIndicator;
import org.mbari.swing.WaitIndicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.DAO;
import vars.UserAccount;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameDAO;
import vars.knowledgebase.ConceptNameTypes;
import vars.knowledgebase.History;
import vars.knowledgebase.HistoryFactory;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.ui.StateLookup;
import vars.knowledgebase.ui.ToolBelt;
import vars.knowledgebase.ui.actions.ApproveHistoryTask;
import vars.shared.ui.FancyButton;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

/**
 *
 * @author  brian
 */
public class AddConceptNameDialog2 extends javax.swing.JDialog {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ApproveHistoryTask approveHistoryTask;
    private javax.swing.JTextField authorField;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton cancelButton;
    private javax.swing.JRadioButton commonRb;
    private Concept concept;
    private final HistoryFactory historyFactory;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private final KnowledgebaseDAOFactory knowledgebaseDAOFactory;
    private final KnowledgebaseFactory knowledgebaseFactory;
    private javax.swing.JLabel msgLabel;
    private javax.swing.JTextField nameField;
    private javax.swing.JButton okButton;
    private javax.swing.JRadioButton synonymRb;
    private JRadioButton formerRb;

    /**
     * Creates new form AddConceptNameDialog2
     *
     * @param parent
     * @param modal
     * @param toolBelt
     */
    public AddConceptNameDialog2(java.awt.Frame parent, boolean modal, ToolBelt toolBelt) {
        super(parent, modal);
        this.knowledgebaseDAOFactory = toolBelt.getKnowledgebaseDAOFactory();
        this.knowledgebaseFactory = toolBelt.getKnowledgebaseFactory();
        this.historyFactory = toolBelt.getHistoryFactory();
        this.approveHistoryTask = toolBelt.getApproveHistoryTask();
        initComponents();
        initModel();
        Frame frame = StateLookup.getApplicationFrame();
        setLocationRelativeTo(frame);
        pack();
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        close();
    }

    private void cancelButtonKeyReleased(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            cancelButtonActionPerformed(null);
        }
    }

    private void close() {
        setVisible(false);
        nameField.setText("");
        msgLabel.setText("");
        authorField.setText("");
    }

    public Concept getConcept() {
        return concept;
    }

private void initComponents() {
        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        authorField = new javax.swing.JTextField();
        commonRb = new javax.swing.JRadioButton();
        synonymRb = new javax.swing.JRadioButton();
        formerRb = new JRadioButton();
        cancelButton = new FancyButton();
        okButton = new FancyButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        msgLabel = new javax.swing.JLabel();

        setTitle("VARS - Add Concept Name");
        //setResizable(false);
        jLabel2.setText("Name:");

        jLabel3.setText("Author:");

        jLabel4.setText("Type:");

        nameField.setToolTipText("The name to store in the database");

        authorField.setToolTipText("An author that described this name in the literature");

        buttonGroup1.add(commonRb);
        commonRb.setText("Common");
        commonRb.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        commonRb.setMargin(new java.awt.Insets(0, 10, 0, 0));

        buttonGroup1.add(synonymRb);
        synonymRb.setText("Synonym");
        synonymRb.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        synonymRb.setMargin(new java.awt.Insets(0, 10, 0, 0));
        
        buttonGroup1.add(formerRb);
        formerRb.setText("Former");
        formerRb.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        formerRb.setMargin(new java.awt.Insets(0, 10, 0, 0));

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
        okButton.setEnabled(false);
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

        jTextArea1.setColumns(20);
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setLineWrap(true);
        jTextArea1.setEditable(false);
        jTextArea1.setRows(5);
        jTextArea1.setText("Add a concept name. Enter a name, author and the type of name. ");
        jScrollPane1.setViewportView(jTextArea1);

        msgLabel.setForeground(new java.awt.Color(153, 0, 0));
        msgLabel.setText(" ");

        GroupLayout layout = new GroupLayout(getContentPane());
        layout.setHorizontalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(layout.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(layout.createParallelGroup(Alignment.TRAILING)
        				.addComponent(msgLabel, GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
        				.addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
        				.addGroup(layout.createSequentialGroup()
        					.addGroup(layout.createParallelGroup(Alignment.LEADING)
        						.addComponent(jLabel3)
        						.addComponent(jLabel4)
        						.addComponent(jLabel2))
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addGroup(layout.createParallelGroup(Alignment.LEADING)
        						.addGroup(layout.createSequentialGroup()
        							.addComponent(commonRb)
        							.addPreferredGap(ComponentPlacement.RELATED)
        							.addComponent(synonymRb)
        							.addPreferredGap(ComponentPlacement.RELATED)
        							.addComponent(formerRb))
        						.addComponent(authorField, GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
        						.addComponent(nameField, GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)))
        				.addGroup(layout.createSequentialGroup()
        					.addComponent(okButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
        			.addContainerGap())
        );
        layout.setVerticalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(layout.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(nameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        				.addComponent(jLabel2))
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(jLabel3)
        				.addComponent(authorField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(jLabel4)
        				.addComponent(commonRb)
        				.addComponent(synonymRb)
        				.addComponent(formerRb))
        			.addGap(17)
        			.addComponent(msgLabel)
        			.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        				.addComponent(okButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        			.addContainerGap())
        );
        getContentPane().setLayout(layout);
        pack();
    }

    private void initModel() {
        nameField.getDocument().addDocumentListener(new DocumentListener() {

            public void changedUpdate(DocumentEvent e) {
                update();
            }
            public void insertUpdate(DocumentEvent e) {
                update();
            }
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            void update() {
                String text = nameField.getText();
                okButton.setEnabled((text != null) && (text.length() > 0) && !text.matches("\\A\\s+"));
            }


        });
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        final String name = nameField.getText();
        Concept myConcept = getConcept();
        final ConceptDAO conceptDAO = knowledgebaseDAOFactory.newConceptDAO();
        final ConceptNameDAO conceptNameDAO = knowledgebaseDAOFactory.newConceptNameDAO();
        boolean okToProceed = true;

        /*
         * Verify that the name is not already used in the database.
         */
        Concept preexistingConcept = null;
        try {
            preexistingConcept = (Concept) Worker.post(new Task() {
                public Object run() throws Exception {
                    return knowledgebaseDAOFactory.newConceptDAO().findByName(name);
                }

            });
        }
        catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Failed attempt to look up the concept '" + name + "'", e);
                msgLabel.setText("Failed to connect to database");
            }

            okToProceed = false;
        }


        if (okToProceed && (preexistingConcept != null)) {
            String preexistingName = preexistingConcept.getPrimaryConceptName().getName();
            msgLabel.setText("The name, " + preexistingName + ", already exits in the knowledgebase");
            okToProceed = false;
        }

        if (okToProceed) {

            WaitIndicator waitIndicator = new SpinningDialWaitIndicator((JFrame) getParent());
            
            /*
             * Creat the new conceptName
             */
            ConceptName conceptName = knowledgebaseFactory.newConceptName();
            conceptName.setName(nameField.getText());
            conceptName.setAuthor(authorField.getText());
            String nameType = ConceptNameTypes.COMMON.toString();
            if (synonymRb.isSelected()) {
                nameType = ConceptNameTypes.SYNONYM.toString();
            }
            else if (formerRb.isSelected()) {
            	nameType = ConceptNameTypes.FORMER.toString();
            }
            conceptName.setNameType(nameType);

            try {
                DAO dao = knowledgebaseDAOFactory.newDAO();
                dao.startTransaction();
                myConcept = dao.merge(myConcept);
                myConcept.addConceptName(conceptName);
                dao.persist(conceptName);


                /*
                 * Add a History object to track the change.
                 */
                final UserAccount userAccount = StateLookup.getUserAccount();
                History history = historyFactory.add(userAccount, conceptName);
                myConcept.getConceptMetadata().addHistory(history);
                dao.persist(history);
                dao.endTransaction();
                EventBus.publish(StateLookup.TOPIC_APPROVE_HISTORY, history);
            }
            catch (Exception e) {
                EventBus.publish(StateLookup.TOPIC_NONFATAL_ERROR, e);
            }
            close();
            waitIndicator.dispose();
        }

    }

    private void okButtonKeyReleased(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            okButtonActionPerformed(null);
        }
    }

    public void setConcept(Concept concept) {
        this.concept = concept;
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            nameField.requestFocus();
            commonRb.setSelected(true);
        }

        super.setVisible(b);
    }
}
