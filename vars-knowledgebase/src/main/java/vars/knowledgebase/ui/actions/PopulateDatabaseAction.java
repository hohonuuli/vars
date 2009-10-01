
/*
 * PopulateDatabaseAction.java
 *
 * Created on May 15, 2006, 3:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package vars.knowledgebase.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.mbari.awt.event.ActionAdapter;
import org.mbari.vars.ui.NewUserDialog;
import org.mbari.vars.dao.DAOException;
import org.mbari.vars.knowledgebase.model.Concept;
import org.mbari.vars.knowledgebase.model.ConceptName;
import org.mbari.vars.knowledgebase.model.dao.ConceptDAO;
import org.mbari.vars.knowledgebase.model.dao.KnowledgeBaseCache;
import vars.Role;
import org.mbari.vars.model.UserAccount;
import org.mbari.vars.model.dao.UserAccountDAO;
import org.mbari.vars.util.AppFrameDispatcher;

/**
 * This action generates a default database that can be used for the knowledgebase.
 * @author brian
 */
public class PopulateDatabaseAction extends ActionAdapter {

    private static final long serialVersionUID = 2286120036607506672L;

    /** Creates a new instance of PopulateDatabaseAction */
    public PopulateDatabaseAction() {}

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
        } catch (DAOException ex) {
            RuntimeException re = new RuntimeException("Failed to fully initialize knowledgebase");
            re.initCause(ex);
            throw re;
        }
        
        if (!ok) {
            throw new RuntimeException("Failed to fully initialize the knowledgbase");
        }
    }
    
    
    /**
     * @throws RuntimeException - Thrown if unable to find or create a root concept
     */
    private boolean checkRootConcept() throws DAOException{
        
        /*
         * The Knowledgebase needs to have a root concept
         */
        Concept root = KnowledgeBaseCache.getInstance().findRootConcept();
        boolean gotRoot = (root != null);
        
        if (!gotRoot) {

            int ok = JOptionPane.showConfirmDialog(AppFrameDispatcher.getFrame(),
                         "Unable to find the root of the knowledgebase. Do you want to create one?",
                         "VARS - No Root Found", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (ok == JOptionPane.YES_OPTION) {
                ConceptName name = new ConceptName(ConceptName.NAME_DEFAULT, ConceptName.NAMETYPE_PRIMARY);
                name.setAuthor("VARS");
                Concept concept = new Concept();
                concept.addConceptName(name);
                concept.setOriginator("VARS");

                ConceptDAO.getInstance().insert(concept);
                KnowledgeBaseCache.getInstance().clear();
                gotRoot = true;

            }
        }
        return gotRoot;
    }
    
    
    /**
     * @throws RuntimeException if unable to find or create an Administrator
     */
    private boolean checkAdmin() throws DAOException {
        
        Collection admins = UserAccountDAO.getInstance().findAdmins();
        boolean gotAdmins = (admins.size() != 0);
        
        if (!gotAdmins) {
            
            // create an admin
            int ok = JOptionPane.showConfirmDialog(AppFrameDispatcher.getFrame(),
                         "Unable to find any Administrator accounts. Do you want to create one?",
                         "VARS - No Administrator Found", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (ok == JOptionPane.YES_OPTION) {
                UserAccount admin = NewUserDialog.showDialog(AppFrameDispatcher.getFrame(), true, 
                        "VARS - Create Administrator Account");
                if (admin != null) {
                    admin.setRole(Role.ADMINISTRATOR);
                    UserAccountDAO.getInstance().update(admin);
                    gotAdmins = true;
                }

            }
        }
        return gotAdmins;
    }
    
    

}
