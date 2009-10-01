package vars.knowledgebase.ui.actions;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mbari.vars.dao.DAOException;
import org.mbari.vars.knowledgebase.model.Concept;
import org.mbari.vars.knowledgebase.model.Media;
import org.mbari.vars.knowledgebase.model.dao.ConceptDAO;
import org.mbari.vars.util.AppFrameDispatcher;
import vars.knowledgebase.IConcept;

public class DeleteMediaTask {
	
private static final Logger log = LoggerFactory.getLogger(DeleteMediaTask.class);
    
    private DeleteMediaTask() {
        // NO instantiation allowed
    }
    
    public static boolean delete(final Media media) {
        boolean okToProceed = (media != null);
        final IConcept concept = media.getConceptDelegate().getConcept();
        
        /*
         * Let the user know just how much damage their about to do to the database
         */
        if (okToProceed) {
            final int option = JOptionPane.showConfirmDialog(AppFrameDispatcher.getFrame(), 
            		"Are you sure you want to delete '" + media.stringValue() + 
            		"' ? Be aware that this will not effect existing annotations that use it.", 
                    "VARS - Delete LinkTemplate", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            okToProceed = (option == JOptionPane.YES_OPTION);
        }

        /*
         * Delete the linkTemplate
         */
        if (okToProceed) {
        	concept.removeMedia(media);
            try {
                ConceptDAO.getInstance().update((Concept) concept);
            }
            catch (DAOException e) {
            	concept.addMedia(media);
                final String msg = "Failed to delete '" + media.stringValue() + "'";
                log.error(msg, e);
                AppFrameDispatcher.showErrorDialog(msg);
                okToProceed = false;
            }
        }        
        
        return okToProceed;
    }

}
