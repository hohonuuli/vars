package vars.knowledgebase.ui.persistence;

import vars.knowledgebase.History;
import vars.knowledgebase.HistoryDAO;
import vars.knowledgebase.ui.Lookup;

/**
 * This subscriber deletes lists of Histories from the database.
 */
class DeleteHistorySubscriber extends DeleteSubscriber<History> {

    public DeleteHistorySubscriber(HistoryDAO historyDAO) {
        super(Lookup.TOPIC_DELETE_HISTORY, historyDAO);
    }

    @Override
    String getLookupName(History obj) {
        return obj.getConceptMetadata().getConcept().getPrimaryConceptName().getName();
    }

    @Override
    History prepareForTransaction(History history) {
        history.getConceptMetadata().removeHistory(history);
        return history;
    }
}
