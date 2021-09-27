/*
 * @(#)LinkRealizationEditorPanel.java   2010.05.05 at 01:59:46 PDT
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



package vars.knowledgebase.ui;

import foxtrot.Job;
import foxtrot.Worker;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.bushe.swing.event.EventBus;
import mbarix4j.awt.event.ActionAdapter;
import mbarix4j.swing.ListListModel;
import mbarix4j.swing.SpinningDialWaitIndicator;
import mbarix4j.swing.WaitIndicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.DAO;
import vars.ILink;
import vars.UserAccount;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptMetadata;
import vars.knowledgebase.ConceptNameTypes;
import vars.knowledgebase.History;
import vars.knowledgebase.LinkRealization;
import vars.knowledgebase.LinkRealizationDAO;
import vars.knowledgebase.LinkTemplate;
import vars.knowledgebase.LinkTemplateDAO;
import vars.knowledgebase.SimpleConceptBean;
import vars.knowledgebase.SimpleConceptNameBean;
import vars.knowledgebase.ui.dialogs.AddLinkRealizationDialog;
//import vars.knowledgebase.ui.dialogs.LinkEditorDialog;
import vars.shared.ui.HierachicalConceptNameComboBox;
import vars.shared.ui.LinkListCellRenderer;

/**
 * Class description
 *
 *
 * @version        $date$, 2009.10.26 at 10:54:16 PDT
 * @author         Brian Schlining [brian@mbari.org]
 */
public class LinkRealizationEditorPanel extends EditorPanel {

    private static final Logger log = LoggerFactory.getLogger(LinkRealizationEditorPanel.class);
    private static final Concept SELF_CONCEPT = new SimpleConceptBean(new SimpleConceptNameBean(ILink.VALUE_SELF,
        ConceptNameTypes.PRIMARY.toString()));
    private static final Concept NIL_CONCEPT = new SimpleConceptBean(new SimpleConceptNameBean(ILink.VALUE_NIL,
        ConceptNameTypes.PRIMARY.toString()));
    private EditorButtonPanel editorButtonPanel = null;
    private JPanel linkEditorPanel = null;
    private JTextField linkField = null;
    private JLabel linkLabel = null;
    private JList linkList = null;
    private JScrollPane linkValueScrollPane = null;
    private JTextArea linkValueTextArea = null;
    private ListListModel listModel = null;
    private JSplitPane propertiesPanel = null;
    private JScrollPane scrollPane = null;
    private HierachicalConceptNameComboBox toConceptComboBox = null;
    private JLabel toConceptLabel = null;
    private JLabel valueLabel = null;
    private DeleteAction deleteAction;
    private NewAction newAction;
    private UpdateAction updateAction;

    /**
     * Constructs ...
     *
     * @param toolBelt
     */
    public LinkRealizationEditorPanel(ToolBelt toolBelt) {
        super(toolBelt);
        initialize();
        setLocked(isLocked());
    }

    DeleteAction getDeleteAction() {
        if (deleteAction == null) {
            deleteAction = new DeleteAction();
        }

        return deleteAction;
    }

    /**
     * This method initializes buttonPanel
     *
     * @return javax.swing.JPanel
     */
    private EditorButtonPanel getEditorButtonPanel() {
        if (editorButtonPanel == null) {
            editorButtonPanel = new EditorButtonPanel();
            editorButtonPanel.getDeleteButton().addActionListener(getDeleteAction());
            editorButtonPanel.getNewButton().addActionListener(getNewAction());
            editorButtonPanel.getUpdateButton().addActionListener(getUpdateAction());
        }

        return editorButtonPanel;
    }

    /**
     * This method initializes linkEditorPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getLinkEditorPanel() {
        if (linkEditorPanel == null) {
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.fill = GridBagConstraints.BOTH;
            gridBagConstraints11.weighty = 1.0;
            gridBagConstraints11.gridx = 1;
            gridBagConstraints11.gridy = 2;
            gridBagConstraints11.gridheight = 1;
            //gridBagConstraints11.anchor = GridBagConstraints.NORTHWEST;
            gridBagConstraints11.insets = new Insets(0, 0, 5, 20);
            gridBagConstraints11.weightx = 1.0;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.insets = new Insets(0, 20, 5, 5);
            gridBagConstraints2.anchor = GridBagConstraints.NORTHWEST;
            gridBagConstraints2.gridy = 2;
            valueLabel = new JLabel();
            valueLabel.setText("Value:");
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.anchor = GridBagConstraints.WEST;
            gridBagConstraints1.insets = new Insets(0, 20, 5, 5);
            gridBagConstraints1.gridy = 1;
            toConceptLabel = new JLabel();
            toConceptLabel.setText("To Concept:");
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new Insets(5, 20, 5, 5);
            gridBagConstraints.gridy = 0;
            linkLabel = new JLabel();
            linkLabel.setText("Link:");
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.fill = GridBagConstraints.BOTH;
            gridBagConstraints7.gridy = 1;
            gridBagConstraints7.anchor = GridBagConstraints.WEST;
            gridBagConstraints7.insets = new Insets(0, 0, 5, 20);
            gridBagConstraints7.gridx = 1;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = GridBagConstraints.BOTH;
            gridBagConstraints6.gridy = 0;
            gridBagConstraints6.anchor = GridBagConstraints.WEST;
            gridBagConstraints6.insets = new Insets(5, 0, 5, 20);
            gridBagConstraints6.gridx = 1;
            linkEditorPanel = new JPanel();
            linkEditorPanel.setLayout(new GridBagLayout());
            linkEditorPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            linkEditorPanel.add(getLinkField(), gridBagConstraints6);
            linkEditorPanel.add(getToConceptComboBox(), gridBagConstraints7);
            linkEditorPanel.add(linkLabel, gridBagConstraints);
            linkEditorPanel.add(toConceptLabel, gridBagConstraints1);
            linkEditorPanel.add(valueLabel, gridBagConstraints2);
            linkEditorPanel.add(getLinkValueScrollPane(), gridBagConstraints11);
        }

        return linkEditorPanel;
    }

    private JTextField getLinkField() {
        if (linkField == null) {
            linkField = new JTextField();
        }

        return linkField;
    }

    private JList getLinkList() {
        if (linkList == null) {
            linkList = new JList();
            linkList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            linkList.setModel(getListModel());

            /*
             * Add a listener to update the UI when a link is selected
             */
            linkList.addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    final LinkRealization link = (LinkRealization) linkList.getSelectedValue();
                    updateUIWithSelectedLink(link);
                }
            });

            /*
             * Add a listener to toggle button state when items in the list are selected/unselected
             */
            linkList.addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    final LinkRealization link = (LinkRealization) linkList.getSelectedValue();
                    boolean enable = !isLocked() && (link != null);
                    getEditorButtonPanel().getDeleteButton().setEnabled(enable);
                    getEditorButtonPanel().getUpdateButton().setEnabled(enable);
                }
            });

            linkList.setCellRenderer(new LinkListCellRenderer());
        }

        return linkList;
    }

    private JScrollPane getLinkValueScrollPane() {
        if (linkValueScrollPane == null) {
            linkValueScrollPane = new JScrollPane(getLinkValueTextArea(), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            //linkValueScrollPane.setPreferredSize(getLinkValueTextArea().getPreferredScrollableViewportSize());
        }

        return linkValueScrollPane;
    }


    private JTextArea getLinkValueTextArea() {
        if (linkValueTextArea == null) {
            linkValueTextArea = new JTextArea();
            linkValueTextArea.setPreferredSize(new Dimension(300, 48));
            linkValueTextArea.setLineWrap(true);
        }

        return linkValueTextArea;
    }

    private ListListModel getListModel() {
        if (listModel == null) {
            List<LinkRealization> list = new Vector<LinkRealization>();
            listModel = new ListListModel(list);
        }

        return listModel;
    }

    NewAction getNewAction() {
        if (newAction == null) {
            newAction = new NewAction();
        }

        return newAction;
    }

    private JSplitPane getPropertiesPanel() {
        if (propertiesPanel == null) {
            propertiesPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getScrollPane(), getLinkEditorPanel());
            propertiesPanel.setDividerLocation(0.25);
            propertiesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Descriptions",
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));

        }

        return propertiesPanel;
    }

    private JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane();
            scrollPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            scrollPane.setViewportView(getLinkList());
        }

        return scrollPane;
    }

    private HierachicalConceptNameComboBox getToConceptComboBox() {
        if (toConceptComboBox == null) {
            toConceptComboBox = new HierachicalConceptNameComboBox(getToolBelt().getAnnotationPersistenceService());
        }

        return toConceptComboBox;
    }

    UpdateAction getUpdateAction() {
        if (updateAction == null) {
            updateAction = new UpdateAction();
        }

        return updateAction;
    }

    private void initialize() {
        this.setLayout(new BorderLayout());
        this.add(getPropertiesPanel(), BorderLayout.CENTER);
        this.setMinimumSize(new Dimension(100, 100));
        this.add(getEditorButtonPanel(), BorderLayout.SOUTH);
    }

    /**
     *
     * @param concept
     */
    @Override
    public void setConcept(Concept concept) {
        getLinkList().clearSelection();
        super.setConcept(concept);
        listModel.clear();

        if (concept != null) {
            ListListModel localListModel = getListModel();
            localListModel.addAll(concept.getConceptMetadata().getLinkRealizations());
        }
    }

    /**
     *
     * @param locked
     */
    @Override
    public void setLocked(boolean locked) {
        super.setLocked(locked);
        getEditorButtonPanel().getNewButton().setEnabled(!locked);
        boolean enable = !locked && (getLinkList().getSelectedValue() != null);

        getEditorButtonPanel().getUpdateButton().setEnabled(enable);
        getEditorButtonPanel().getDeleteButton().setEnabled(enable);
        getLinkField().setEnabled(enable);
        getToConceptComboBox().setEnabled(enable);
        getLinkValueTextArea().setEnabled(enable);
    }

    /**
     * Updates the UI when a Link is selected in the toConceptComboBox
     *
     * @param link The link that was selected in the toConceptComboBox
     */
    private void updateUIWithSelectedLink(final LinkRealization link) {

        if (log.isDebugEnabled()) {
            log.debug("Updating with " + link);
        }

        enableEditor:
        {
            boolean enable = !isLocked() && (link != null);
            getLinkField().setEnabled(enable);
            getLinkValueTextArea().setEnabled(enable);
            getToConceptComboBox().setEnabled(enable);
        }

        handleNullArg:
        {
            if (link == null) {
                getLinkField().setText("");
                getLinkValueTextArea().setText("");
                getToConceptComboBox().removeAllItems();

                return;
            }
        }

        updateTextFields:
        {
            getLinkField().setText(link.getLinkName());
            getLinkValueTextArea().setText(link.getLinkValue());
        }

        updateToConceptComboBox:
        {

            /*
             * Find the LinkTemplate that the LinkRealization is based on.
             */

            // DAOTX
            LinkTemplateDAO linkTemplateDAO = getToolBelt().getKnowledgebaseDAOFactory().newLinkTemplateDAO();
            linkTemplateDAO.startTransaction();
            LinkRealization linkRealization = linkTemplateDAO.find(link);
            Concept toConcept = linkRealization.getConceptMetadata().getConcept();
            Collection<LinkTemplate> matchingLinkTemplates = linkTemplateDAO.findAllByLinkName(link.getLinkName(),
                toConcept);
            linkTemplateDAO.endTransaction();
            linkTemplateDAO.close();


            /*
             * Get the toConceptAsString that's used. It will be a child of the toConceptAsString in the LinkTemplate
             */
            String toConceptAsString = null;
            if (matchingLinkTemplates.isEmpty()) {
                EventBus.publish(StateLookup.TOPIC_WARNING, "Unable to find a LinkTemplate that matches '" + link + "'");
                toConceptAsString = link.getToConcept();
            }
            else {
                ILink matchingLink = (ILink) matchingLinkTemplates.iterator().next();
                toConceptAsString = matchingLink.getToConcept();
            }

            WaitIndicator waitIndicator = new SpinningDialWaitIndicator(this);
            final String fToConceptAsString = toConceptAsString;
            Worker.post(new Job() {

                public Object run() {
                    Concept concept = null;
                    Concept selectedConcept = null;
                    final HierachicalConceptNameComboBox cb = getToConceptComboBox();
                    cb.removeAllItems();

                    if (fToConceptAsString.equalsIgnoreCase(ILink.VALUE_SELF)) {
                        concept = SELF_CONCEPT;
                        selectedConcept = SELF_CONCEPT;
                        cb.addItem(SELF_CONCEPT.getPrimaryConceptName());
                    }
                    else if (fToConceptAsString.equalsIgnoreCase(ILink.VALUE_NIL)) {
                        concept = NIL_CONCEPT;
                        selectedConcept = NIL_CONCEPT;
                        cb.addItem(NIL_CONCEPT.getPrimaryConceptName());
                    }
                    else {
                        try {

                            // DAOTX
                            ConceptDAO conceptDAO = getToolBelt().getKnowledgebaseDAOFactory().newConceptDAO();
                            conceptDAO.startTransaction();
                            concept = conceptDAO.findByName(fToConceptAsString);
                            selectedConcept = conceptDAO.findByName(link.getToConcept());
                            conceptDAO.endTransaction();
                            conceptDAO.close();
                            cb.setConcept(concept);
                        }
                        catch (Exception e) {
                            log.error("", e);
                            EventBus.publish(StateLookup.TOPIC_WARNING,
                                             "A database error occurred. Try refreshing the knowledgebase");
                            concept = NIL_CONCEPT;
                            selectedConcept = NIL_CONCEPT;
                            cb.addItem(NIL_CONCEPT.getPrimaryConceptName());
                        }
                    }

                    final Concept fSelectedConcept = selectedConcept;
                    SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                            cb.setSelectedItem(fSelectedConcept.getPrimaryConceptName().getName());
                            cb.repaint();
                        }

                    });


                    return null;    // TODO Verify this default implementation is correct
                }
            });
            waitIndicator.dispose();
        }

        log.debug("Update with {} is complete", link);

    }


    private class DeleteAction extends ActionAdapter {

        /**
         */
        @Override
        public void doAction() {
            final UserAccount userAccount = StateLookup.getUserAccount();
            if ((userAccount != null) && !userAccount.isReadOnly()) {

                JList linkList = getLinkList();
                LinkRealization linkRealization = (LinkRealization) linkList.getSelectedValue();
                if (linkRealization != null) {
                    History history = getToolBelt().getHistoryFactory().delete(userAccount, linkRealization);

                    DAO dao = getToolBelt().getKnowledgebaseDAOFactory().newDAO();
                    dao.startTransaction();
                    linkRealization = dao.find(linkRealization);
                    ConceptMetadata conceptDelegate = linkRealization.getConceptMetadata();
                    conceptDelegate.addHistory(history);
                    dao.persist(history);
                    dao.endTransaction();
                    dao.close();

                    EventBus.publish(StateLookup.TOPIC_APPROVE_HISTORY, history);
                }
            }
        }
    }


    private class NewAction extends ActionAdapter {

        private final AddLinkRealizationDialog dialog = new AddLinkRealizationDialog(StateLookup.getApplicationFrame(),
            getToolBelt());

        /**
         */
        public void doAction() {
            dialog.setConcept(getConcept());
            dialog.setVisible(true);
        }
    }


    private class UpdateAction extends ActionAdapter {

        /**
         */
        @Override
        public void doAction() {
            final UserAccount userAccount = StateLookup.getUserAccount();
            if ((userAccount != null) && !userAccount.isReadOnly()) {
                JList linkList = getLinkList();
                LinkRealization linkRealization = (LinkRealization) linkList.getSelectedValue();


                String name = (String) getToConceptComboBox().getSelectedItem();
                if (ILink.VALUE_NIL.equalsIgnoreCase(name) || ILink.VALUE_SELF.equalsIgnoreCase(name)) {

                    // Do nothing
                }
                else {
                    Concept concept = null;
                    try {
                        ConceptDAO conceptDAO = getToolBelt().getKnowledgebaseDAOFactory().newConceptDAO();
                        concept = conceptDAO.findByName(name);
                        conceptDAO.close();

                        if (concept != null) {
                            name = concept.getPrimaryConceptName().toString();
                        }
                    }
                    catch (Exception e) {
                        log.error("Unable to find Concept '" + name + "' in the database");
                    }
                }

                // Create a copy of the old values to create a history
                LinkRealization oldValue = getToolBelt().getKnowledgebaseFactory().newLinkRealization();
                oldValue.setLinkName(linkRealization.getLinkName());
                oldValue.setToConcept(linkRealization.getToConcept());
                oldValue.setLinkValue(linkRealization.getLinkValue());

                //. Update the current linkRealization
                DAO dao = getToolBelt().getKnowledgebaseDAOFactory().newDAO();
                dao.startTransaction();
                linkRealization = dao.find(linkRealization);
                linkRealization.setLinkName(getLinkField().getText());
                linkRealization.setToConcept(name);
                linkRealization.setLinkValue(getLinkValueTextArea().getText());

                // Generate the appropriate history object
                History history = getToolBelt().getHistoryFactory().replaceLinkRealization(userAccount, oldValue,
                    linkRealization);
                linkRealization.getConceptMetadata().addHistory(history);
                dao.persist(history);
                dao.endTransaction();
                dao.close();

                EventBus.publish(StateLookup.TOPIC_APPROVE_HISTORY, history);

            }
        }
    }
}
