package vars.knowledgebase.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.JDialog;
import javax.swing.JPanel;
import org.bushe.swing.event.EventBus;
import vars.ILink;
import vars.LinkBean;
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
import vars.knowledgebase.ui.KnowledgebaseApp;
import vars.knowledgebase.ui.LinkEditorPanel;
import vars.knowledgebase.ui.Lookup;
import vars.knowledgebase.ui.ToolBelt;
import vars.knowledgebase.ui.actions.ApproveHistoryTask;
import vars.shared.ui.OkCancelButtonPanel;

public class AddLinkTemplateDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    /**
	 * @uml.property  name="jContentPane"
	 * @uml.associationEnd  
	 */
    private JPanel jContentPane = null;

    /**
	 * @uml.property  name="buttonPanel"
	 * @uml.associationEnd  
	 */
    private OkCancelButtonPanel buttonPanel = null;

    /**
	 * @uml.property  name="linkEditorPanel"
	 * @uml.associationEnd  
	 */
    private LinkEditorPanel linkEditorPanel = null;
    
    /**
	 * @uml.property  name="concept"
	 * @uml.associationEnd  
	 */
    private Concept concept;
    
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final KnowledgebaseDAOFactory knowledgebaseDAOFactory;
    private final KnowledgebaseFactory knowledgebaseFactory;
    private final HistoryFactory historyFactory;
    private final ApproveHistoryTask approveHistoryTask;
    
    /**
     * A placeholder that provides default values in the UI
     */
    private final LinkTemplate emptyLinkTemplate;

    /**
     * @param owner
     */
    public AddLinkTemplateDialog(Frame owner, ToolBelt toolBelt) {
        super(owner);
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
    
    public AddLinkTemplateDialog(ToolBelt toolBelt) {
        this(null, toolBelt);
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        if (log.isDebugEnabled()) {
            log.debug("Initializing " + getClass().getName());
        }
        this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        this.setModal(true);
        this.setContentPane(getJContentPane());
        Frame frame = (Frame) Lookup.getApplicationFrameDispatcher().getValueObject();
        setLocationRelativeTo(frame);
        pack();
    }

    /**
	 * This method initializes jContentPane
	 * @return  javax.swing.JPanel
	 * @uml.property  name="jContentPane"
	 */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(getButtonPanel(), BorderLayout.SOUTH);
            jContentPane.add(getLinkEditorPanel(), BorderLayout.CENTER);
        }
        return jContentPane;
    }

    /**
	 * This method initializes buttonPanel	
	 * @return  javax.swing.JPanel
	 * @uml.property  name="buttonPanel"
	 */
    private OkCancelButtonPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new OkCancelButtonPanel();
            buttonPanel.getOkButton().addActionListener(new ActionListener() {

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
                    ConceptDAO conceptDAO = knowledgebaseDAOFactory.newConceptDAO();
                    try {
                        c = conceptDAO.findByName(p.getFromConcept());
                    }
                    catch (Exception e2) {
                        EventBus.publish(Lookup.TOPIC_FATAL_ERROR, "Failed to lookup '" + p.getFromConcept() + "' from the" +
                                " database. Unable to add '" + linkTemplate.stringValue() + "'");
                    }
                    
                    /*
                     * Add the new linkTemplate and refresh the view
                     */
                    if (c != null) {
                        // Verify that the linkName isn't already being used.
                        Collection links = new ArrayList();
                        LinkTemplateDAO linkTemplateDAO = knowledgebaseDAOFactory.newLinkTemplateDAO();
                        try {
                            links = linkTemplateDAO.findAllByLinkName(linkTemplate.getLinkName());
                        }
                        catch (Exception e1) {
                            log.error("Failed to look up linkname", e1);
                            EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e1);
                        }
                        
                        // verify that the linkName and linkValue aren't already used.
                        boolean matchExists = false;
                        for (Iterator i = links.iterator(); i.hasNext();) {
                            LinkTemplate link = (LinkTemplate) i.next();
                            if (link.getLinkName().equalsIgnoreCase(linkTemplate.getLinkName()) &&
                                    link.getLinkValue().equalsIgnoreCase(linkTemplate.getLinkValue())) {
                                matchExists = true;
                                break;
                            }
                        }

                        if (matchExists) {
                            // Don't allow duplicate link names
                            EventBus.publish(Lookup.TOPIC_WARNING, links.size() + " LinkTemplate(s) with a LinkName of '" +
                                linkTemplate.getLinkName() + "' and LinkValue of '" + linkTemplate.getLinkValue() + 
                                "' already exist. Unable to complete your request");
                        }
                        else {
                            c.getConceptMetadata().addLinkTemplate(linkTemplate);
                            linkTemplateDAO.makePersistent(linkTemplate);
                            UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();
                            History history = historyFactory.add(userAccount, linkTemplate);
                            c.getConceptMetadata().addHistory(history);
                            try {
                                HistoryDAO historyDAO = knowledgebaseDAOFactory.newHistoryDAO();
                                historyDAO.makePersistent(history);
                            }
                            catch (Exception e1) {
                                c.getConceptMetadata().removeLinkTemplate(linkTemplate);
                                EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, "Failed to update '" + c.getPrimaryConceptName().getName() +
                                        "' in the database. Rolling back your changes.");

                            }

                            if (userAccount != null && userAccount.isAdministrator()) {
                                approveHistoryTask.approve(userAccount, history);
                            }

                            KnowledgebaseApp app = (KnowledgebaseApp) KnowledgebaseApp.DISPATCHER.getValueObject();
                            app.getKnowledgebaseFrame().refreshTreeAndOpenNode(c.getPrimaryConceptName().getName());
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

    /**
	 * This method initializes linkEditorPanel	
	 * @return  javax.swing.JPanel
	 * @uml.property  name="linkEditorPanel"
	 */
    private LinkEditorPanel getLinkEditorPanel() {
        if (linkEditorPanel == null) {
            linkEditorPanel = new LinkEditorPanel();
            linkEditorPanel.getSearchField().setEnabled(false);
            linkEditorPanel.getLinkComboBox().setEnabled(false);            
        }
        return linkEditorPanel;
    }

    /**
	 * @param concept  the concept to set
	 * @uml.property  name="concept"
	 */
    public void setConcept(Concept concept) {
        if (concept != null) {
            concept.getConceptMetadata().addLinkTemplate(emptyLinkTemplate);
        }
        getLinkEditorPanel().setConcept(concept);
        getLinkEditorPanel().setLink(emptyLinkTemplate);
        this.concept = concept;
    }

    /**
	 * @return  the concept
	 * @uml.property  name="concept"
	 */
    public Concept getConcept() {
        return concept;
    }

}  //  @jve:decl-index=0:visual-constraint="10,10"
