package vars.knowledgebase;

import vars.IDAO;

import java.util.Set;


public interface IConceptNameDAO extends IDAO {

    /**
     * Retrives all conceptnames actually used in annotations. This query
     * searches the Observation.conceptName, Association.toConcept, and
     * ConceptName.name fields
     * fields
     *
     * @return Set of Strings representing the var
     */
    Set<String> findAllConceptNamesUsedInAnnotations();

}
