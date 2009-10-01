package vars.knowledgebase.ui.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mbari.vars.knowledgebase.model.LinkRealization;
import org.mbari.vars.knowledgebase.model.dao.ConceptDAO;
import org.mbari.vars.util.AppFrameDispatcher;
import org.mbari.vars.dao.DAOException;

import javax.swing.*;
import org.mbari.vars.knowledgebase.model.Concept;
import vars.knowledgebase.IConcept;

/**
 * @author brian
 * @version $Id: $
 * @since Jan 5, 2007 11:31:15 AM PST
 */
public class DeleteLinkRealizationTask {

    private static final Logger log = LoggerFactory.getLogger(DeleteLinkRealizationTask.class);

     private DeleteLinkRealizationTask() {
        // NO instantiation allowed
    }

    public static boolean delete(final LinkRealization linkRealization) {
        boolean okToProceed = (linkRealization != null);
        final IConcept concept = linkRealization.getConceptDelegate().getConcept();


        /*
         * Let the user know just how much damage their about to do to the database
         */
        if (okToProceed) {
            final int option = JOptionPane.showConfirmDialog(AppFrameDispatcher.getFrame(),
            		"Are you sure you want to delete '" + linkRealization.stringValue() + "' ? ", 
                    "VARS - Delete LinkTemplate", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            okToProceed = (option == JOptionPane.YES_OPTION);
        }

        /*
         * Delete the linkRealization
         */
        if (okToProceed) {
            try {
            	concept.removeLinkRealization(linkRealization);
                ConceptDAO.getInstance().update((Concept) concept);
            }
            catch (DAOException e) {
                final String msg = "Failed to delete '" + linkRealization.stringValue() + "'";
                log.error(msg, e);
                AppFrameDispatcher.showErrorDialog(msg);
                okToProceed = false;
            }
        }

        return okToProceed;
    }

}
