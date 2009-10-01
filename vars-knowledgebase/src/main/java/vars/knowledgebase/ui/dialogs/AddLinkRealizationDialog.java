package vars.knowledgebase.ui.dialogs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mbari.vars.knowledgebase.model.Concept;
import org.mbari.vars.knowledgebase.model.LinkRealization;
import org.mbari.vars.knowledgebase.model.HistoryFactory;
import org.mbari.vars.knowledgebase.model.History;
import org.mbari.vars.knowledgebase.model.dao.ConceptDAO;
import org.mbari.vars.knowledgebase.ui.LinkEditorPanel;
import org.mbari.vars.knowledgebase.ui.KnowledgebaseApp;
import org.mbari.vars.knowledgebase.ui.actions.ApproveHistoryTask;
import org.mbari.vars.ui.OkCancelButtonPanel;
import org.mbari.vars.util.AppFrameDispatcher;
import org.mbari.vars.dao.DAOException;
import org.mbari.vars.model.UserAccount;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import vars.knowledgebase.IHistory;

/**
 * @version 
 */
public class AddLinkRealizationDialog extends JDialog {

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

    private static final Logger log = LoggerFactory.getLogger(AddLinkRealizationDialog.class);

    /**
     * @param owner
     */
    public AddLinkRealizationDialog(Frame owner) {
        super(owner);
        setTitle("VARS - Add Description");
        initialize();
    }

    public AddLinkRealizationDialog() {
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

    private void onOkClick() {
        setVisible(false);

        UserAccount userAccount = (UserAccount) KnowledgebaseApp.DISPATCHER_USERACCOUNT.getValueObject();
        if (userAccount != null && !userAccount.isReadOnly()) {

            /*
             * Create the linkRealization
             */
            final LinkRealization linkRealization = new LinkRealization();
            final LinkEditorPanel p = getLinkEditorPanel();
            linkRealization.setLinkName(p.getLinkName());
            linkRealization.setToConcept(p.getToConcept());
            linkRealization.setLinkValue(p.getLinkValue());
            final Concept c = getConcept();
            c.addLinkRealization(linkRealization);
            try {
                /*
                 * Use the primary concept name of the 'toConcept'
                 */
                Concept toConcept = ConceptDAO.getInstance().findByName(linkRealization.getToConcept());
                if (toConcept != null) {
                    linkRealization.setToConcept(toConcept.getPrimaryConceptNameAsString());
                }

                /*
                 * Create a History
                 */
                IHistory history = HistoryFactory.add(userAccount, linkRealization);
                c.addHistory(history);

                /*
                 * Save to the database
                 */
                ConceptDAO.getInstance().update(c);

                if (userAccount.isAdmin()) {
                    ApproveHistoryTask.approve(userAccount, history);
                }

                KnowledgebaseApp.DISPATCHER_SELECTED_CONCEPT.setValueObject(null);
                KnowledgebaseApp.DISPATCHER_SELECTED_CONCEPT.setValueObject(c);
            }
            catch (DAOException e) {
                c.removeLinkRealization(linkRealization);
                AppFrameDispatcher.showErrorDialog("A database error occurred. Rolling back change.");
            }
            setConcept(null);
        }
    }

    private void onCancelClick() {
        setVisible(false);
        setConcept(null);
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
                    onOkClick();
                }
            });

            buttonPanel.getCancelButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    onCancelClick();
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
            linkEditorPanel.getLinkNameField().setEditable(false);
        }
        return linkEditorPanel;
    }

    /**
	 * @param concept  the concept to set
	 * @uml.property  name="concept"
	 */
    public void setConcept(Concept concept) {
        getLinkEditorPanel().setConcept(concept);
        this.concept = concept;
    }

    /**
	 * @return  the concept
	 * @uml.property  name="concept"
	 */
    public Concept getConcept() {
        return concept;
    }
}
