package vars.knowledgebase.rules;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import java.util.Collection;
import vars.PersistenceRule;
import vars.VARSPersistenceException;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameTypes;

/**
 * Throws an exception if the concept is missing a primary name
 */
public class ExactlyOnePrimaryNameRule implements PersistenceRule<Concept> {

    // Filter used by before method
    Predicate<ConceptName> filter = new Predicate<ConceptName>() {
        public boolean apply(ConceptName input) {
            return  ConceptNameTypes.PRIMARY.toString().equalsIgnoreCase(input.getNameType());
        }
    };

    @SuppressWarnings("empty-statement")
    public Concept apply(Concept concept) {
        /* ---- Does a duplicate name exist
         *  DO NOTHING. JPA layer will throw an exeption if a duplicate exists
         */

        /* ---- Does the concept have exactly 1 primary name
         *  JPA Impl does not allow this when calling addConceptName BUT it could
         *  changed after a ConceptName has been added.
         */
        Collection<ConceptName> primaryNames = Collections2.filter(concept.getConceptNames(), filter);
        if (primaryNames.size() != 1) {
            throw new VARSPersistenceException("The concept, " + concept + ", should have exactly 1 primary name. It has " + primaryNames.size());
        };
        return concept;
    }
}
