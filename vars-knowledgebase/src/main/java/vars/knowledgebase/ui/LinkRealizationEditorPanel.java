/*
 * @(#)LinkRealizationEditorPanel.java   2009.10.26 at 10:54:16 PDT
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

import foxtrot.Job;
import foxtrot.Worker;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.bushe.swing.event.EventBus;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.swing.ListListModel;
import org.mbari.swing.SpinningDialWaitIndicator;
import org.mbari.swing.WaitIndicator;
import org.mbari.util.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.DAO;
import vars.ILink;
import vars.LinkBean;
import vars.UserAccount;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptMetadata;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameTypes;
import vars.knowledgebase.History;
import vars.knowledgebase.LinkRealization;
import vars.knowledgebase.LinkRealizationDAO;
import vars.knowledgebase.LinkTemplate;
import vars.knowledgebase.LinkTemplateDAO;
import vars.knowledgebase.SimpleConceptBean;
import vars.knowledgebase.SimpleConceptNameBean;
import vars.knowledgebase.ui.dialogs.AddLinkRealizationDialog;
import vars.knowledgebase.ui.dialogs.LinkEditorDialog;
import vars.shared.ui.HierachicalConceptNameComboBox;

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
    private static final ILink NIL_LINKREALIZATION = new LinkBean(ILink.VALUE_NIL, ILink.VALUE_NIL, ILink.VALUE_NIL);
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
    private JPanel propertiesPanel = null;
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
    public LinkRealizationEditorPanel(Toolbelt toolBelt) {
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
            gridBagConstraints11.anchor = GridBagConstraints.NORTHWEST;
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
            gridBagConstraints7.weightx = 1.0;
            gridBagConstraints7.anchor = GridBagConstraints.WEST;
            gridBagConstraints7.insets = new Insets(0, 0, 5, 20);
            gridBagConstraints7.gridx = 1;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = GridBagConstraints.BOTH;
            gridBagConstraints6.gridy = 0;
            gridBagConstraints6.weightx = 1.0;
            gridBagConstraints6.anchor = GridBagConstraints.WEST;
            gridBagConstraints6.insets = new Insets(5, 0, 5, 20);
            gridBagConstraints6.gridx = 1;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.anchor = GridBagConstraints.WEST;
            gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.insets = new Insets(0, 20, 0, 10);
            gridBagConstraints5.gridy = 2;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.anchor = GridBagConstraints.WEST;
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.insets = new Insets(0, 20, 0, 10);
            gridBagConstraints4.gridy = 1;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.anchor = GridBagConstraints.WEST;
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.insets = new Insets(0, 20, 0, 10);
            gridBagConstraints3.gridy = 0;
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

    /**
     * This method initializes linkField
     *
     * @return javax.swing.JTextField
     */
    private JTextField getLinkField() {
        if (linkField == null) {
            linkField = new JTextField();
        }

        return linkField;
    }

    /**
     * This method initializes linkList
     *
     * @return javax.swing.JList
     */
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
        }

        return linkList;
    }

    /**
     * This method initializes linkValueScrollPane
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getLinkValueScrollPane() {
        if (linkValueScrollPane == null) {
            linkValueScrollPane = new JScrollPane();
            linkValueScrollPane.setViewportView(getLinkValueTextArea());
        }

        return linkValueScrollPane;
    }

    /**
     * This method initializes linkValueTextArea
     *
     * @return javax.swing.JTextArea
     */
    private JTextArea getLinkValueTextArea() {
        if (linkValueTextArea == null) {
            linkValueTextArea = new JTextArea();
            linkValueTextArea.setPreferredSize(new Dimension(300, 48));
        }

        return linkValueTextArea;
    }

    private ListListModel getListModel() {
        if (listModel == null) {
            List<LinkRealization> list = Collections.synchronizedList(new ArrayList<LinkRealization>());
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

    /**
     * This method initializes propertiesPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getPropertiesPanel() {
        if (propertiesPanel == null) {
            propertiesPanel = new JPanel();
            propertiesPanel.setLayout(new BorderLayout());
            propertiesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Descriptions",
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            propertiesPanel.add(getScrollPane(), BorderLayout.CENTER);
            propertiesPanel.add(getLinkEditorPanel(), BorderLayout.SOUTH);
        }

        return propertiesPanel;
    }

    /**
     * This method initializes scrollPane
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane();
            scrollPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            scrollPane.setViewportView(getLinkList());
            scrollPane.setViewportView(getLinkList());
        }

        return scrollPane;
    }

    /**
     * This method initializes toConceptComboBox
     *
     * @return javax.swing.JComboBox
     */
    private HierachicalConceptNameComboBox getToConceptComboBox() {
        if (toConceptComboBox == null) {
            toConceptComboBox = new HierachicalConceptNameComboBox(
                getToolBelt().getKnowledgebaseDAOFactory().newConceptDAO());
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
            Concept toConcept = link.getConceptMetadata().getConcept();
            LinkTemplateDAO linkTemplateDAO = getToolBelt().getKnowledgebaseDAOFactory().newLinkTemplateDAO();
            Collection<LinkTemplate> matchingLinkTemplates = linkTemplateDAO.findAllByLinkName(link.getLinkName(),
                toConcept);

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
            WaitIndicator waitIndicator = new SpinningDialWaitIndicator(this);
            final String theToConcept = toConceptAsString;
            Worker.post(new Job() {

                public Object run() {
                    Concept concept = null;
                    Concept selectedConcept = null;
                    HierachicalConceptNameComboBox cb = getToConceptComboBox();
                    cb.removeAllItems();

                    if (theToConcept.equalsIgnoreCase(ILink.VALUE_SELF)) {
                        concept = SELF_CONCEPT;
                        selectedConcept = SELF_CONCEPT;
                        cb.addItem(SELF_CONCEPT.getPrimaryConceptName());
                    }
                    else if (theToConcept.equalsIgnoreCase(ILink.VALUE_NIL)) {
                        concept = NIL_CONCEPT;
                        selectedConcept = NIL_CONCEPT;
                        cb.addItem(NIL_CONCEPT.getPrimaryConceptName());
                    }
                    else {
                        try {
                            ConceptDAO conceptDAO = getToolBelt().getKnowledgebaseDAOFactory().newConceptDAO();
                            concept = conceptDAO.findByName(theToConcept);
                            selectedConcept = conceptDAO.findByName(link.getToConcept());
                            cb.setConcept(concept);    // TODO app hangs up here. Need to optimize
                        }
                        catch (Exception e) {
                            log.error("", e);
                            EventBus.publish(Lookup.TOPIC_WARNING,
                                             "A database error occurred. Try refreshing the knowledgebase");
                            concept = NIL_CONCEPT;
                            selectedConcept = NIL_CONCEPT;
                            cb.addItem(NIL_CONCEPT.getPrimaryConceptName());
                        }
                    }

                    cb.setSelectedItem(selectedConcept.getPrimaryConceptName());

                    return null;    // TODO Verify this default implementation is correct
                }
            });
            waitIndicator.dispose();
        }

        if (log.isDebugEnabled()) {
            log.debug("Update with " + link + " is complete");
        }
    }

    private class ANewAction extends ActionAdapter {

        private final LinkEditorDialog dialog = new MyLinkEditorDialog();

        /**
         * Show the dialog. The dialog  is used to set the properties of the link
         */
        @Override
        public void doAction() {
            final UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();
            if ((userAccount != null) && !userAccount.isReadOnly()) {

                LinkTemplate linkTemplate = (LinkTemplate) getLinkList().getSelectedValue();
                LinkRealization linkRealization = getToolBelt().getKnowledgebaseFactory().newLinkRealization();
                linkRealization.setLinkName(linkTemplate.getLinkName());
                linkRealization.setLinkValue(linkTemplate.getLinkValue());
                linkRealization.setToConcept(linkTemplate.getToConcept());

                // Show new dialog
                dialog.setLink(linkRealization);
                dialog.setVisible(true);
            }
        }

        /**
                 * Class that adds some functionality to a LinkEditorDialog when it's
                 * okbutton is clicked.
                 */
        private final class MyLinkEditorDialog extends LinkEditorDialog {

            MyLinkEditorDialog() {
                super((Frame) Lookup.getApplicationFrameDispatcher().getValueObject(),
                      getToolBelt().getKnowledgebaseDAOFactory());
                getLinkField().setEditable(false);
                setLocationRelativeTo((Frame) Lookup.getApplicationFrameDispatcher().getValueObject());
                this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
            }

            @Override
            public void onOkClick() {

                /*
                 * The dialog handles setting the link propertes. We just have to
                 * save it in this method.
                 */
                Concept concept = getConcept();
                LinkRealization linkRealization = (LinkRealization) getLink();
                concept.getConceptMetadata().addLinkRealization(linkRealization);

                try {
                    LinkRealizationDAO linkRealizationDAO = getToolBelt().getKnowledgebaseDAOFactory()
                        .newLinkRealizationDAO();
                    linkRealizationDAO.persist(linkRealization);
                }
                catch (Exception e) {
                    concept.getConceptMetadata().removeLinkRealization(linkRealization);
                    log.error("Database transaction failed", e);
                    EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR,
                                     "Failed to update " + concept + ". Rolling back changes.");
                }
            }
        }
    }


    private class DeleteAction extends ActionAdapter {

        @Override
        public void doAction() {
            final UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();
            if ((userAccount != null) && !userAccount.isReadOnly()) {

                JList linkList = getLinkList();
                LinkRealization linkRealization = (LinkRealization) linkList.getSelectedValue();
                if (linkRealization != null) {
                    History history = getToolBelt().getHistoryFactory().delete(userAccount, linkRealization);

                    DAO dao = getToolBelt().getKnowledgebaseDAOFactory().newDAO();
                    dao.startTransaction();
                    linkRealization = dao.merge(linkRealization);
                    ConceptMetadata conceptDelegate = linkRealization.getConceptMetadata();
                    conceptDelegate.addHistory(history);
                    dao.persist(history);
                    dao.endTransaction();

                    EventBus.publish(Lookup.TOPIC_APPROVE_HISTORY, history);

                    /*
                     * Trigger a redraw
                     */
                    Dispatcher dispatcher = Dispatcher.getDispatcher(Concept.class);
                    dispatcher.setValueObject(null);
                    dispatcher.setValueObject(conceptDelegate.getConcept());
                }
            }
        }
    }


    private class NewAction extends ActionAdapter {

        private final AddLinkRealizationDialog dialog = new AddLinkRealizationDialog(
            (Frame) Lookup.getApplicationFrameDispatcher().getValueObject(), getToolBelt());

        public void doAction() {
            dialog.setConcept(getConcept());
            dialog.setVisible(true);
        }
    }


    private class UpdateAction extends ActionAdapter {

        @Override
        public void doAction() {
            final UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();
            if ((userAccount != null) && !userAccount.isReadOnly()) {
                JList linkList = getLinkList();
                LinkRealization linkRealization = (LinkRealization) linkList.getSelectedValue();


                String name = ((ConceptName) getToConceptComboBox().getSelectedItem()).getName();
                if (ILink.VALUE_NIL.equalsIgnoreCase(name) || ILink.VALUE_SELF.equalsIgnoreCase(name)) {

                    // Do nothing
                }
                else {
                    Concept concept = null;
                    try {
                        ConceptDAO conceptDAO = getToolBelt().getKnowledgebaseDAOFactory().newConceptDAO();
                        concept = conceptDAO.findByName(name);

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
                linkRealization = dao.merge(linkRealization);
                linkRealization.setLinkName(getLinkField().getText());
                linkRealization.setToConcept(name);
                linkRealization.setLinkValue(getLinkValueTextArea().getText());

                // Generate the appropriate history object
                History history = getToolBelt().getHistoryFactory().replaceLinkRealization(userAccount, oldValue,
                    linkRealization);
                linkRealization.getConceptMetadata().addHistory(history);
                dao.persist(history);
                dao.endTransaction();

                EventBus.publish(Lookup.TOPIC_APPROVE_HISTORY, history);

                /*
                 * Trigger a redraw
                 */
                Dispatcher dispatcher = Lookup.getSelectedConceptDispatcher();
                dispatcher.setValueObject(null);
                dispatcher.setValueObject(linkRealization.getConceptMetadata().getConcept());
            }
        }
    }
}
