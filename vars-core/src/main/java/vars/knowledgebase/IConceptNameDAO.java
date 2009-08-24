package vars.knowledgebase;

import java.util.Collection;
import vars.IDAO;



public interface IConceptNameDAO extends IDAO {

    /**
     * Retrives all conceptnames actually used in annotations. This query
     * searches the Observation.conceptName, Association.toConcept, and
     * ConceptName.name fields
     * fields
     *
     * @return Set of Strings representing the var
     */


    IConceptName findByName(String name);

    Collection<IConceptName> findAll();

}
