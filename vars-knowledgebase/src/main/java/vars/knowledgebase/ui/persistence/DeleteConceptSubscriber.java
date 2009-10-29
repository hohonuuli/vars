package vars.knowledgebase.ui.persistence;

import java.util.Collection;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ui.Lookup;

/**
 * This subscriber deletes lists of Concepts from the database.
 */
class DeleteConceptSubscriber extends DeleteSubscriber<Concept> {


    public DeleteConceptSubscriber(ConceptDAO conceptDAO) {
        super(Lookup.TOPIC_DELETE_CONCEPT, conceptDAO);
    }

    @Override
    String getLookupName(Concept obj) {
        return obj.getPrimaryConceptName().getName();
    }

    @Override
    Concept prepareForTransaction(Concept obj) {
        Concept concept = (Concept) obj;
        concept.getParentConcept().removeChildConcept(concept);
        return obj;
    }

    @Override
    Concept before(Concept obj) {
        obj = super.before(obj);
        Collection<Concept> descendants = ((ConceptDAO) dao).findDescendents(obj);
        log.info("Deleteing " + descendants.size() + " concepts from the persistent store");

        return obj;
    }


}
