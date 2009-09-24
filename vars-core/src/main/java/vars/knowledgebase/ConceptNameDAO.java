package vars.knowledgebase;

import java.util.Collection;
import vars.IDAO;



public interface ConceptNameDAO extends IDAO {

    /**
     * Retrives all conceptnames actually used in annotations. This query
     * searches the Observation.conceptName, Association.toConcept, and
     * ConceptName.name fields
     * fields
     *
     * @return Set of Strings representing the var
     */


    ConceptName findByName(String name);

    Collection<ConceptName> findAll();

}
