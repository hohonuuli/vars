package vars.knowledgebase.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.swing.ListListModel;
import org.mbari.swing.WaitIndicator;
import org.mbari.swing.SpinningDialWaitIndicator;
import org.mbari.vars.dao.DAOException;
import org.mbari.vars.knowledgebase.model.dao.KnowledgeBaseCache;
import org.mbari.vars.knowledgebase.model.dao.LinkTemplateDAO;
import org.mbari.vars.knowledgebase.model.dao.ConceptDAO;
import org.mbari.vars.knowledgebase.ui.actions.ApproveHistoryTask;
import org.mbari.vars.knowledgebase.ui.dialogs.LinkEditorDialog;
import org.mbari.vars.knowledgebase.ui.dialogs.AddLinkRealizationDialog;
import vars.ILink;
import org.mbari.vars.model.UserAccount;
import org.mbari.vars.query.ui.ConceptConstraints;
import org.mbari.vars.ui.HierachicalConceptNameComboBox;
import org.mbari.vars.util.AppFrameDispatcher;
import org.mbari.util.Dispatcher;
import org.mbari.vars.knowledgebase.model.Concept;
import org.mbari.vars.knowledgebase.model.ConceptName;
import org.mbari.vars.knowledgebase.model.HistoryFactory;
import org.mbari.vars.knowledgebase.model.LinkRealization;
import org.mbari.vars.knowledgebase.model.LinkTemplate;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import foxtrot.Worker;
import foxtrot.Job;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import vars.knowledgebase.IConcept;
import vars.knowledgebase.IConceptDelegate;
import vars.knowledgebase.IHistory;


public class LinkRealizationEditorPanel extends EditorPanel {

    private static final Logger log = LoggerFactory.getLogger(LinkRealizationEditorPanel.class);
    private EditorButtonPanel editorButtonPanel = null;
    private JList linkList = null;
    private JPanel propertiesPanel = null;
    private ListListModel listModel = null;
    private JScrollPane scrollPane = null;
    private JPanel linkEditorPanel = null;
    private JTextField linkField = null;
    private HierachicalConceptNameComboBox toConceptComboBox = null;
    private JLabel linkLabel = null;
    private JLabel toConceptLabel = null;
    private JLabel valueLabel = null;
    private static final Concept SELF_CONCEPT = new Concept(new ConceptName("self", ConceptName.NAMETYPE_PRIMARY), null);
    private static final LinkRealization NIL_LINKREALIZATION = new LinkRealization(ConceptConstraints.WILD_CARD_STRING, ConceptConstraints.WILD_CARD_STRING, ConceptConstraints.WILD_CARD_STRING);
    private static final Concept NIL_CONCEPT = new Concept(new ConceptName(ConceptConstraints.WILD_CARD_STRING, ConceptName.NAMETYPE_PRIMARY), null);
    private NewAction newAction;
    private UpdateAction updateAction;
    private DeleteAction deleteAction;
    private JScrollPane linkValueScrollPane = null;
    private JTextArea linkValueTextArea = null;

    public LinkRealizationEditorPanel() {
        initialize();
        setLocked(isLocked());
    }

    private void initialize() {
        this.setLayout(new BorderLayout());
        this.add(getPropertiesPanel(), BorderLayout.CENTER);
        this.setMinimumSize(new Dimension(100, 100));
        this.add(getEditorButtonPanel(), BorderLayout.SOUTH);
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

    private ListListModel getListModel() {
        if (listModel == null) {
            List<LinkRealization> list = Collections.synchronizedList(new ArrayList<LinkRealization>());
            listModel = new ListListModel(list);
        }
        return listModel;
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
            propertiesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Descriptions", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            propertiesPanel.add(getScrollPane(), BorderLayout.CENTER);
            propertiesPanel.add(getLinkEditorPanel(), BorderLayout.SOUTH);
        }
        return propertiesPanel;
    }

    public void setConcept(Concept concept) {
        getLinkList().clearSelection();
        super.setConcept(concept);
        listModel.clear();
        if (concept != null) {
            ListListModel listModel = getListModel();
            listModel.addAll(concept.getLinkRealizationSet());
        }
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
            boolean enable = !isLocked() && link != null;
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
            IConcept toConcept = link.getConceptDelegate().getConcept();
            Set<LinkTemplate> matchingLinkTemplates = LinkTemplateDAO.getInstance().findByLinkName((Concept) toConcept, link.getLinkName());

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
            WaitIndicator waitIndicator = new SpinningDialWaitIndicator(this);
            final String theToConcept = toConceptAsString;
            Worker.post(new Job() {

                public Object run() {
                    Concept concept = null;
                    Concept selectedConcept = null;
                    HierachicalConceptNameComboBox cb = getToConceptComboBox();
                    cb.removeAllItems();
                    if (theToConcept.equalsIgnoreCase("self")) {
                        concept = SELF_CONCEPT;
                        selectedConcept = SELF_CONCEPT;
                        cb.addItem(SELF_CONCEPT.getPrimaryConceptName());
                    }
                    else if (theToConcept.equalsIgnoreCase(ConceptConstraints.WILD_CARD_STRING)) {
                        concept = NIL_CONCEPT;
                        selectedConcept = NIL_CONCEPT;
                        cb.addItem(NIL_CONCEPT.getPrimaryConceptName());
                    }
                    else {
                        try {
                            concept = KnowledgeBaseCache.getInstance().findConceptByName(theToConcept);
                            selectedConcept = KnowledgeBaseCache.getInstance().findConceptByName(link.getToConcept());
                            cb.setConcept(concept); // TODO app hangs up here. Need to optimize
                        }
                        catch (DAOException e) {
                            log.error("", e);
                            AppFrameDispatcher.showWarningDialog("A database error occurred. Try refreshing the knowledgebase");
                            concept = NIL_CONCEPT;
                            selectedConcept = NIL_CONCEPT;
                            cb.addItem(NIL_CONCEPT.getPrimaryConceptName());
                        }
                    }

                    cb.setSelectedItem(selectedConcept.getPrimaryConceptName());
                    return null; // TODO Verify this default implementation is correct
                }
            });
            waitIndicator.dispose();
        }

        if (log.isDebugEnabled()) {
            log.debug("Update with " + link + " is complete");
        }
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
     * This method initializes toConceptComboBox
     *
     * @return javax.swing.JComboBox
     */
    private HierachicalConceptNameComboBox getToConceptComboBox() {
        if (toConceptComboBox == null) {
            toConceptComboBox = new HierachicalConceptNameComboBox();
        }
        return toConceptComboBox;
    }

    DeleteAction getDeleteAction() {
        if (deleteAction == null) {
            deleteAction = new DeleteAction();
        }
        return deleteAction;
    }

    NewAction getNewAction() {
        if (newAction == null) {
            newAction = new NewAction();
        }
        return newAction;
    }

    UpdateAction getUpdateAction() {
        if (updateAction == null) {
            updateAction = new UpdateAction();
        }
        return updateAction;
    }

    private class DeleteAction extends ActionAdapter {

        @Override
        public void doAction() {
            final UserAccount userAccount = (UserAccount) KnowledgebaseApp.DISPATCHER_USERACCOUNT.getValueObject();
            if (userAccount != null && !userAccount.isReadOnly()) {

                JList linkList = getLinkList();
                LinkRealization linkRealization = (LinkRealization) linkList.getSelectedValue();
                if (linkRealization != null) {
                    IHistory history = HistoryFactory.delete(userAccount, linkRealization);
                    IConceptDelegate conceptDelegate = linkRealization.getConceptDelegate();
                    conceptDelegate.addHistory(history);

                    if (userAccount.isAdmin()) {
                        ApproveHistoryTask.approve(userAccount, history);
                    }

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

    private class UpdateAction extends ActionAdapter {

        @Override
        public void doAction() {
            final UserAccount userAccount = (UserAccount) KnowledgebaseApp.DISPATCHER_USERACCOUNT.getValueObject();
            if (userAccount != null && !userAccount.isReadOnly()) {
                JList linkList = getLinkList();
                LinkRealization linkRealization = (LinkRealization) linkList.getSelectedValue();

                // Create a copy of the old values to create a history
                LinkRealization oldValue = new LinkRealization(linkRealization.getLinkName(), linkRealization.getToConcept(), linkRealization.getLinkValue());

                //. Update the current linkRealization
                linkRealization.setLinkName(getLinkField().getText());
                String name = ((ConceptName) getToConceptComboBox().getSelectedItem()).getName();
                if (ILink.NIL.equalsIgnoreCase(name) || ILink.SELF.equalsIgnoreCase(name)) {
                    // Do nothing
                }
                else {
                    Concept concept = null;
                    try {
                        concept = KnowledgeBaseCache.getInstance().findConceptByName(name);
                        if (concept != null) {
                            name = concept.getPrimaryConceptNameAsString();
                        }
                    }
                    catch (DAOException e) {
                        log.error("Unable to find Concept '" + name + "' in the database");
                    }
                }
                linkRealization.setToConcept(name);
                linkRealization.setLinkValue(getLinkValueTextArea().getText());

                // Generate the appropriate history object
                IHistory history = HistoryFactory.replaceLinkRealization(userAccount, oldValue, linkRealization);
                linkRealization.getConceptDelegate().addHistory(history);

                if (userAccount.isAdmin()) {
                    ApproveHistoryTask.approve(userAccount, history);
                }

                /*
                 * Trigger a redraw
                 */
                Dispatcher dispatcher = Dispatcher.getDispatcher(Concept.class);
                dispatcher.setValueObject(null);
                dispatcher.setValueObject(linkRealization.getConceptDelegate().getConcept());
            }
        }
    }

    private class ANewAction extends ActionAdapter {

        private final LinkEditorDialog dialog = new MyLinkEditorDialog();

        /**
         * Show the dialog. The dialog  is used to set the properties of the link
         */
        @Override
        public void doAction() {
            final UserAccount userAccount = (UserAccount) KnowledgebaseApp.DISPATCHER_USERACCOUNT.getValueObject();
            if (userAccount != null && !userAccount.isReadOnly()) {

                LinkTemplate linkTemplate = (LinkTemplate) getLinkList().getSelectedValue();
                LinkRealization linkRealization = new LinkRealization(linkTemplate);
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
                super(AppFrameDispatcher.getFrame());
                getLinkField().setEditable(false);
                setLocationRelativeTo(AppFrameDispatcher.getFrame());
                this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
            }

            public void onOkClick() {
                /*
                 * The dialog handles setting the link propertes. We just have to
                 * save it in this method.
                 */
                Concept concept = getConcept();
                LinkRealization linkRealization = (LinkRealization) getLink();
                concept.addLinkRealization(linkRealization);
                try {
                    ConceptDAO.getInstance().update(concept);
                }
                catch (DAOException e) {
                    concept.removeLinkRealization(linkRealization);
                    log.error("Database transaction failed", e);
                    AppFrameDispatcher.showErrorDialog("Failed to update " + concept + ". Rolling back changes.");
                }
            }
        }
        {
        }
    }

    private class NewAction extends ActionAdapter {

        private final AddLinkRealizationDialog dialog = new AddLinkRealizationDialog(AppFrameDispatcher.getFrame());

        public void doAction() {
            dialog.setConcept(getConcept());
            dialog.setVisible(true);
        }
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
}