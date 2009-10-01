/*
 * LinkTemplateEditorPanel.java
 *
 * Created on May 23, 2006, 1:20 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package vars.knowledgebase.ui;

import java.awt.BorderLayout;
import java.util.Collection;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.vars.dao.DAOException;
import org.mbari.vars.knowledgebase.model.Concept;
import org.mbari.vars.knowledgebase.model.HistoryFactory;
import vars.knowledgebase.IConceptName;
import org.mbari.vars.knowledgebase.model.LinkTemplate;
import org.mbari.vars.knowledgebase.model.dao.ConceptDAO;
import org.mbari.vars.knowledgebase.model.dao.KnowledgeBaseCache;
import org.mbari.vars.knowledgebase.model.dao.LinkTemplateDAO;
import org.mbari.vars.knowledgebase.ui.actions.ApproveHistoryTask;
import org.mbari.vars.knowledgebase.ui.dialogs.AddLinkTemplateDialog;
import vars.ILink;
import org.mbari.vars.model.UserAccount;
import org.mbari.vars.util.AppFrameDispatcher;
import org.mbari.swing.WaitIndicator;
import org.mbari.swing.SpinningDialWaitIndicator;
import foxtrot.Worker;
import foxtrot.Job;
import vars.knowledgebase.IHistory;

/**
 *
 * @author brian
 */
public class LinkTemplateEditorPanel extends EditorPanel {

    private static final Logger log = LoggerFactory.getLogger(LinkTemplateEditorPanel.class);

    private static final long serialVersionUID = 1034645432734979217L;
    /**
     * @uml.property  name="editorButtonPanel"
     * @uml.associationEnd
     */
    private EditorButtonPanel editorButtonPanel;
    /**
     * @uml.property  name="linkEditorPanel"
     * @uml.associationEnd
     */
    private LinkEditorPanel linkEditorPanel;
    /**
     * @uml.property  name="newAction"
     * @uml.associationEnd
     */
    private ActionAdapter newAction;
    /**
     * @uml.property  name="updateAction"
     * @uml.associationEnd
     */
    private ActionAdapter updateAction;
    /**
     * @uml.property  name="deleteAction"
     * @uml.associationEnd
     */
    private ActionAdapter deleteAction;

    /** Creates a new instance of LinkTemplateEditorPanel */
    public LinkTemplateEditorPanel() {
        initialize();
        setLocked(isLocked());
    }

    private void initialize() {
        add(getLinkEditorPanel(), BorderLayout.CENTER);
        add(getEditorButtonPanel(), BorderLayout.SOUTH);
    }

    /**
     * @return  the editorButtonPanel
     * @uml.property  name="editorButtonPanel"
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
     * @return  the newAction
     * @uml.property  name="newAction"
     */
    private ActionAdapter getNewAction() {
        if (newAction == null) {
            newAction = new NewAction();
        }
        return newAction;
    }

    /**
     * @return  the updateAction
     * @uml.property  name="updateAction"
     */
    private ActionAdapter getUpdateAction() {
        if (updateAction == null) {
            updateAction = new UpdateAction();
        }
        return updateAction;
    }

    /**
     * @return  the deleteAction
     * @uml.property  name="deleteAction"
     */
    private ActionAdapter getDeleteAction() {
        if (deleteAction == null) {
            deleteAction = new DeleteAction();
        }
        return deleteAction;
    }

    /**
     * @return  the linkEditorPanel
     * @uml.property  name="linkEditorPanel"
     */
    private LinkEditorPanel getLinkEditorPanel() {
        if (linkEditorPanel == null) {
            linkEditorPanel = new LinkEditorPanel();
            linkEditorPanel.setBorder(BorderFactory.createTitledBorder("Allowed Associations"));
        }
        return linkEditorPanel;
    }

    public void setLocked(boolean locked) {
        super.setLocked(locked);
        getEditorButtonPanel().getNewButton().setEnabled(!locked);
        getEditorButtonPanel().getUpdateButton().setEnabled(!locked);
        getEditorButtonPanel().getDeleteButton().setEnabled(!locked);
        getLinkEditorPanel().setLocked(locked);
    }

    public void setConcept(Concept concept) {
        super.setConcept(concept);
        linkEditorPanel.setConcept(concept);
    }

/**
     * @author  brian
     */
    private class NewAction extends ActionAdapter {

        private static final long serialVersionUID = -5786656103234187207L;
        AddLinkTemplateDialog dialog;

        public void doAction() {
            getDialog().setConcept(getConcept());
            getDialog().setVisible(true);
        }

        /**
         * @return  the dialog
         * @uml.property  name="dialog"
         */
        private AddLinkTemplateDialog getDialog() {
            if (dialog == null) {
                dialog = new AddLinkTemplateDialog(AppFrameDispatcher.getFrame());
            }
            return dialog;
        }
    }

    private class UpdateAction extends ActionAdapter {

        private static final long serialVersionUID = 943111369958005649L;

        public void doAction() {

            final LinkEditorPanel panel = getLinkEditorPanel();
            final LinkTemplate linkTemplate = (LinkTemplate) panel.getLink();
            if (linkTemplate == null) {
                AppFrameDispatcher.showWarningDialog("No link has been selected");
            } else {
                // Get new values
                WaitIndicator waitIndicator = new SpinningDialWaitIndicator(LinkTemplateEditorPanel.this);
                final String newToConceptName = panel.getToConcept();
                final String newFromConceptName = panel.getFromConcept();
                final String newLinkName = panel.getLinkName();
                final String newLinkValue = panel.getLinkValue();

                Worker.post(new Job() {

                    public Object run() {

                        // Get old values
                        String oldToConceptName = panel.getToConcept();
                        String oldFromConceptName = null;
                        try {
                            oldFromConceptName = linkTemplate.getFromConcept();
                        } catch (Exception e) {
                            oldFromConceptName = IConceptName.NAME_DEFAULT;
                        }
                        String oldLinkName = linkTemplate.getLinkName();
                        String oldLinkValue = linkTemplate.getLinkValue();

                        // Did we actually make changes
                        boolean updateLink = !newToConceptName.equals(oldToConceptName) || !newFromConceptName.equals(oldFromConceptName) || !newLinkName.equals(oldLinkName) || !newLinkValue.equals(oldLinkValue);

                        // Change the parent concept
                        if (!newFromConceptName.equals(oldFromConceptName)) {
                            try {
                                final Concept newFromConcept = KnowledgeBaseCache.getInstance().findConceptByName(newFromConceptName);
                                final Concept oldFromConcept = (Concept) linkTemplate.getConceptDelegate().getConcept();
                                oldFromConcept.removeLinkTemplate(linkTemplate);
                                ConceptDAO.getInstance().update(oldFromConcept);
                                newFromConcept.addLinkTemplate(linkTemplate);
                                ConceptDAO.getInstance().update(newFromConcept);
                            } catch (DAOException e) {
                                String message = "Failed to change parent of " + linkTemplate + " from " + oldFromConceptName + " to " + newFromConceptName;
                                log.error(message, e);
                                AppFrameDispatcher.showErrorDialog(message);
                            }
                        }

                        if (updateLink) {
                            // Verify that the link name/ link value combo is unique
                            Collection<LinkTemplate> links = new ArrayList<LinkTemplate>();
                            boolean okToProceed = true;
                            try {
                                links = LinkTemplateDAO.getInstance().findByLinkName(linkTemplate.getLinkName());
                                for (LinkTemplate link : links) {
                                    if (link.getId() != linkTemplate.getId() && link.getLinkValue().equalsIgnoreCase(newLinkValue)) {
                                        okToProceed = false;
                                        break;
                                    }
                                }
                            } catch (DAOException e1) {
                                log.error("Failed to look up linkname", e1);
                                AppFrameDispatcher.showErrorDialog("A database error occurred. Unable to complete your request");
                            }

                            if (!okToProceed) {
                                // Don't allow duplicate link names
                                AppFrameDispatcher.showWarningDialog("A LinkTemplate with a LinkName of '" + linkTemplate.getLinkName() + "' and a LinkValue of '" + newLinkValue + "' already exist. Unable to change the LinkTemplate.");
                                panel.setLink(linkTemplate);
                            } else {
                                linkTemplate.setLinkName(newLinkName);
                                linkTemplate.setLinkValue(newLinkValue);
                                linkTemplate.setToConcept(newToConceptName);
                                final Concept concept = (Concept) linkTemplate.getConceptDelegate().getConcept();
                                if (concept != null) {
                                    try {
                                        ConceptDAO.getInstance().update(concept);
                                    } catch (DAOException e) {
                                        log.error("Update to " + linkTemplate + " failed.", e);
                                        AppFrameDispatcher.showErrorDialog("Failed to save changes to database");
                                    }
                                }
                            }
                        }
                        return null;
                    }
                });
                waitIndicator.dispose();
            }
        }
    }

    private class DeleteAction extends ActionAdapter {

        private static final long serialVersionUID = 326290691726170616L;

        public void doAction() {

            final UserAccount userAccount = (UserAccount) KnowledgebaseApp.DISPATCHER_USERACCOUNT.getValueObject();
            if (userAccount != null && !userAccount.isReadOnly()) {
                final LinkEditorPanel panel = getLinkEditorPanel();
                ILink link = panel.getLink();
                if (link == null) {
                    AppFrameDispatcher.showWarningDialog("No LinkTemplate has been selected");
                } else {
                    final LinkTemplate linkTemplate = (LinkTemplate) link;
                    final Concept concept = (Concept) linkTemplate.getConceptDelegate().getConcept();
                    concept.removeLinkTemplate(linkTemplate);
                    final IHistory history = HistoryFactory.delete(userAccount, linkTemplate);
                    concept.addHistory(history);
                    try {
                        ConceptDAO.getInstance().update(concept);
                        if (userAccount.isAdmin()) {
                            ApproveHistoryTask.approve(userAccount, history);
                        }
                    } catch (DAOException e) {
                        log.error("Failed to update " + concept + " in database");
                        AppFrameDispatcher.showErrorDialog("An error occured when trying to save History information " + " to the database. The change was made but no History record for it will be available");
                    }
                    ((KnowledgebaseApp) KnowledgebaseApp.DISPATCHER.getValueObject()).getKnowledgebaseFrame().refreshTreeAndOpenNode(concept.getPrimaryConceptNameAsString());
                }
            }
        }
    }
}
