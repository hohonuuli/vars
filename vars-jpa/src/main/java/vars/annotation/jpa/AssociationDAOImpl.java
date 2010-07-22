package vars.annotation.jpa;

import com.google.common.collect.ImmutableMap;
import vars.annotation.AssociationDAO;
import vars.annotation.Association;
import vars.jpa.DAO;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.Concept;
import com.google.inject.Inject;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 3:30:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class AssociationDAOImpl extends DAO implements AssociationDAO {


    @Inject
    public AssociationDAOImpl(EntityManager entityManager) {
        super(entityManager);

    }

    public List<Association> findAllByConceptNameAndValues(String conceptName,
            String linkName, String toConcept, String linkValue) {
        Map<String, Object> params = toParameterMap("conceptName", conceptName, "linkName", linkName,
                "toConcept", toConcept, "linkValue", linkValue);
        return null;
        //return findByNamedQuery("Observation.findAllByConceptNameAndAssociation", params);
    }

    /**
     * {@inheritDoc}
     * @param ass {@inheritDoc}
     * @param conceptDAO {@inheritDoc}
     */
    public void validateName(Association ass, ConceptDAO conceptDAO) {
        Concept concept = conceptDAO.findByName(ass.getToConcept());
        if (concept != null) {
            ass.setToConcept(concept.getPrimaryConceptName().getName());
        }
        else {
            log.warn(ass + " contains a 'toConcept', " + ass.getToConcept() + " that was not found in the knowlegebase");
        }
    }
}
