package vars.knowledgebase.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.jpa.DAO;
import vars.jpa.JPAEntity;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptMetadata;
import vars.knowledgebase.ConceptName;
import vars.VARSPersistenceException;
import com.google.inject.Inject;

import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.persistence.EntityManager;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 4:43:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConceptDAOImpl extends DAO implements ConceptDAO {


    @Inject
    public ConceptDAOImpl(EntityManager entityManager) {
        super(entityManager);
    }

    public Concept findRoot() {
        Concept root = null;
        List<Concept> roots = findByNamedQuery("Concept.findRoot", new HashMap<String, Object>());

        if (roots.size() == 1) {
            root = roots.get(0);
        }
        else if (roots.size() > 1) {
            throw new VARSPersistenceException("ERROR!! More than one root was found in the knowedgebase");
        }
        else if (roots.size() == 0) {
            log.warn("No root was found in the knowledgebase");
            //throw new VARSPersistenceException("ERROR!! No root was found in the knowedgebase");
        }
        return root;
    }

    /**
     * This find method should be called inside of a transaction
     * @param name
     * @return
     */
    public Concept findByName(final String name) {
        List<Concept> concepts =  findByNamedQuery("Concept.findByName", new HashMap<String, Object>() {{ put("name", name);}});
        return concepts.size() == 0 ? null : concepts.get(0);
    }

    public List<Concept> findAllByNameContaining(final String nameGlob) {
        final String name = "%" + nameGlob + "%";
        return findByNamedQuery("Concept.findAllByNameGlob", new HashMap<String, Object>() {{ put("name", name.toLowerCase()); }});
    }

    public List<Concept> findAllByNameStartingWith(final String nameGlob) {
        final String name = nameGlob + "%";
        return findByNamedQuery("Concept.findAllByNameGlob", new HashMap<String, Object>() {{ put("name", name.toLowerCase()); }});

    }

    public List<Concept> findAllByNameEndingWith(final String nameGlob) {
        final String name = '%' + nameGlob;
        return findByNamedQuery("Concept.findAllByNameGlob", new HashMap<String, Object>() {{ put("name", name.toLowerCase()); }});
    }

    /**
     * This should be called within a JPA tranaction
     * @param concept
     * @return
     */
    public Collection<ConceptName> findDescendentNames(Concept concept) {

        Collection<ConceptName> conceptNames = new ArrayList<ConceptName>();
        Concept mergedConcept;
        if (concept instanceof JPAEntity) {
            mergedConcept = findByPrimaryKey(ConceptImpl.class, ((JPAEntity) concept).getId());
        }
        else {
            mergedConcept = findByName(concept.getPrimaryConceptName().getName());
        }

        if (mergedConcept != null) {
            conceptNames.addAll(mergedConcept.getConceptNames());
            findDescendentNames(mergedConcept.getChildConcepts(), conceptNames);
        }

        return conceptNames;

    }


    /**
     * Private method for recursively collecting conceptnames
     * @param concepts A collection of concepts. Normally this is from concept.getChildConcepts()
     * @param conceptNames The colleciton of ConceptNames used to collect all the individual
     *      ConceptName objects
     */
    private void findDescendentNames(Collection<Concept> concepts, Collection<ConceptName> conceptNames) {

        for (Concept concept : concepts) {
            //log.info("In findDescendentNames processing " + concept);
            conceptNames.addAll(concept.getConceptNames());
            findDescendentNames(concept.getChildConcepts(), conceptNames);
        }
    }


    public Collection<Concept> findAll() {
        Map<String, Object> params = new HashMap<String, Object>();
        return findByNamedQuery("Concept.findAll", params);
    }

    /**
     * Should be called within a JPA transaction
     * @param concept
     * @return
     */
    public Collection<Concept> findDescendents(Concept concept) {
        Collection<Concept> concepts = new ArrayList<Concept>();
        findDescendents(concept, concepts);

        return concepts;
    }

    private void findDescendents(Concept concept, Collection<Concept> concepts) {
        concepts.add(concept);
        for (Concept child : concept.getChildConcepts()) {
            findDescendents(child, concepts);
        }
    }


    /**
     * This method will start and stop the transaction on it's own. 
     */
    public void cascadeRemove(Concept concept) {
        if (((JPAEntity) concept).getId() == null) {
            log.info("Attempted to cascade delete a non-mnaged entity");
            return;
        }
        // Bring ALL child concepts into the transaction first
        startTransaction();
        concept = find(concept);
        Queue<Concept> queue = new LinkedList<Concept>(findDescendents(concept));
        endTransaction();
        while(queue.size() > 0) {
            Concept c = queue.poll();
            if (c.getChildConcepts().size() == 0) {
                // If it doesn't have any children delete the concept
                startTransaction();
                c = find(c);
                Concept parent = c.getParentConcept();
                if (parent != null) {
                    parent.removeChildConcept(c);
                }
                remove(c);
                endTransaction();
            }
            else {
                // If it has children put it at the end of the queue
                queue.offer(c);
            }
        }
        
    }

}
