package vars.knowledgebase.ui.persistence;

import vars.VARSException;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameDAO;
import vars.knowledgebase.ConceptNameTypes;
import vars.knowledgebase.ui.Lookup;

/**
 * This subscriber deletes lists of observations from the database.
 */
class DeleteConceptNameSubscriber extends DeleteSubscriber<ConceptName> {


    public DeleteConceptNameSubscriber(ConceptNameDAO conceptNameDAO) {
        super(Lookup.TOPIC_DELETE_CONCEPT_NAME, conceptNameDAO);
    }

    @Override
    ConceptName before(ConceptName obj) {
        
        /*
         * ---- Step 1: Check that the is not the primary name
         */
        if (ConceptNameTypes.PRIMARY.toString().equals(obj.getNameType())) {
            throw new VARSException("You attempted to delete a primary name, '" + obj +
                    "'. This is not allowed");
        }

        return super.before(obj);
    }



    @Override
    String getLookupName(ConceptName obj) {
        return obj.getConcept().getPrimaryConceptName().getName();
    }

    @Override
    ConceptName prepareForTransaction(ConceptName conceptName) {
        conceptName.getConcept().removeConceptName(conceptName);
        return conceptName;
    }
}
