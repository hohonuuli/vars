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


package vars.knowledgebase.ui.dialogs;

import foxtrot.Job;
import foxtrot.Worker;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.vars.dao.DAOException;
import org.mbari.vars.knowledgebase.model.Concept;
import org.mbari.vars.knowledgebase.model.ConceptName;
import org.mbari.vars.knowledgebase.model.HistoryFactory;
import org.mbari.vars.knowledgebase.model.dao.ConceptDAO;
import org.mbari.vars.knowledgebase.model.dao.KnowledgeBaseCache;
import org.mbari.vars.knowledgebase.ui.KnowledgebaseApp;
import org.mbari.vars.knowledgebase.ui.KnowledgebaseFrame;
import org.mbari.vars.model.UserAccount;
import org.mbari.vars.util.AppFrameDispatcher;
import vars.knowledgebase.IHistory;

//~--- classes ----------------------------------------------------------------

/**
 * <!-- Class Description -->
 *
 *
 * @version    $Id: AddConceptNameDialog.java 295 2006-07-06 23:47:31Z hohonuuli $
 * @author     MBARI    
 */
public class AddConceptNameDialog extends JDialog {

    private static final long serialVersionUID = -2636179816517133686L;

    private static final Logger log = LoggerFactory.getLogger(AddConceptNameDialog.class);

    //~--- fields -------------------------------------------------------------

    /**
	 * @uml.property  name="concept"
	 * @uml.associationEnd  
	 */
    private Concept concept;
    /**
	 * @uml.property  name="jContentPane"
	 * @uml.associationEnd  
	 */
    private JPanel jContentPane = null;
    /**
	 * @uml.property  name="nameLabel"
	 * @uml.associationEnd  
	 */
    private JLabel nameLabel = null;
    /**
	 * @uml.property  name="authorLabel"
	 * @uml.associationEnd  
	 */
    private JLabel authorLabel = null;
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
	 * @uml.property  name="rbPanel"
	 * @uml.associationEnd  
	 */
    private JPanel rbPanel = null;
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
	 * @uml.property  name="authorField"
	 * @uml.associationEnd  
	 */
    private JTextField authorField = null;
    /**
	 * @uml.property  name="viewPanel"
	 * @uml.associationEnd  
	 */
    private JPanel viewPanel = null;    // @jve:decl-index=0:visual-constraint="405,283"
    /**
	 * @uml.property  name="createButton"
	 * @uml.associationEnd  
	 */
    private JButton createButton = null;
    /**
	 * @uml.property  name="cancelButton"
	 * @uml.associationEnd  
	 */
    private JButton cancelButton = null;
    /**
	 * @uml.property  name="actionPanel"
	 * @uml.associationEnd  
	 */
    private JPanel actionPanel = null;

    //~--- constructors -------------------------------------------------------

    /**
     *     This is the default constructor
     */
    public AddConceptNameDialog() {
        this(null);
    }

    /**
     * @param owner
     * @throws HeadlessException
     */
    public AddConceptNameDialog(Frame owner) throws HeadlessException {
        super(owner, "VARS - Add Concept-name");
        initialize();
    }

    //~--- get methods --------------------------------------------------------

    /**
	 * This method initializes actionPanel
	 * @return  javax.swing.JPanel
	 * @uml.property  name="actionPanel"
	 */
    private JPanel getActionPanel() {
        if (actionPanel == null) {
            actionPanel = new JPanel();
            actionPanel.setLayout(
                    new BoxLayout(getActionPanel(), BoxLayout.X_AXIS));
            actionPanel.add(Box.createHorizontalGlue());
            actionPanel.add(getCreateButton(), null);
            actionPanel.add(Box.createHorizontalStrut(10));
            actionPanel.add(getCancelButton(), null);
            actionPanel.add(Box.createHorizontalGlue());
        }

        return actionPanel;
    }

    /**
	 * This method initializes authorField
	 * @return  javax.swing.JTextField
	 * @uml.property  name="authorField"
	 */
    protected JTextField getAuthorField() {
        if (authorField == null) {
            authorField = new JTextField();
        }

        return authorField;
    }

    /**
	 * This method initializes cancelButton
	 * @return  javax.swing.JButton
	 * @uml.property  name="cancelButton"
	 */
    private JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JButton();

            /*
             * Hide the Dialog when the cancel button is pressed.
             */
            cancelButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    AddConceptNameDialog.this.setVisible(false);
                }
            });
            cancelButton.setText("Cancel");
        }

        return cancelButton;
    }

    /**
	 * This method initializes commonRb
	 * @return  javax.swing.JRadioButton
	 * @uml.property  name="commonRb"
	 */
    protected JRadioButton getCommonRb() {
        if (commonRb == null) {
            commonRb = new JRadioButton();
        }

        return commonRb;
    }

    /**
	 * @return  Returns the concept.
	 * @uml.property  name="concept"
	 */
    public Concept getConcept() {
        return concept;
    }

    /**
	 * This method initializes okButton
	 * @return  javax.swing.JButton
	 * @uml.property  name="createButton"
	 */
    private JButton getCreateButton() {
        if (createButton == null) {
            createButton = new JButton();
            createButton.setAction(new AddConceptNameAction());

            /*
             * Hide the Dialog when the create button is pressed.
             */
            createButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    AddConceptNameDialog.this.setVisible(false);
                }
            });
            createButton.setText("Add");
        }

        return createButton;
    }

    /**
	 * This method initializes viewPanel
	 * @return  javax.swing.JPanel
	 * @uml.property  name="jContentPane"
	 */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(getViewPanel(), java.awt.BorderLayout.CENTER);
            jContentPane.add(getActionPanel(), java.awt.BorderLayout.SOUTH);
        }

        return jContentPane;
    }

    /**
	 * This method initializes nameField
	 * @return  javax.swing.JTextField
	 * @uml.property  name="nameField"
	 */
    protected JTextField getNameField() {
        if (nameField == null) {
            nameField = new JTextField();

            /*
             * The create button should only be enabled if actual text was
             * entered into the name field.
             */
            nameField.getDocument().addDocumentListener(
                    new DocumentListener() {

                public void insertUpdate(DocumentEvent e) {
                    update();
                }
                public void removeUpdate(DocumentEvent e) {
                    update();
                }
                public void changedUpdate(DocumentEvent e) {
                    update();
                }
                private void update() {
                    boolean enable = nameField.getText().matches("\\w+.*");
                    getCreateButton().setEnabled(enable);
                }

            });
        }

        return nameField;
    }

    /**
	 * This method initializes rbPanel
	 * @return  javax.swing.JPanel
	 * @uml.property  name="rbPanel"
	 */
    private JPanel getRbPanel() {
        if (rbPanel == null) {
            synonymLabel = new JLabel();
            synonymLabel.setText("Synonym");
            commonLabel = new JLabel();
            commonLabel.setText("Common");
            rbPanel = new JPanel();
            rbPanel.setLayout(new BoxLayout(getRbPanel(), BoxLayout.X_AXIS));
            rbPanel.add(commonLabel, null);
            rbPanel.add(getCommonRb(), null);
            rbPanel.add(synonymLabel, null);
            rbPanel.add(getSynonymRb(), null);
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add(getCommonRb());
            buttonGroup.add(getSynonymRb());
        }

        return rbPanel;
    }

    /**
	 * This method initializes synonymRb
	 * @return  javax.swing.JRadioButton
	 * @uml.property  name="synonymRb"
	 */
    protected JRadioButton getSynonymRb() {
        if (synonymRb == null) {
            synonymRb = new JRadioButton();
            synonymRb.setSelected(true);
        }

        return synonymRb;
    }

    /**
	 * This method initializes jContentPane
	 * @return  javax.swing.JPanel
	 * @uml.property  name="viewPanel"
	 */
    private JPanel getViewPanel() {
        if (viewPanel == null) {
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 1;
            gridBagConstraints5.gridy = 2;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.gridy = 1;
            gridBagConstraints4.weightx = 1.0;
            gridBagConstraints4.gridx = 1;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridy = 0;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.gridx = 1;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.gridy = 2;
            typeLabel = new JLabel();
            typeLabel.setText("Type:");
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.gridy = 1;
            authorLabel = new JLabel();
            authorLabel.setText("Author:");
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            nameLabel = new JLabel();
            nameLabel.setText("Name:");
            viewPanel = new JPanel();
            viewPanel.setLayout(new GridBagLayout());
            viewPanel.add(nameLabel, gridBagConstraints);
            viewPanel.add(authorLabel, gridBagConstraints1);
            viewPanel.add(typeLabel, gridBagConstraints2);
            viewPanel.add(getNameField(), gridBagConstraints3);
            viewPanel.add(getAuthorField(), gridBagConstraints4);
            viewPanel.add(getRbPanel(), gridBagConstraints5);
        }

        return viewPanel;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * This method initializes this
     *
     * @return void
     */
    private void initialize() {
        //this.setSize(300, 200);
        this.setContentPane(getJContentPane());
        setLocationRelativeTo(AppFrameDispatcher.getFrame());
        pack();
    }

    //~--- set methods --------------------------------------------------------

    /**
	 * @param concept  The concept to set.
	 * @uml.property  name="concept"
	 */
    public void setConcept(Concept concept) {
        this.concept = concept;
        getCreateButton().setEnabled(concept != null);
    }

    public void setVisible(boolean b) {
        super.setVisible(b);

        /*
         * Reset the valueswhen made visible so that we don't have the old
         * values hanging around
         */
        if (b) {
            getNameField().setText("");
            getAuthorField().setText("");
            getSynonymRb().setSelected(true);
        }
    }

    //~--- inner classes ------------------------------------------------------

    private class AddConceptNameAction extends ActionAdapter {

        private static final long serialVersionUID = -8067840245552321972L;

        public void doAction() {
            final String name = getNameField().getText();
            final Concept concept = getConcept();
            boolean okToProceed = true;

            /*
             * Verify that the name is not already used in the database.
             */
            Concept preexistingConcept = null;
            try {
                preexistingConcept = KnowledgeBaseCache.getInstance().findConceptByName(name);
            } catch (DAOException e) {
                if (log.isErrorEnabled()) {
                    log.error(
                            "Failed attempt to look up the concept '" + name +
                            "'",
                            e);
                    AppFrameDispatcher.showErrorDialog(
                            "Unable to connect " +
                            "to the knowledgebase database");
                    okToProceed = false;
                }
            }

            if (okToProceed && (preexistingConcept != null)) {
                String preexistingName = preexistingConcept.getPrimaryConceptNameAsString();
                AppFrameDispatcher.showErrorDialog(
                        "The name '" + name + "' is already used by '" +
                        preexistingName +
                            "'. If you want to add the name to '" +
                                concept.getPrimaryConceptNameAsString() +
                                    "' you must" + "remove it from '" +
                                        preexistingName + "' first.");
                okToProceed = false;
            }

            if (okToProceed) {

                /*
                 * Creat the new conceptName
                 */
                ConceptName conceptName = new ConceptName();
                conceptName.setName(getNameField().getText());
                conceptName.setAuthor(getAuthorField().getText());
                String nameType = ConceptName.NAMETYPE_COMMON;
                if (getSynonymRb().isSelected()) {
                    nameType = ConceptName.NAMETYPE_SYNONYM;
                }

                conceptName.setNameType(nameType);
                concept.addConceptName(conceptName);

                /*
                 * Add a History object to track the change.
                 */
                UserAccount userAccount = (UserAccount) KnowledgebaseApp.DISPATCHER_USERACCOUNT.getValueObject();
                IHistory history = HistoryFactory.add(userAccount, conceptName);
                concept.addHistory(history);

                /*
                 * Store the new name in the database.
                 */
                try {
                    ConceptDAO.getInstance().update(concept);
                } catch (DAOException e) {
                    if (log.isErrorEnabled()) {
                        concept.removeConceptName(conceptName);
                        concept.removeHistory(history);
                        log.error("Failed to update " + concept, e);
                        AppFrameDispatcher.showErrorDialog(
                                "Failed to save" +
                                " changes. Rolling back to previous state");
                    }
                }

                final Frame frame = AppFrameDispatcher.getFrame();
                if ((frame != null) && (frame instanceof KnowledgebaseFrame)) {
                    Worker.post(new Job() {

                        public Object run() {
                            ((KnowledgebaseFrame) frame).refreshTreeAndOpenNode(
                                    concept.getPrimaryConceptNameAsString());
                            return null;
                        }

                    });
                }
            }
        }
    }
}
