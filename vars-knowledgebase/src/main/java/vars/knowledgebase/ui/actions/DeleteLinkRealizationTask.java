package vars.knowledgebase.ui.actions;

import java.awt.Frame;
import javax.swing.JOptionPane;
import org.bushe.swing.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import vars.DAO;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.LinkRealization;
import vars.knowledgebase.ui.Lookup;

/**
 * @author brian
 * @version $Id: $
 * @since Jan 5, 2007 11:31:15 AM PST
 */
public class DeleteLinkRealizationTask {

    private  final Logger log = LoggerFactory.getLogger(getClass());
    private final KnowledgebaseDAOFactory knowledgebaseDAOFactory;

     public DeleteLinkRealizationTask(KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
         this.knowledgebaseDAOFactory = knowledgebaseDAOFactory;
    }

    public boolean delete(LinkRealization linkRealization) {
        boolean okToProceed = (linkRealization != null);

        /*
         * Let the user know just how much damage their about to do to the database
         */
        if (okToProceed) {
            Frame frame = (Frame) Lookup.getApplicationFrameDispatcher().getValueObject();
            final int option = JOptionPane.showConfirmDialog(frame,
            		"Are you sure you want to delete '" + linkRealization.stringValue() + "' ? ", 
                    "VARS - Delete LinkTemplate", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            okToProceed = (option == JOptionPane.YES_OPTION);
        }

        /*
         * Delete the linkRealization
         */
        if (okToProceed) {
            try {
                DAO dao = knowledgebaseDAOFactory.newDAO();
                dao.startTransaction();
                linkRealization = dao.merge(linkRealization);
            	linkRealization.getConceptMetadata().removeLinkRealization(linkRealization);
                dao.remove(linkRealization);
                dao.endTransaction();
            }
            catch (Exception e) {
                final String msg = "Failed to delete '" + linkRealization.stringValue() + "'";
                log.error(msg, e);
                EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, msg);
                okToProceed = false;
            }
        }

        return okToProceed;
    }

}
