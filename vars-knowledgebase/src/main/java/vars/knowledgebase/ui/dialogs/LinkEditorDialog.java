/**
 *
 */
package vars.knowledgebase.ui.dialogs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mbari.vars.dao.DAOException;
import org.mbari.vars.knowledgebase.model.Concept;
import org.mbari.vars.knowledgebase.model.ConceptName;
import org.mbari.vars.knowledgebase.model.LinkTemplate;
import org.mbari.vars.knowledgebase.model.dao.KnowledgeBaseCache;
import org.mbari.vars.knowledgebase.model.dao.LinkTemplateDAO;
import org.mbari.vars.knowledgebase.ui.ILockableEditor;
import vars.ILink;
import org.mbari.vars.query.ui.ConceptConstraints;
import org.mbari.vars.ui.HierachicalConceptNameComboBox;
import org.mbari.vars.ui.OkCancelButtonPanel;
import org.mbari.vars.util.AppFrameDispatcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * @author brian
 *
 */
public class LinkEditorDialog extends JDialog implements ILockableEditor {

    private static final Concept SELF_CONCEPT = new Concept(new ConceptName(
            "self", ConceptName.NAMETYPE_PRIMARY), null);

    private static final Concept NIL_CONCEPT = new Concept(new ConceptName(
            ConceptConstraints.WILD_CARD_STRING, ConceptName.NAMETYPE_PRIMARY),
            null);
    
    private static final Logger log = LoggerFactory.getLogger(LinkEditorDialog.class);

    private static final long serialVersionUID = 1L;

    private JPanel jContentPane = null;

    private JPanel editorPanel = null;

    private OkCancelButtonPanel buttonPanel = null;

    private JLabel linkNameLabel = null;

    private JLabel toConceptLabel = null;

    private JLabel linkValueLabel = null;

    private JTextField linkNameTextField = null;

    private HierachicalConceptNameComboBox toConceptComboBox = null;

    private JScrollPane scrollPane = null;

    private JTextArea linkValueTextArea = null;

    private ILink link;

    private ILink EMPTY_LINK = new ILink() {

        public String getFromConcept() {
            return null;
        }

        public String getLinkName() {
            return ConceptConstraints.WILD_CARD_STRING;
        }

        public String getLinkValue() {
            return ConceptConstraints.WILD_CARD_STRING;
        }

        public String getToConcept() {
            return ConceptConstraints.WILD_CARD_STRING;
        }

        public void setLinkName(String linkName_) {
            // Do nothing

        }

        public void setLinkValue(String linkValue_) {
            // Do nothing
        }

        public void setToConcept(String toConcept_) {
            // Do nothing
        }

    };

    /**
     * @throws HeadlessException
     */
    public LinkEditorDialog() throws HeadlessException {

        super();
        setModal(true);
        initialize();
    }

    /**
     * @param owner
     * @throws HeadlessException
     */
    public LinkEditorDialog(Frame owner) throws HeadlessException {
        super(owner);
        setModal(true);
        initialize();
    }

    /**
     *
     * @param link The link to be edited
     * @return 
     */
    public void setLink(ILink link) {

        this.link = link;

        if (link == null) {
            getLinkNameTextField().setText("");
            getLinkValueTextArea().setText("");
            getToConceptComboBox().setConcept(SELF_CONCEPT);
            return;
        }

        // FInd matching link template and use it to populate the toConceptComboBox
        /*
         * Find the LinkTemplate that the LinkRealization is based on.
         */
        Set<LinkTemplate> matchingLinkTemplates = null;
        try {
            matchingLinkTemplates = LinkTemplateDAO.getInstance().findByLinkName(link.getLinkName());
        }
        catch (DAOException e) {
            log.error("Failed to lookup LinkTemplates with linkName = " + link.getLinkName(), e);
            matchingLinkTemplates = new HashSet<LinkTemplate>();
        }

        /*
        * Get the toConceptAsString that's used. It will be a child of the toConceptAsString in the LinkTemplate
        */
        String toConceptAsString = null;
        if (matchingLinkTemplates.isEmpty()) {
            AppFrameDispatcher.showWarningDialog("Unable to find a LinkTemplate that matches '" + link + "'");
            toConceptAsString = link.getToConcept();
        }
        else {
            ILink matchingLink = (ILink) matchingLinkTemplates.iterator().next();
            toConceptAsString = matchingLink.getToConcept();
        }

        /*
        *
        */
        Concept concept = null;
        Concept selectedConcept = null;
        HierachicalConceptNameComboBox cb = getToConceptComboBox();
        cb.removeAllItems();
        if (toConceptAsString.equalsIgnoreCase("self")) {
            concept = SELF_CONCEPT;
            selectedConcept = SELF_CONCEPT;
            cb.addItem(SELF_CONCEPT.getPrimaryConceptName());
        }
        else if (toConceptAsString.equalsIgnoreCase(ConceptConstraints.WILD_CARD_STRING)) {
            concept = NIL_CONCEPT;
            selectedConcept = NIL_CONCEPT;
            cb.addItem(NIL_CONCEPT.getPrimaryConceptName());
        }
        else {
            try {
                concept = KnowledgeBaseCache.getInstance().findConceptByName(toConceptAsString);
                selectedConcept = KnowledgeBaseCache.getInstance().findConceptByName(link.getToConcept());
                cb.setConcept(concept);
            }
            catch (DAOException e) {
                log.error("", e);
                AppFrameDispatcher.showWarningDialog("A database error occurred. Try refreshing the knowledgebase");
                concept = NIL_CONCEPT;
                selectedConcept = NIL_CONCEPT;
                cb.addItem(NIL_CONCEPT.getPrimaryConceptName());
            }
        }
        cb.setSelectedItem(selectedConcept);

        // And here we set the other 2 fields
        getLinkNameTextField().setText(link.getLinkName());
        getLinkValueTextArea().setText(link.getLinkValue());

    }

    public ILink getLink() {
        return link;
    }

    /**
     * This method initializes this
     *
     * @return void
     */
    private void initialize() {
        this.setSize(300, 200);
        this.setContentPane(getJContentPane());
    }

    /**
     * This method initializes jContentPane
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(getEditorPanel(), BorderLayout.CENTER);
            jContentPane.add(getButtonPanel(), BorderLayout.SOUTH);
        }
        return jContentPane;
    }

    /**
     * This method initializes editorPanel	
     *
     * @return javax.swing.JPanel
     */
    private JPanel getEditorPanel() {
        if (editorPanel == null) {
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.fill = GridBagConstraints.BOTH;
            gridBagConstraints5.gridy = 2;
            gridBagConstraints5.weightx = 1.0;
            gridBagConstraints5.weighty = 1.0;
            gridBagConstraints5.anchor = GridBagConstraints.NORTHWEST;
            gridBagConstraints5.insets = new Insets(0, 0, 5, 20);
            gridBagConstraints5.gridx = 1;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.fill = GridBagConstraints.BOTH;
            gridBagConstraints4.gridy = 1;
            gridBagConstraints4.weightx = 1.0;
            gridBagConstraints4.insets = new Insets(0, 0, 5, 20);
            gridBagConstraints4.gridx = 1;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = GridBagConstraints.BOTH;
            gridBagConstraints3.gridy = 0;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.insets = new Insets(5, 0, 5, 20);
            gridBagConstraints3.gridx = 1;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.anchor = GridBagConstraints.NORTHWEST;
            gridBagConstraints2.insets = new Insets(0, 20, 5, 0);
            gridBagConstraints2.gridy = 2;
            linkValueLabel = new JLabel();
            linkValueLabel.setText("Link Value:");
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.anchor = GridBagConstraints.NORTHWEST;
            gridBagConstraints1.insets = new Insets(0, 20, 5, 0);
            gridBagConstraints1.gridy = 1;
            toConceptLabel = new JLabel();
            toConceptLabel.setText("To Concept:");
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
            gridBagConstraints.insets = new Insets(5, 20, 5, 0);
            gridBagConstraints.gridy = 0;
            linkNameLabel = new JLabel();
            linkNameLabel.setText("Link Name:");
            editorPanel = new JPanel();
            editorPanel.setLayout(new GridBagLayout());
            editorPanel.add(linkNameLabel, gridBagConstraints);
            editorPanel.add(toConceptLabel, gridBagConstraints1);
            editorPanel.add(linkValueLabel, gridBagConstraints2);
            editorPanel.add(getLinkNameTextField(), gridBagConstraints3);
            editorPanel.add(getToConceptComboBox(), gridBagConstraints4);
            editorPanel.add(getScrollPane(), gridBagConstraints5);
        }
        return editorPanel;
    }


    /**
     * This method initializes buttonPanel	
     *
     * @return javax.swing.JPanel
     */
    private OkCancelButtonPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new OkCancelButtonPanel();
            JButton okButton = buttonPanel.getOkButton();

            /*
             * Handle an OK click
             */
            okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    link.setLinkName(getLinkNameTextField().getText());
                    link.setLinkValue(getLinkValueTextArea().getText());
                    link.setToConcept((String) getToConceptComboBox().getSelectedItem());
                    onOkClick();
                    setLink(null);
                    setVisible(false);
                }
            });

            /*
             * Handle a cancel click
             */
            final JButton cancelButton = buttonPanel.getCancelButton();
            cancelButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    onCancelClick();
                    setLink(null);
                    setVisible(false);
                }
            });


        }
        return buttonPanel;
    }

    /**
     * When the OK button is clicked the fields of the ILink are updated. If further
     * action is needed then override this method. okOkClick is called after the fields
     * of the link have been set
     */
    public void onOkClick() {
        // By default do nothing
    }

    /**
     * When the Cancel button is clicked the dialog is hidden. If further action is
     * needed the override this method.
     */
    public void onCancelClick() {
        // by default do nothing
    }


    /**
     * This method initializes linkNameTextField	
     *
     * @return javax.swing.JTextField
     */
    private JTextField getLinkNameTextField() {
        if (linkNameTextField == null) {
            linkNameTextField = new JTextField();
        }
        return linkNameTextField;
    }

    /**
     * This method initializes toConceptComboBox	
     *
     * @return javax.swing.JComboBox
     */
    public HierachicalConceptNameComboBox getToConceptComboBox() {
        if (toConceptComboBox == null) {
            toConceptComboBox = new HierachicalConceptNameComboBox();
        }
        return toConceptComboBox;
    }

    /**
     * This method initializes scrollPane	
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane();
            scrollPane.setViewportView(getLinkValueTextArea());
        }
        return scrollPane;
    }

    /**
     * This method initializes linkValueTextArea	
     *
     * @return javax.swing.JTextArea
     */
    private JTextArea getLinkValueTextArea() {
        if (linkValueTextArea == null) {
            linkValueTextArea = new JTextArea();
        }
        return linkValueTextArea;
    }

    public boolean isLocked() {
        // TODO Auto-generated method stub
        return false;
    }

    public void setLocked(boolean locked) {
        // TODO Auto-generated method stub

    }

}
