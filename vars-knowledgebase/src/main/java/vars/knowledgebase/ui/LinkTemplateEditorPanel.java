/*
 * @(#)LinkTemplateEditorPanel.java   2009.10.24 at 08:17:15 PDT
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
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.BorderFactory;
import org.bushe.swing.event.EventBus;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.swing.SpinningDialWaitIndicator;
import org.mbari.swing.WaitIndicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.ILink;
import vars.UserAccount;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptMetadata;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.History;
import vars.knowledgebase.HistoryDAO;
import vars.knowledgebase.LinkTemplate;
import vars.knowledgebase.LinkTemplateDAO;
import vars.knowledgebase.ui.dialogs.AddLinkTemplateDialog;

/**
 *
 * @author brian
 */
public class LinkTemplateEditorPanel extends EditorPanel {

    private static final Logger log = LoggerFactory.getLogger(LinkTemplateEditorPanel.class);
    private static final long serialVersionUID = 1034645432734979217L;
    private ActionAdapter deleteAction;
    private EditorButtonPanel editorButtonPanel;
    private LinkEditorPanel linkEditorPanel;
    private ActionAdapter newAction;
    private final ToolBelt toolBelt;
    private ActionAdapter updateAction;

    /**
     * Creates a new instance of LinkTemplateEditorPanel
     *
     * @param toolBelt
     */
    public LinkTemplateEditorPanel(final ToolBelt toolBelt) {
        this.toolBelt = toolBelt;
        initialize();
        setLocked(isLocked());
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
     * @return  the linkEditorPanel
     * @uml.property  name="linkEditorPanel"
     */
    private LinkEditorPanel getLinkEditorPanel() {
        if (linkEditorPanel == null) {
            linkEditorPanel = new LinkEditorPanel(toolBelt);
            linkEditorPanel.setBorder(BorderFactory.createTitledBorder("Allowed Associations"));
        }

        return linkEditorPanel;
    }

    /**
     * @return  the newAction
     */
    private ActionAdapter getNewAction() {
        if (newAction == null) {
            newAction = new NewAction();
        }

        return newAction;
    }

    /**
     * @return  the updateAction
     */
    private ActionAdapter getUpdateAction() {
        if (updateAction == null) {
            updateAction = new UpdateAction();
        }

        return updateAction;
    }

    private void initialize() {
        add(getLinkEditorPanel(), BorderLayout.CENTER);
        add(getEditorButtonPanel(), BorderLayout.SOUTH);
    }

    @Override
    public void setConcept(Concept concept) {
        super.setConcept(concept);
        linkEditorPanel.setConcept(concept);
    }

    @Override
    public void setLocked(boolean locked) {
        super.setLocked(locked);
        getEditorButtonPanel().getNewButton().setEnabled(!locked);
        getEditorButtonPanel().getUpdateButton().setEnabled(!locked);
        getEditorButtonPanel().getDeleteButton().setEnabled(!locked);
        getLinkEditorPanel().setLocked(locked);
    }

    private class DeleteAction extends ActionAdapter {

        private static final long serialVersionUID = 326290691726170616L;

        public void doAction() {


            final UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();
            if ((userAccount != null) && !userAccount.isReadOnly()) {
                final LinkEditorPanel panel = getLinkEditorPanel();
                ILink link = panel.getLink();
                if (link == null) {
                    EventBus.publish(Lookup.TOPIC_WARNING, "No LinkTemplate has been selected");
                }
                else {
                    final LinkTemplate linkTemplate = (LinkTemplate) link;
                    final Concept concept = (Concept) linkTemplate.getConceptMetadata().getConcept();
                    final ConceptMetadata conceptMetadata = linkTemplate.getConceptMetadata();
                    conceptMetadata.removeLinkTemplate(linkTemplate);
                    final History history = toolBelt.getHistoryFactory().delete(userAccount, linkTemplate);
                    conceptMetadata.addHistory(history);

                    try {
                        HistoryDAO historyDAO = toolBelt.getKnowledgebaseDAOFactory().newHistoryDAO();
                        historyDAO.makePersistent(history);

                        if (userAccount.isAdministrator()) {
                            toolBelt.getApproveHistoryTask().approve(userAccount, history);
                        }
                    }
                    catch (Exception e) {
                        log.error("Failed to update " + concept + " in database");
                        EventBus.publish(
                            Lookup.TOPIC_NONFATAL_ERROR,
                            "An error occured when trying to save History information " +
                            " to the database. The change was made but no History record for it will be available");
                    }

                    EventBus.publish(Lookup.TOPIC_REFRESH_KNOWLEGEBASE, concept.getPrimaryConceptName().getName());
                }
            }
        }
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
                Frame frame = (Frame) Lookup.getApplicationFrameDispatcher().getValueObject();
                dialog = new AddLinkTemplateDialog(frame, toolBelt);
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
                EventBus.publish(Lookup.TOPIC_WARNING, "No link has been selected");
            }
            else {

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
                        }
                        catch (Exception e) {
                            oldFromConceptName = ConceptName.NAME_DEFAULT;
                        }

                        String oldLinkName = linkTemplate.getLinkName();
                        String oldLinkValue = linkTemplate.getLinkValue();

                        // Did we actually make changes
                        boolean updateLink = !newToConceptName.equals(oldToConceptName) ||
                                             !newFromConceptName.equals(oldFromConceptName) ||
                                             !newLinkName.equals(oldLinkName) || !newLinkValue.equals(oldLinkValue);

                        // Change the parent concept
                        if (!newFromConceptName.equals(oldFromConceptName)) {
                            try {
                                final ConceptDAO conceptDAO = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
                                final Concept newFromConcept = conceptDAO.findByName(newFromConceptName);
                                final ConceptMetadata conceptMetadata = linkTemplate.getConceptMetadata();
                                conceptMetadata.removeLinkTemplate(linkTemplate);
                                conceptDAO.update(conceptMetadata);
                                newFromConcept.getConceptMetadata().addLinkTemplate(linkTemplate);
                                conceptDAO.update(newFromConcept);
                            }
                            catch (Exception e) {
                                String message = "Failed to change parent of " + linkTemplate + " from " +
                                                 oldFromConceptName + " to " + newFromConceptName;
                                log.error(message, e);
                                EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, message);
                            }
                        }

                        if (updateLink) {

                            // Verify that the link name/ link value combo is unique
                            LinkTemplateDAO linkTemplateDAO = toolBelt.getKnowledgebaseDAOFactory()
                                .newLinkTemplateDAO();
                            Collection<LinkTemplate> links = new ArrayList<LinkTemplate>();
                            boolean okToProceed = true;
                            try {

                                links.addAll(linkTemplateDAO.findAllByLinkName(linkTemplate.getLinkName()));

                                for (LinkTemplate link : links) {
                                    if (!linkTemplateDAO.equalInDatastore(link, linkTemplate) &&
                                            link.getLinkValue().equalsIgnoreCase(newLinkValue)) {
                                        okToProceed = false;

                                        break;
                                    }
                                }
                            }
                            catch (Exception e1) {
                                log.error("Failed to look up linkname", e1);
                                EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e1);
                            }

                            if (!okToProceed) {

                                // Don't allow duplicate link names
                                EventBus.publish(Lookup.TOPIC_WARNING,
                                                 "A LinkTemplate with a LinkName of '" + linkTemplate.getLinkName() +
                                                 "' and a LinkValue of '" + newLinkValue +
                                                 "' already exist. Unable to change the LinkTemplate.");
                                panel.setLink(linkTemplate);
                            }
                            else {
                                linkTemplate.setLinkName(newLinkName);
                                linkTemplate.setLinkValue(newLinkValue);
                                linkTemplate.setToConcept(newToConceptName);

                                try {
                                    linkTemplateDAO.update(linkTemplate);
                                }
                                catch (Exception e) {
                                    log.error("Update to " + linkTemplate + " failed.", e);
                                    EventBus.publish(Lookup.TOPIC_WARNING, "Failed to save changes to database");
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
}
