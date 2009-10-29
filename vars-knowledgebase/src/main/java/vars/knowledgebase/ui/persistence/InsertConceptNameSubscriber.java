package vars.knowledgebase.ui.persistence;

import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameDAO;
import vars.knowledgebase.ui.Lookup;

/**
 * This subscriber inserts lists of observations from the database.
 */
class InsertConceptNameSubscriber extends InsertSubscriber<ConceptName> {


    public InsertConceptNameSubscriber(ConceptNameDAO conceptNameDAO) {
        super(Lookup.TOPIC_INSERT_CONCEPT_NAME, conceptNameDAO);
    }

    @Override
    String getLookupName(ConceptName obj) {
        return obj.getConcept().getPrimaryConceptName().getName();
    }
}
