/*
 * @(#)LinkEditorDialog.java   2009.10.09 at 11:27:50 PDT
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

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashSet;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.bushe.swing.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.ILink;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptNameTypes;
import vars.knowledgebase.LinkTemplate;
import vars.knowledgebase.SimpleConceptBean;
import vars.knowledgebase.SimpleConceptNameBean;
import vars.shared.ui.ILockableEditor;
import vars.knowledgebase.ui.Lookup;
import vars.knowledgebase.ui.ToolBelt;
import vars.shared.ui.HierachicalConceptNameComboBox;
import vars.shared.ui.OkCancelButtonPanel;

/**
 * @author brian
 *
 */
public class LinkEditorDialog extends JDialog implements ILockableEditor {

    private static final Concept SELF_CONCEPT = new SimpleConceptBean(new SimpleConceptNameBean("self",
        ConceptNameTypes.PRIMARY.toString()));
    private static final Concept NIL_CONCEPT = new SimpleConceptBean(new SimpleConceptNameBean(ILink.VALUE_NIL,
        ConceptNameTypes.PRIMARY.toString()));
    private static final Logger log = LoggerFactory.getLogger(LinkEditorDialog.class);
    private static final long serialVersionUID = 1L;
    private OkCancelButtonPanel buttonPanel = null;
    private JPanel editorPanel = null;
    private JPanel jContentPane = null;
    private JLabel linkNameLabel = null;
    private JTextField linkNameTextField = null;
    private JLabel linkValueLabel = null;
    private JTextArea linkValueTextArea = null;
    private JScrollPane scrollPane = null;
    private HierachicalConceptNameComboBox toConceptComboBox = null;
    private JLabel toConceptLabel = null;
    private ILink EMPTY_LINK = new ILink() {

        public String getFromConcept() {
            return null;
        }

        public String getLinkName() {
            return VALUE_NIL;
        }

        public String getLinkValue() {
            return VALUE_NIL;
        }

        public String getToConcept() {
            return VALUE_NIL;
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

        public String stringValue() {
            return VALUE_NIL + DELIMITER + getLinkName() + DELIMITER + getToConcept() + DELIMITER + getLinkValue();
        }

    };
    private final ToolBelt toolBelt;
    private final KnowledgebaseDAOFactory knowledgebaseDAOFactory;
    private ILink link;

    /**
     *
     * @param knowledgebaseDAOFactory
     * @throws HeadlessException
     */
    public LinkEditorDialog(ToolBelt toolBelt) throws HeadlessException {
        super();
        this.toolBelt = toolBelt;
        this.knowledgebaseDAOFactory = toolBelt.getKnowledgebaseDAOFactory();
        setModal(true);
        initialize();
    }

    /**
     * @param owner
     * @param knowledgebaseDAOFactory
     * @throws HeadlessException
     */
    public LinkEditorDialog(Frame owner, ToolBelt toolBelt) throws HeadlessException {
        super(owner);
        this.toolBelt = toolBelt;
        this.knowledgebaseDAOFactory = toolBelt.getKnowledgebaseDAOFactory();
        setModal(true);
        initialize();
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
            cancelButton.addActionListener(new ActionListener() {

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

    public ILink getLink() {
        return link;
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
     * This method initializes toConceptComboBox
     *
     * @return javax.swing.JComboBox
     */
    public HierachicalConceptNameComboBox getToConceptComboBox() {
        if (toConceptComboBox == null) {
            toConceptComboBox = new HierachicalConceptNameComboBox(toolBelt.getAnnotationPersistenceService());
        }

        return toConceptComboBox;
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

    public boolean isLocked() {

        // TODO Auto-generated method stub
        return false;
    }

    /**
     * When the Cancel button is clicked the dialog is hidden. If further action is
     * needed the override this method.
     */
    public void onCancelClick() {

        // by default do nothing
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
        Collection<LinkTemplate> matchingLinkTemplates = null;
        try {
            matchingLinkTemplates = knowledgebaseDAOFactory.newLinkTemplateDAO().findAllByLinkName(link.getLinkName());
        }
        catch (Exception e) {
            log.error("Failed to lookup LinkTemplates with linkName = " + link.getLinkName(), e);
            matchingLinkTemplates = new HashSet<LinkTemplate>();
        }

        /*
        * Get the toConceptAsString that's used. It will be a child of the toConceptAsString in the LinkTemplate
        */
        String toConceptAsString = null;
        if (matchingLinkTemplates.isEmpty()) {
            EventBus.publish(Lookup.TOPIC_WARNING, "Unable to find a LinkTemplate that matches '" + link + "'");
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
        else if (toConceptAsString.equalsIgnoreCase(ILink.VALUE_NIL)) {
            concept = NIL_CONCEPT;
            selectedConcept = NIL_CONCEPT;
            cb.addItem(NIL_CONCEPT.getPrimaryConceptName());
        }
        else {
            try {
                ConceptDAO conceptDAO = knowledgebaseDAOFactory.newConceptDAO();
                concept = conceptDAO.findByName(toConceptAsString);
                selectedConcept = conceptDAO.findByName(link.getToConcept());
                cb.setConcept(concept);
            }
            catch (Exception e) {
                log.error("", e);
                EventBus.publish(Lookup.TOPIC_WARNING, "A database error occurred. Try refreshing the knowledgebase");
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

    public void setLocked(boolean locked) {

        // TODO Auto-generated method stub

    }
}
