package vars.knowledgebase.ui.actions;

import java.awt.Frame;
import javax.swing.JOptionPane;
import org.bushe.swing.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import vars.knowledgebase.ConceptMetadata;
import vars.knowledgebase.LinkRealization;
import vars.knowledgebase.LinkRealizationDAO;
import vars.knowledgebase.ui.Lookup;

/**
 * @author brian
 * @version $Id: $
 * @since Jan 5, 2007 11:31:15 AM PST
 */
public class DeleteLinkRealizationTask {

    private  final Logger log = LoggerFactory.getLogger(getClass());
    private final LinkRealizationDAO linkRealizationDAO;

     public DeleteLinkRealizationTask(LinkRealizationDAO linkRealizationDAO) {
         this.linkRealizationDAO = linkRealizationDAO;
    }

    public boolean delete(final LinkRealization linkRealization) {
        boolean okToProceed = (linkRealization != null);
        final ConceptMetadata conceptMetadata = linkRealization.getConceptMetadata();

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
            	conceptMetadata.removeLinkRealization(linkRealization);
                linkRealizationDAO.makeTransient(linkRealization);
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
