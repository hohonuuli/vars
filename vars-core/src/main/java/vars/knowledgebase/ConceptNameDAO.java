package vars.knowledgebase;

import java.util.Collection;
import vars.DAO;


public interface ConceptNameDAO extends DAO {

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

    Collection<ConceptName> findByNameContaining(String substring);

    Collection<ConceptName> findByNameStartingWith(String s);

}
