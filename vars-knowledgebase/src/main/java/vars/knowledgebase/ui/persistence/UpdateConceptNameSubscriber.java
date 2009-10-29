package vars.knowledgebase.ui.persistence;

import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameDAO;
import vars.knowledgebase.ui.Lookup;

/**
 * This subscriber deletes lists of observations from the database.
 */
class UpdateConceptNameSubscriber extends UpdateSubscriber<ConceptName> {

 

    public UpdateConceptNameSubscriber(ConceptNameDAO conceptNameDAO) {
        super(Lookup.TOPIC_UPDATE_CONCEPT, conceptNameDAO);
    }

    @Override
    String getLookupName(ConceptName obj) {
        return obj.getConcept().getPrimaryConceptName().getName();
    }

    @Override
    ConceptName before(ConceptName obj) {

        //final Concept concept = obj.getConcept();
        /* ---- Does a duplicate name exist
         *  DO NOTHING. JPA layer will throw an exeption if a duplicate exists
         */ 

        /* ---- Does the concept have exactly 1 primary name
         *  JPA Impl does not allow this when calling addConceptName BUT it could
         *  changed after a ConceptName has been added.
         */
        // Do Nothing. The ConceptValidator.class checks this.

        return super.before(obj);
    }





}
