package vars.annotation.jpa;

import vars.jpa.DAO;
import vars.annotation.ObservationDAO;
import vars.annotation.Observation;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptName;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collection;

import com.google.inject.Inject;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vars.knowledgebase.jpa.ConceptDAOImpl;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 4:40:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class ObservationDAOImpl extends DAO implements ObservationDAO {

    private final ConceptDAO conceptDAO;
    private final Logger log = LoggerFactory.getLogger(getClass());
    

    @Inject
    public ObservationDAOImpl(EntityManager entityManager) {
        super(entityManager);
        
        /* 
         * TODO This won't work if we move the Knowledgebase to a separate persistence unit.
         * If that happens the KB lookups would need to be done in a separate transaction
         */
        this.conceptDAO = new ConceptDAOImpl(entityManager);
    }

    public List<Observation> findAllByConceptName(String conceptName) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("conceptName", conceptName);
        return findByNamedQuery("Observation.findByConceptName", map);
    }

    /**
     * This should be called within a JPA transaction
     * @param concept
     * @param cascade
     * @return
     */
    public List<Observation> findAllByConcept(final Concept concept, final boolean cascade) {

        Collection<ConceptName> conceptNames = null;
        if (cascade) {
            conceptNames = conceptDAO.findDescendentNames(concept);
        }
        else {
            conceptNames = new HashSet<ConceptName>();
            conceptNames.addAll(concept.getConceptNames());
        }

        StringBuilder sb = new StringBuilder("SELECT o FROM Observation o WHERE o.conceptName IN (");
        int n = 0;
        for (ConceptName cn : conceptNames) {
            sb.append("'").append(cn.getName()).append("'");
            if (n < conceptNames.size() - 1) {
                sb.append(", ");
            }
            n++;
        }
        sb.append(")");

        Query query = getEntityManager().createQuery(sb.toString());
        List<Observation> observations = query.getResultList();

        return observations;
    }
    
    /**
     * Updates the fields of an observation in the database. This is used by the
     * annotation UI since we don't know when the observation was last modified
     * in the database so it's a workaround for concurrent modifications. 
     * 
     * @param observation
     * @return
     */
    public Observation updateFields(Observation observation) {
        Observation original = observation;
        if (observation != null) {
            original = find(observation);
            if (original != null) {
                original.setConceptName(observation.getConceptName());
                original.setNotes(observation.getNotes());
                original.setObservationDate(observation.getObservationDate());
                original.setObserver(observation.getObserver());
                original.setX(observation.getX());
                original.setY(observation.getY());
            }
        }
    	return original;
    }

    /**
     * This should be called within a JPA transaction
     * @return
     */
    public List<String> findAllConceptNamesUsedInAnnotations() {

        final EntityManager entityManager = getEntityManager();

        // ---- Step 1: Fetch from Observation
        String sql = "SELECT DISTINCT conceptName FROM Observation";
        Query query = entityManager.createNativeQuery(sql);
        List<String> conceptNames = query.getResultList();

        // ---- Step 2: Fetch from Association
        sql = "SELECT DISTINCT toConcept FROM Association";
        query = entityManager.createNativeQuery(sql);
        conceptNames.addAll(query.getResultList());


        return conceptNames; 
    }

    public void validateName(Observation object) {
        Concept concept = conceptDAO.findByName(object.getConceptName());
        if (concept != null) {
            object.setConceptName(concept.getPrimaryConceptName().getName());
        }
        else {
            log.warn(object + " contains a 'conceptName', " + object.getConceptName() + " that was not found in the knowlegebase");
        }
    }
    
}
