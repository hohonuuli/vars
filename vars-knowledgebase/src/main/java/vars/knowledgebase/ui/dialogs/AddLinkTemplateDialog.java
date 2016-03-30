/*
 * @(#)AddLinkTemplateDialog.java   2009.10.27 at 11:08:18 PDT
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.JDialog;
import javax.swing.JPanel;
import org.bushe.swing.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.ILink;
import vars.LinkUtilities;
import vars.UserAccount;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.History;
import vars.knowledgebase.HistoryDAO;
import vars.knowledgebase.HistoryFactory;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.LinkTemplate;
import vars.knowledgebase.LinkTemplateDAO;
import vars.knowledgebase.ui.LinkEditorPanel;
import vars.knowledgebase.ui.StateLookup;
import vars.knowledgebase.ui.ToolBelt;
import vars.knowledgebase.ui.actions.ApproveHistoryTask;
import vars.shared.ui.OkCancelButtonPanel;

/**
 * Class description
 *
 *
 * @version        $date$, 2009.10.27 at 11:08:18 PDT
 * @author         Brian Schlining [brian@mbari.org]
 */
public class AddLinkTemplateDialog extends JDialog {

    private OkCancelButtonPanel buttonPanel = null;
    private JPanel jContentPane = null;
    private LinkEditorPanel linkEditorPanel = null;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ApproveHistoryTask approveHistoryTask;
    private Concept concept;

    /**
     * A placeholder that provides default values in the UI
     */
    private final LinkTemplate emptyLinkTemplate;
    private final HistoryFactory historyFactory;
    private final KnowledgebaseDAOFactory knowledgebaseDAOFactory;
    private final KnowledgebaseFactory knowledgebaseFactory;
    private final ToolBelt toolBelt;

    /**
     * Constructs ...
     *
     * @param toolBelt
     */
    public AddLinkTemplateDialog(ToolBelt toolBelt) {
        this(null, toolBelt);
    }

    /**
     * @param owner
     * @param toolBelt
     */
    public AddLinkTemplateDialog(Frame owner, ToolBelt toolBelt) {
        super(owner);
        this.toolBelt = toolBelt;
        this.knowledgebaseDAOFactory = toolBelt.getKnowledgebaseDAOFactory();
        this.knowledgebaseFactory = toolBelt.getKnowledgebaseFactory();
        this.historyFactory = toolBelt.getHistoryFactory();
        this.approveHistoryTask = toolBelt.getApproveHistoryTask();
        emptyLinkTemplate = knowledgebaseFactory.newLinkTemplate();
        emptyLinkTemplate.setLinkName("");
        emptyLinkTemplate.setToConcept(ILink.VALUE_SELF);
        emptyLinkTemplate.setLinkValue(ILink.VALUE_NIL);
        initialize();
    }

    private OkCancelButtonPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new OkCancelButtonPanel();
            buttonPanel.getOkayButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                    LinkTemplate linkTemplate = knowledgebaseFactory.newLinkTemplate();
                    final LinkEditorPanel p = getLinkEditorPanel();
                    linkTemplate.setLinkName(p.getLinkName());
                    linkTemplate.setToConcept(p.getToConcept());
                    linkTemplate.setLinkValue(p.getLinkValue());
                    Concept cOld = getConcept();

                    // A little house cleaning. We don't want the empty_linktemplate to be stored in the db.
                    if (cOld != null) {
                        getConcept().getConceptMetadata().removeLinkTemplate(emptyLinkTemplate);
                    }

                    /*
                     * Lookup the fromConcept
                     */
                    Concept c = null;
                    // DAOTX
                    ConceptDAO conceptDAO = knowledgebaseDAOFactory.newConceptDAO();
                    conceptDAO.startTransaction();
                    try {
                        c = conceptDAO.findByName(p.getFromConcept());
                        c.getConceptMetadata(); // Lazy Load relation
                    }
                    catch (Exception e2) {
                        EventBus.publish(StateLookup.TOPIC_FATAL_ERROR,
                                         "Failed to lookup '" + p.getFromConcept() + "' from the" +
                                         " database. Unable to add '" + linkTemplate.stringValue() + "'");
                    }
                    conceptDAO.endTransaction();
                    conceptDAO.close();

                    /*
                     * Add the new linkTemplate and refresh the view
                     */
                    if (c != null) {

                        // Verify that the linkName isn't already being used.
                        Collection<ILink> links = new ArrayList<ILink>();
                        // DAOTX
                        LinkTemplateDAO linkTemplateDAO = knowledgebaseDAOFactory.newLinkTemplateDAO();
                        linkTemplateDAO.startTransaction();
                        try {
                            links.addAll(linkTemplateDAO.findAllByLinkName(linkTemplate.getLinkName()));
                        }
                        catch (Exception e1) {
                            log.error("Failed to look up linkname", e1);
                            EventBus.publish(StateLookup.TOPIC_NONFATAL_ERROR, e1);
                        }
                        linkTemplateDAO.endTransaction();

                        // verify that the linkName and linkValue aren't already used.
                        Collection<ILink> matchingLinks = LinkUtilities.findMatchingLinksIn(links, linkTemplate);


                        if (matchingLinks.size() > 0) {

                            // Don't allow duplicate link names
                            EventBus.publish(StateLookup.TOPIC_WARNING,
                                             links.size() + " LinkTemplate(s) with a LinkName of '" +
                                             linkTemplate.getLinkName() + "' and LinkValue of '" +
                                             linkTemplate.getLinkValue() +
                                             "' already exist. Unable to complete your request");
                        }
                        else {

                            UserAccount userAccount = StateLookup.getUserAccount();
                            History history = historyFactory.add(userAccount, linkTemplate);
                            try {
                                // DAOTX
                                linkTemplateDAO.startTransaction();
                                linkTemplateDAO.merge(c);
                                c.getConceptMetadata().addLinkTemplate(linkTemplate);
                                linkTemplateDAO.persist(linkTemplate);
                                c.getConceptMetadata().addHistory(history);
                                linkTemplateDAO.persist(history);
                                linkTemplateDAO.endTransaction();
                            
                            }
                            catch (Exception e1) {
                                EventBus.publish(StateLookup.TOPIC_NONFATAL_ERROR, e1);
                            }

                            EventBus.publish(StateLookup.TOPIC_APPROVE_HISTORY, history);

                        }
                        
                    }

                    setConcept(null);
                }

            });
            buttonPanel.getCancelButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    getConcept().getConceptMetadata().removeLinkTemplate(emptyLinkTemplate);
                    setVisible(false);
                    setConcept(null);
                }

            });
        }

        return buttonPanel;
    }

    public Concept getConcept() {
        return concept;
    }

    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(getButtonPanel(), BorderLayout.SOUTH);
            jContentPane.add(getLinkEditorPanel(), BorderLayout.CENTER);
        }

        return jContentPane;
    }

    private LinkEditorPanel getLinkEditorPanel() {
        if (linkEditorPanel == null) {
            linkEditorPanel = new LinkEditorPanel(toolBelt);
            linkEditorPanel.getSearchField().setEnabled(false);
            linkEditorPanel.getLinkComboBox().setEnabled(false);
        }

        return linkEditorPanel;
    }

    private void initialize() {
        if (log.isDebugEnabled()) {
            log.debug("Initializing " + getClass().getName());
        }

        this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        this.setModal(true);
        this.setContentPane(getJContentPane());
        Frame frame = StateLookup.getApplicationFrame();
        setLocationRelativeTo(frame);
        pack();
    }

    public void setConcept(Concept concept) {
        if (concept != null) {
            concept.getConceptMetadata().addLinkTemplate(emptyLinkTemplate);
        }

        getLinkEditorPanel().setConcept(concept);
        getLinkEditorPanel().setLink(emptyLinkTemplate);
        this.concept = concept;
    }
}
