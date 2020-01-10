/*
 * @(#)LinkTemplateEditorPanel.java   2009.11.23 at 10:17:37 PST
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
import vars.DAO;
import vars.ILink;
import vars.LinkBean;
import vars.UserAccount;
import vars.knowledgebase.*;
import vars.knowledgebase.ui.dialogs.AddLinkTemplateDialog;

/**
 *
 * @author brian
 */
public class LinkTemplateEditorPanel extends EditorPanel {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private ActionAdapter deleteAction;
    private EditorButtonPanel editorButtonPanel;
    private LinkEditorPanel linkEditorPanel;
    private ActionAdapter newAction;
    private ActionAdapter updateAction;

    /**
     * Creates a new instance of LinkTemplateEditorPanel
     *
     * @param toolBelt
     */
    public LinkTemplateEditorPanel(final ToolBelt toolBelt) {
        super(toolBelt);
        initialize();
        setLocked(isLocked());
    }

    private ActionAdapter getDeleteAction() {
        if (deleteAction == null) {
            deleteAction = new DeleteAction();
        }

        return deleteAction;
    }

    private EditorButtonPanel getEditorButtonPanel() {
        if (editorButtonPanel == null) {
            editorButtonPanel = new EditorButtonPanel();
            editorButtonPanel.getDeleteButton().addActionListener(getDeleteAction());
            editorButtonPanel.getNewButton().addActionListener(getNewAction());
            editorButtonPanel.getUpdateButton().addActionListener(getUpdateAction());
        }

        return editorButtonPanel;
    }

    private LinkEditorPanel getLinkEditorPanel() {
        if (linkEditorPanel == null) {
            linkEditorPanel = new LinkEditorPanel(getToolBelt());
            linkEditorPanel.setBorder(BorderFactory.createTitledBorder("Allowed Associations"));
        }

        return linkEditorPanel;
    }

    private ActionAdapter getNewAction() {
        if (newAction == null) {
            newAction = new NewAction();
        }

        return newAction;
    }

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

    /**
     *
     * @param concept
     */
    @Override
    public void setConcept(Concept concept) {
        super.setConcept(concept);
        linkEditorPanel.setConcept(concept);
    }

    /**
     *
     * @param locked
     */
    @Override
    public void setLocked(boolean locked) {
        super.setLocked(locked);
        getEditorButtonPanel().getNewButton().setEnabled(!locked);
        getEditorButtonPanel().getUpdateButton().setEnabled(!locked);
        getEditorButtonPanel().getDeleteButton().setEnabled(!locked);
        getLinkEditorPanel().setLocked(locked);
    }

    private class DeleteAction extends ActionAdapter {


        /**
         */
        public void doAction() {

            final UserAccount userAccount = StateLookup.getUserAccount();
            if ((userAccount != null) && !userAccount.isReadOnly()) {
                final LinkEditorPanel panel = getLinkEditorPanel();
                ILink link = panel.getLink();
                if (link == null) {
                    EventBus.publish(StateLookup.TOPIC_WARNING, "No LinkTemplate has been selected");
                }
                else if (!(link instanceof LinkTemplate)) {

                    // This happens when you try to delete 'nil | nil | nil'
                    EventBus.publish(StateLookup.TOPIC_WARNING,
                                     "You are not allowed to delete '" + link.stringValue() + "'");

                }
                else {

                    LinkTemplate linkTemplate = (LinkTemplate) link;

                    String name = linkTemplate.getConceptMetadata().getConcept().getPrimaryConceptName().getName();
                    try {

                        // DAOTX
                        DAO dao = getToolBelt().getKnowledgebaseDAOFactory().newDAO();
                        dao.startTransaction();
                        /*
                         * Don't merge linkTemplate as it may have been modified in the history approval
                         * process. Instead let's just find it in the database
                         */
                        linkTemplate = dao.find(linkTemplate); 
                        ConceptMetadata conceptMetadata = linkTemplate.getConceptMetadata();
                        final History history = getToolBelt().getHistoryFactory().delete(userAccount, linkTemplate);
                        conceptMetadata.addHistory(history);
                        dao.persist(history);
                        dao.endTransaction();
                        dao.close();
                        EventBus.publish(StateLookup.TOPIC_APPROVE_HISTORY, history);    // Will refresh everything

                    }
                    catch (Exception e) {
                        log.error("Failed to delete " + linkTemplate + " in database", e);
                        EventBus.publish(StateLookup.TOPIC_NONFATAL_ERROR, e);
                        EventBus.publish(StateLookup.TOPIC_REFRESH_KNOWLEGEBASE, name);
                    }
                }
            }
        }
    }



    private class NewAction extends ActionAdapter {

        AddLinkTemplateDialog dialog;

        /**
         */
        public void doAction() {
            getDialog().setConcept(getConcept());
            getDialog().setVisible(true);
        }

 
        private AddLinkTemplateDialog getDialog() {
            if (dialog == null) {
                Frame frame = StateLookup.getApplicationFrame();
                dialog = new AddLinkTemplateDialog(frame, getToolBelt());
            }

            return dialog;
        }
    }


    private class UpdateAction extends ActionAdapter {

        void updateLink(LinkTemplate linkTemplate, ILink newLink) {
            KnowledgebaseDAOFactory factory = getToolBelt().getKnowledgebaseDAOFactory();
            LinkTemplateDAO linkTemplateDAO = factory.newLinkTemplateDAO();
            linkTemplateDAO.startTransaction();
            linkTemplate = linkTemplateDAO.find(linkTemplate);
            // TODO what if linkTemplate is null
            // Change the parent concept
            if (!linkTemplate.getFromConcept().equals(newLink.getFromConcept())) {
                final ConceptDAO conceptDAO = factory.newConceptDAO(linkTemplateDAO.getEntityManager());
                final Concept newFromConcept = conceptDAO.findByName(newLink.getFromConcept());
                final ConceptMetadata conceptMetadata = linkTemplate.getConceptMetadata();
                conceptMetadata.removeLinkTemplate(linkTemplate);
                newFromConcept.getConceptMetadata().addLinkTemplate(linkTemplate);
            }

            // Verify that the link name/ link value combo is unique
            Collection<LinkTemplate> links = new ArrayList<LinkTemplate>();
            links.addAll(linkTemplateDAO.findAllByLinkName(linkTemplate.getLinkName()));
            for (LinkTemplate link : links) {
                if (!linkTemplateDAO.equalInDatastore(link, linkTemplate) &&
                        link.getLinkValue().equalsIgnoreCase(newLink.getLinkValue())) {
                    // Don't allow duplicate link names
                    EventBus.publish(StateLookup.TOPIC_WARNING,
                                     "A LinkTemplate with a LinkName of '" +
                                     linkTemplate.getLinkName() + "' and a LinkValue of '" +
                                     newLink.getLinkValue() +
                                     "' already exist. Unable to change the LinkTemplate.");
                    getLinkEditorPanel().setLink(linkTemplate);
                    return;
                }
            }

            linkTemplate.setLinkName(newLink.getLinkName());
            linkTemplate.setLinkValue(newLink.getLinkValue());
            linkTemplate.setToConcept(newLink.getToConcept());
            linkTemplateDAO.endTransaction();
            linkTemplateDAO.close();
        }

        

        boolean needsUpdate(ILink oldLink, ILink newLink) {
            // Did we actually make changes
            return !newLink.getToConcept().equals(oldLink.getToConcept()) ||
                    !newLink.getFromConcept().equals(oldLink.getFromConcept()) ||
                    !newLink.getLinkName().equals(oldLink.getLinkName()) ||
                    !newLink.getLinkValue().equals(oldLink.getLinkValue());
        }


        /**
         */
        public void doAction() {
            final UserAccount userAccount = StateLookup.getUserAccount();
            if ((userAccount != null) && !userAccount.isReadOnly()) {
                final LinkEditorPanel panel = getLinkEditorPanel();
                ILink link = panel.getLink();
                if (link == null) {
                    EventBus.publish(StateLookup.TOPIC_WARNING, "No LinkTemplate has been selected");
                }
                else if (!(link instanceof LinkTemplate)) {

                    // This happens when you try to delete 'nil | nil | nil'
                    EventBus.publish(StateLookup.TOPIC_WARNING,
                            "You are not allowed to delete '" + link.stringValue() + "'");
                }
                else {

                    final LinkTemplate linkTemplate = (LinkTemplate) link;
                    final ILink newLink = new LinkBean(panel.getLinkName(), panel.getToConcept(),
                            panel.getLinkValue(), panel.getFromConcept());

                    // Get new values
                    WaitIndicator waitIndicator = new SpinningDialWaitIndicator(LinkTemplateEditorPanel.this);

                    Worker.post(new Job() {
                        public Object run() {
                            if (needsUpdate(linkTemplate, newLink)) {
                                try {
                                    updateLink(linkTemplate, newLink);
                                }
                                catch (Exception e) {
                                    EventBus.publish(StateLookup.TOPIC_NONFATAL_ERROR, e);
                                }
                            }
                            EventBus.publish(StateLookup.TOPIC_REFRESH_KNOWLEGEBASE, newLink.getFromConcept());
                            return null;
                        }
                    });

                    waitIndicator.dispose();

                }
            }
        }
    }
}
