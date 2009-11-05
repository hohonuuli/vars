package vars.knowledgebase.ui.actions;

import java.awt.Frame;
import javax.swing.JOptionPane;

import org.bushe.swing.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.DAO;
import vars.knowledgebase.ConceptMetadata;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.Media;
import vars.knowledgebase.ui.Lookup;

public class DeleteMediaTask {
	
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final KnowledgebaseDAOFactory  knowledgebaseDAOFactory;

    public DeleteMediaTask(KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
        this.knowledgebaseDAOFactory = knowledgebaseDAOFactory;
    }
    
    public boolean delete(Media media) {
        boolean okToProceed = (media != null);
        
        /*
         * Let the user know just how much damage their about to do to the database
         */
        if (okToProceed) {
            final Frame frame = (Frame) Lookup.getApplicationFrameDispatcher().getValueObject();
            final int option = JOptionPane.showConfirmDialog(frame,
            		"Are you sure you want to delete '" + media.stringValue() + 
            		"' ? Be aware that this will not effect existing annotations that use it.", 
                    "VARS - Delete LinkTemplate", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            okToProceed = (option == JOptionPane.YES_OPTION);
        }

        /*
         * Delete the linkTemplate
         */
        final ConceptMetadata conceptMetadata = media.getConceptMetadata();
        if (okToProceed) {
            try {
                DAO dao = knowledgebaseDAOFactory.newDAO();
                dao.startTransaction();
                media = dao.merge(media);
                media.getConceptMetadata().removeMedia(media);
                dao.remove(media);
                dao.endTransaction();
            }
            catch (Exception e) {
            	conceptMetadata.addMedia(media);
                final String msg = "Failed to delete '" + media.stringValue() + "'";
                log.error(msg, e);
                EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, msg);
                okToProceed = false;
            }
        }        
        
        return okToProceed;
    }

}
