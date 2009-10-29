package vars.knowledgebase.ui.persistence;

import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ui.Lookup;

/**
 * This subscriber inserts lists of observations from the database.
 */
class InsertConceptSubscriber extends InsertSubscriber<Concept> {


    public InsertConceptSubscriber(ConceptDAO conceptDAO) {
        super(Lookup.TOPIC_INSERT_CONCEPT, conceptDAO);
    }

    @Override
    String getLookupName(Concept obj) {
        return obj.getPrimaryConceptName().getName();
    }
}
