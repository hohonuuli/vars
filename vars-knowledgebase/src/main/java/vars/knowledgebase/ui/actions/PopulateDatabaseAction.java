
/*
 * PopulateDatabaseAction.java
 *
 * Created on May 15, 2006, 3:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package vars.knowledgebase.ui.actions;

import java.awt.Frame;
import java.util.Collection;
import javax.swing.JOptionPane;

import org.mbari.awt.event.ActionAdapter;

import vars.MiscFactory;
import vars.UserAccount;
import vars.UserAccountDAO;
import vars.UserAccountRoles;
import vars.VARSPersistenceException;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameTypes;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.ui.StateLookup;
import vars.knowledgebase.ui.ToolBelt;
import vars.shared.ui.dialogs.CreateUserAccountDialog;


/**
 * This action generates a default database that can be used for the knowledgebase.
 * @author brian
 */
public class PopulateDatabaseAction extends ActionAdapter {

    private final KnowledgebaseFactory knowledgebaseFactory;
    private final MiscFactory miscFactory;
    private final ToolBelt toolBelt;

    public PopulateDatabaseAction(ToolBelt toolBelt) {
        this.knowledgebaseFactory = toolBelt.getKnowledgebaseFactory();
        this.miscFactory = toolBelt.getMiscFactory();
        this.toolBelt = toolBelt;
    }
    

    /**
     *     Method description
     *    
     * @throws RuntimeException - Thrown if the action can not be completed.
     */
    public void doAction() {
        boolean ok = false;
        try {
            ok = checkRootConcept();
            ok = checkAdmin();
        } catch (Exception ex) {
            throw new VARSPersistenceException("Failed to fully initialize knowledgebase", ex);
        }
        
        if (!ok) {
            throw new VARSPersistenceException("Failed to fully initialize the knowledgbase");
        }
    }
    
    
    /**
     * @throws RuntimeException - Thrown if unable to find or create a root concept
     */
    private boolean checkRootConcept() {
        
        /*
         * The Knowledgebase needs to have a root concept
         */
        ConceptDAO conceptDAO = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
        conceptDAO.startTransaction();
        Concept root = conceptDAO.findRoot();
        boolean gotRoot = (root != null);
        
        if (!gotRoot) {

            final Frame frame = StateLookup.getApplicationFrame();

            int ok = JOptionPane.showConfirmDialog(frame,
                         "Unable to find the root of the knowledgebase. Do you want to create one?",
                         "VARS - No Root Found", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (ok == JOptionPane.YES_OPTION) {
                ConceptName conceptName = knowledgebaseFactory.newConceptName();
                conceptName.setNameType(ConceptNameTypes.PRIMARY.toString());
                conceptName.setName(ConceptName.NAME_DEFAULT);
                conceptName.setAuthor("VARS");
                Concept concept = knowledgebaseFactory.newConcept();
                concept.addConceptName(conceptName);
                concept.setOriginator("VARS");
                conceptDAO.persist(concept);
                gotRoot = true;

            }
        }
        conceptDAO.endTransaction();
        conceptDAO.close();
        return gotRoot;
    }
    
    
    /**
     * @throws RuntimeException if unable to find or create an Administrator
     */
    private boolean checkAdmin() {

        UserAccountDAO userAccountDAO = toolBelt.getMiscDAOFactory().newUserAccountDAO();
        userAccountDAO.startTransaction();
        Collection<UserAccount> admins = userAccountDAO.findAllByRole(UserAccountRoles.ADMINISTRATOR.toString());
        userAccountDAO.endTransaction();
        boolean gotAdmins = (admins.size() != 0);
        
        if (!gotAdmins) {
            final Frame frame = StateLookup.getApplicationFrame();

            // create an admin
            int ok = JOptionPane.showConfirmDialog(frame,
                         "Unable to find any Administrator accounts. Do you want to create one?",
                         "VARS - No Administrator Found", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (ok == JOptionPane.YES_OPTION) {
                UserAccount admin = CreateUserAccountDialog.showDialog(frame, true,
                        "VARS - Create Administrator Account", toolBelt.getMiscDAOFactory(), miscFactory);
                if (admin != null) {
                    userAccountDAO.startTransaction();
                    admin.setRole(UserAccountRoles.ADMINISTRATOR.toString());
                    userAccountDAO.merge(admin);
                    userAccountDAO.endTransaction();
                    gotAdmins = true;
                }

            }
        }
        userAccountDAO.close();
        return gotAdmins;
    }
    
    

}
