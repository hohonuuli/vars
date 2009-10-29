package vars.knowledgebase.ui.persistence;

import org.bushe.swing.event.EventBus;
import vars.knowledgebase.History;
import vars.knowledgebase.HistoryDAO;
import vars.knowledgebase.ui.Lookup;

/**
 * This subscriber inserts lists of Histories from the database.
 */
class InsertHistorySubscriber extends InsertSubscriber<History> {


    public InsertHistorySubscriber(HistoryDAO historyDAO) {
        super(Lookup.TOPIC_INSERT_HISTORY, historyDAO);
    }

    @Override
    History after(History obj) {
        // After putting a new history in the database we'll see if we can approve it.
        EventBus.publish(Lookup.TOPIC_APPROVE_HISTORY, obj);
        return obj;
    }

    @Override
    String getLookupName(History obj) {
        return obj.getConceptMetadata().getConcept().getPrimaryConceptName().getName();
    }
}
