package vars.knowledgebase.ui.actions;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptName;
import vars.annotation.ObservationDAO;
import vars.knowledgebase.ConceptDAO;

public class DeleteConceptTask  {

    private static final Logger log = LoggerFactory.getLogger(DeleteConceptTask.class);
    private final ConceptDAO conceptDAO;
    private final ObservationDAO observationDAO;
    
    public DeleteConceptTask(ConceptDAO conceptDAO, ObservationDAO observationDAO) {
        this.conceptDAO = conceptDAO;
        this.observationDAO = observationDAO;
    }
    
    public  boolean delete(final Concept concept) {
        boolean okToProceed = (concept != null);
        final String rejectedName = concept.getPrimaryConceptName().getName();
        final Concept parentConcept = concept.getParentConcept();

        /*
         * Look up the concept that we're deleting. Make sure it exists and it's not the root concept
         */
        if (okToProceed) {
            okToProceed = (concept != null && 
                    concept.getConceptName(ConceptName.NAME_DEFAULT) == null);
        }
        
        /*
         * Let the user know jsut how much damage their about to do to the database
         */
        if (okToProceed) {
            Collection<ConceptName> deletedConcepts;
            try {
                deletedConcepts = conceptDAO.findDescendentNames(concept);
                deletedConcepts = KnowledgeBaseCache.getInstance().findDescendants(rejectedName);
                final int option = JOptionPane.showConfirmDialog(AppFrameDispatcher.getFrame(), "You are about to delete " + 
                        deletedConcepts.size() + " concept(s) from the \nknowledgebase. Are you sure you want to continue?", 
                        "VARS - Delete Concepts", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                okToProceed = (option == JOptionPane.YES_OPTION);
            }
            catch (DAOException e) {
                log.error("Failed to fetch concepts from the database", e);
                AppFrameDispatcher.showErrorDialog("Failed to fetch concepts from database");
                okToProceed = false;
            }
            
        }

        /*
         * Get all concept-names that will be deleted. Use those to find all the Observations that 
         * will be affected.
         */
        Collection observations = null;
        if (okToProceed) {
            try {
                observations = ObservationDAO.getInstance().findByConcept(concept, true);
            }
            catch (DAOException e) {
                log.error("Failed to fetch observations from database", e);
                AppFrameDispatcher.showErrorDialog("Unable to look up Observations from database");
                okToProceed = false;
            }
        }

        /*
         * If observations were found that should be modified, give the user a 
         * choice about how to modify them.. i.e. leave them alone or change to the name of the parentConcept
         */
        if (okToProceed && observations.size() > 0) {
            okToProceed = handleObservations(observations, concept);
        }

        /*
         * Delete the concept
         */
        if (okToProceed) {
            try {
                ConceptDAO.getInstance().delete(concept);
                parentConcept.removeChildConcept(concept);
            }
            catch (DAOException e) {
                final String msg = "Failed to delete '" + rejectedName + "'";
                log.error(msg, e);
                AppFrameDispatcher.showErrorDialog(msg);
                okToProceed = false;
            }
        }        
        
        /*
         * We really want to refresh the cache. However, if we do it here then any references that we're holding
         * on to will be stale and Castor will throw an exception if we try to do DB operations on them
         */
//        if (okToProceed) {
//            try {
//                KnowledgeBaseCache.getInstance().clear();
//            }
//            catch (DAOException e) {
//                log.error("Failed to clear KnowledgeBaseCache", e);
//            }
//        }
        
        return okToProceed;
    }
    
    private static boolean handleObservations(final Collection observations, final Concept concept) {
        boolean okToProceed = true;
        final String deletedName = concept.getPrimaryConceptNameAsString();
        final IConcept parentConcept = concept.getParentConcept();
        final String newName = parentConcept.getPrimaryConceptNameAsString();

        final String msg = observations.size() + " Observations were found using '" +
        deletedName + "' or one of it's \nchildren. Do you want to update the names to '" + newName+
        "' or \nignore them and leave them as is?";

        /*
         * Report the usages to the user. Allow them to replace with parent concept or leave as is.
         */
        final Object[] options = {"Update", "Ignore", "Cancel"};
        final int option = JOptionPane.showOptionDialog(AppFrameDispatcher.getFrame(),
                msg, "VARS - Removing '" + deletedName + "'",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[2]);


        switch (option) {
        case JOptionPane.YES_OPTION:
            okToProceed = update(observations, newName);
            break;
        case JOptionPane.NO_OPTION:
            break;
        default:
            okToProceed = false;
            break;
        }
        return okToProceed;
    }


    private static boolean update(final Collection observations, final String newName) {
        boolean success = true;
        final IDAO dao = ObservationDAO.getInstance();
        for (final Iterator i = observations.iterator(); i.hasNext();) {
            final Observation observation = (Observation) i.next();
            final String oldName = observation.getConceptName();
            observation.setConceptName(newName);
            try {
                dao.update(observation);
            }
            catch (DAOException e) {
                observation.setConceptName(oldName);
                log.error("Failed to change observation[" + observation.getId() + "] name to '" + newName + "'", e);
                success = false;
                break;
            }
        }
        return success;
    }

}
