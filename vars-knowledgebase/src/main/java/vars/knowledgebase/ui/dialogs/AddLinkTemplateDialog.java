package vars.knowledgebase.ui.dialogs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mbari.vars.dao.DAOException;
import org.mbari.vars.knowledgebase.model.Concept;
import org.mbari.vars.knowledgebase.model.History;
import org.mbari.vars.knowledgebase.model.HistoryFactory;
import org.mbari.vars.knowledgebase.model.LinkTemplate;
import org.mbari.vars.knowledgebase.model.dao.ConceptDAO;
import org.mbari.vars.knowledgebase.model.dao.KnowledgeBaseCache;
import org.mbari.vars.knowledgebase.model.dao.LinkTemplateDAO;
import org.mbari.vars.knowledgebase.ui.KnowledgebaseApp;
import org.mbari.vars.knowledgebase.ui.LinkEditorPanel;
import org.mbari.vars.knowledgebase.ui.actions.ApproveHistoryTask;
import org.mbari.vars.model.UserAccount;
import org.mbari.vars.ui.OkCancelButtonPanel;
import org.mbari.vars.util.AppFrameDispatcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import vars.knowledgebase.IHistory;

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
    
    private static final Logger log = LoggerFactory.getLogger(AddLinkTemplateDialog.class);
    
    /**
     * A placeholder that provides default values in the UI
     */
    private static final LinkTemplate EMPTY_LINKTEMPLATE = new LinkTemplate("", "self", "nil");

    /**
     * @param owner
     */
    public AddLinkTemplateDialog(Frame owner) {
        super(owner);
        initialize();
    }
    
    public AddLinkTemplateDialog() {
        this(null);
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
        setLocationRelativeTo(AppFrameDispatcher.getFrame());
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
                    LinkTemplate linkTemplate = new LinkTemplate();
                    final LinkEditorPanel p = getLinkEditorPanel();
                    linkTemplate.setLinkName(p.getLinkName());
                    linkTemplate.setToConcept(p.getToConcept());
                    linkTemplate.setLinkValue(p.getLinkValue());
                    Concept cOld = getConcept();
                    
                    // A little house cleaning. We don't want the empty_linktemplate to be stored in the db.
                    if (cOld != null) {
                        getConcept().removeLinkTemplate(EMPTY_LINKTEMPLATE);
                    }
                    
                    /*
                     * Lookup the fromConcept
                     */
                    Concept c = null;
                    try {
                        c = KnowledgeBaseCache.getInstance().findConceptByName(p.getFromConcept());
                    }
                    catch (DAOException e2) {
                        AppFrameDispatcher.showErrorDialog("Failed to lookup '" + p.getFromConcept() + "' from the" +
                                " database. Unable to add '" + linkTemplate.stringValue() + "'");
                    }
                    
                    /*
                     * Add the new linkTemplate and refresh the view
                     */
                    if (c != null) {
                        // Verify that the linkName isn't already being used.
                        Collection links = new ArrayList();
                        try {
                            links = LinkTemplateDAO.getInstance().findByLinkName(linkTemplate.getLinkName());
                        }
                        catch (DAOException e1) {
                            log.error("Failed to look up linkname", e1);
                            AppFrameDispatcher.showErrorDialog("A database error occurred. Unable to complete your request");
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
                            AppFrameDispatcher.showWarningDialog(links.size() + " LinkTemplate(s) with a LinkName of '" +
                                linkTemplate.getLinkName() + "' and LinkValue of '" + linkTemplate.getLinkValue() + 
                                "' already exist. Unable to complete your request");
                        }
                        else {
                            c.addLinkTemplate(linkTemplate);
                            UserAccount userAccount = (UserAccount) KnowledgebaseApp.DISPATCHER_USERACCOUNT.getValueObject();
                            IHistory history = HistoryFactory.add(userAccount, linkTemplate);
                            c.addHistory(history);
                            try {
                                ConceptDAO.getInstance().update(c);
                            }
                            catch (DAOException e1) {
                                c.removeLinkTemplate(linkTemplate);
                                AppFrameDispatcher.showErrorDialog("Failed to update '" + c.getPrimaryConceptNameAsString() +
                                        "' in the database. Rolling back your changes.");

                            }

                            if (userAccount != null && userAccount.isAdmin()) {
                                ApproveHistoryTask.approve(userAccount, history);
                            }

                            KnowledgebaseApp app = (KnowledgebaseApp) KnowledgebaseApp.DISPATCHER.getValueObject();
                            app.getKnowledgebaseFrame().refreshTreeAndOpenNode(c.getPrimaryConceptNameAsString());
                        }
                    }
                    
                    setConcept(null);
                }
                
            });
            buttonPanel.getCancelButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    getConcept().removeLinkTemplate(EMPTY_LINKTEMPLATE);
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
            concept.addLinkTemplate(EMPTY_LINKTEMPLATE);
        }
        getLinkEditorPanel().setConcept(concept);
        getLinkEditorPanel().setLink(EMPTY_LINKTEMPLATE);
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
