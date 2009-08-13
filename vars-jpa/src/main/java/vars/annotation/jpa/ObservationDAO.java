package vars.annotation.jpa;

import vars.jpa.DAO;
import vars.annotation.IObservationDAO;
import vars.annotation.IObservation;
import vars.knowledgebase.IConcept;
import vars.knowledgebase.IConceptDAO;
import vars.knowledgebase.IConceptName;
import org.mbari.jpax.EAO;
import org.mbari.jpax.NonManagedEAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collection;

import com.google.inject.Inject;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.EntityTransaction;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 4:40:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class ObservationDAO extends DAO implements IObservationDAO{

    private final IConceptDAO conceptDAO;
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    public ObservationDAO(EAO eao, IConceptDAO conceptDao) {
        super(eao);
        this.conceptDAO = conceptDao;
    }

    public List<IObservation> findAllByConceptName(String conceptName) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("conceptName", conceptName);
        return getEAO().findByNamedQuery("Observation.findByConceptName", map);
    }

    public List<IObservation> findAllByConcept(final IConcept concept, final boolean cascade) {

        Collection<IConceptName> conceptNames = null;
        if (cascade) {
            conceptNames = conceptDAO.findDescendentNames(concept);
        }
        else {
            conceptNames = new HashSet<IConceptName>();
            conceptNames.addAll(concept.getConceptNames());
        }

        String jpql = "SELECT o FROM Observation o WHERE o.conceptName IN (";
        int n = 0;
        for (IConceptName cn : conceptNames) {
            jpql += cn.getName();
            if (n < conceptNames.size() - 1) {
                jpql += ", ";
            }
        }
        jpql += ")";

        final EntityManager entityManager = getEAO().createEntityManager();
        final EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        Query query = entityManager.createQuery(jpql);
        List<IObservation> observations = query.getResultList();
        entityManager.close();

        return observations;
    }

    public List<String> findAllConceptNamesUsedInAnnotations() {

        final EntityManager entityManager = getEAO().createEntityManager();

        // ---- Step 1: Fetch from Observation
        String sql = "SELECT DISTINCT conceptName FROM Observation";
        Query query = entityManager.createNativeQuery(sql);
        List<String> conceptNames = query.getResultList();

        // ---- Step 2: Fetch from Association
        sql = "SELECT DISTINCT toConcept FROM Association";
        query = entityManager.createNativeQuery(sql);
        conceptNames.addAll(query.getResultList());

        entityManager.close();

        return conceptNames; 
    }

    public void validateName(IObservation object) {
        IConcept concept = conceptDAO.findByName(object.getConceptName());
        if (concept != null) {
            object.setConceptName(concept.getPrimaryConceptName().getName());
        }
        else {
            log.warn(object + " contains a 'conceptName', " + object.getConceptName() + " that was not found in the knowlegebase");
        }
    }
    
}
