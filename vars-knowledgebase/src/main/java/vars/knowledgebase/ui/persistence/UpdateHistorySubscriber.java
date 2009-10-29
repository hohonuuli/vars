package vars.knowledgebase.ui.persistence;

import vars.knowledgebase.History;
import vars.knowledgebase.HistoryDAO;
import vars.knowledgebase.ui.Lookup;

/**
 * This subscriber deletes lists of Histories from the database.
 */
class UpdateHistorySubscriber extends UpdateSubscriber<History> {

    public UpdateHistorySubscriber(HistoryDAO historyDAO) {
        super(Lookup.TOPIC_UPDATE_HISTORY, historyDAO);
    }

    @Override
    String getLookupName(History obj) {
        return obj.getConceptMetadata().getConcept().getPrimaryConceptName().getName();
    }
}
