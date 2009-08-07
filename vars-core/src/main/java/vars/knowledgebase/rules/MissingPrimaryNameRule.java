package vars.knowledgebase.rules;

import vars.IPersistenceRule;
import vars.services.VARSPersistenceException;
import vars.knowledgebase.IConcept;

/**
 * Throws an exception if the concept is missing a primary name
 */
public class MissingPrimaryNameRule implements IPersistenceRule<IConcept> {

    public IConcept apply(IConcept object) {
        if (object.getPrimaryConceptName() == null) {
            throw new VARSPersistenceException("You are not allowed to persist a concept without a conceptname");
        }
        return object;
    }
}
