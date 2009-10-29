package vars.knowledgebase.ui.persistence;

import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ui.Lookup;

/**
 * This subscriber deletes lists of observations from the database.
 */
class UpdateConceptSubscriber extends UpdateSubscriber<Concept> {

    public UpdateConceptSubscriber(ConceptDAO conceptDAO) {
        super(Lookup.TOPIC_UPDATE_CONCEPT, conceptDAO);
    }

    @Override
    String getLookupName(Concept obj) {
        return obj.getPrimaryConceptName().getName();
    }

}
