package vars.annotation;

import vars.IDAO;
import vars.IConceptNameValidator;
import vars.knowledgebase.IConcept;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 2:33:04 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IObservationDAO extends IDAO, IConceptNameValidator<IObservation> {

    List<IObservation> findAllByConceptName(String conceptName);

    /**
     * Finds all Observations in the database that use this Concept OR a child of this
     * Concept (if cascade = true)
     *
     * @param concept
     * @param cascade false = use only the concepts names to locate observations. true = Use the concepts names
     *      AND all its descendants names too.
     * @return A Set<IObservation> containing all matching observations. If none are found an empty collection 
     *      is returned
     */
    List<IObservation> findAllByConcept(IConcept concept, boolean cascade);

     /**
     * Retrieves all conceptnames actually used in annotations. This query
     * searches the Observation.conceptName and Association.toConcept fields
     * fields
     *
     * @return Set of Strings
     */
    List<String> findAllConceptNamesUsedInAnnotations();

}
