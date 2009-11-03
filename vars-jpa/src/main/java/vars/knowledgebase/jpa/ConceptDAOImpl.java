package vars.knowledgebase.jpa;

import vars.jpa.DAO;
import vars.jpa.JPAEntity;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameDAO;
import vars.VARSPersistenceException;
import com.google.inject.Inject;

import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import vars.PersistenceRule;
import vars.knowledgebase.rules.ExactlyOnePrimaryNameRule;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 4:43:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConceptDAOImpl extends DAO implements ConceptDAO {

    private final ConceptNameDAO conceptNameDAO;
    private final PersistenceRule<Concept> thereCanBeOnlyOne = new ExactlyOnePrimaryNameRule();

    @Inject
    public ConceptDAOImpl(EntityManager entityManager, ConceptNameDAO conceptNameDAO) {
        super(entityManager);
        this.conceptNameDAO = conceptNameDAO;
    }

    @Override
    public <T> T makePersistent(T object) {
        thereCanBeOnlyOne.apply((Concept) object);
        return super.makePersistent(object);
    }

    @Override
    public <T> T update(T object) {
        thereCanBeOnlyOne.apply((Concept) object);
        return super.update(object);
    }

    public Concept findRoot() {
        List<Concept> roots = findByNamedQuery("Concept.findRoot", new HashMap<String, Object>());
        if (roots.size() > 1) {
            throw new VARSPersistenceException("ERROR!! More than one root was found in the knowedgebase");
        }
        else if (roots.size() == 0) {
            throw new VARSPersistenceException("ERROR!! No root was found in the knowedgebase");
        }
        return roots.get(0);
    }

    public Concept findByName(String name) {
        ConceptName conceptName = conceptNameDAO.findByName(name);
        return conceptName == null ? null : conceptName.getConcept();
    }

    public Collection<ConceptName> findDescendentNames(Concept concept) {

        Collection<ConceptName> conceptNames = new ArrayList<ConceptName>();

        startTransaction();
        Concept mergedConcept = findByPrimaryKey(ConceptImpl.class, ((JPAEntity) concept).getId());
        conceptNames.addAll(mergedConcept.getConceptNames());
        findDescendentNames(mergedConcept.getChildConcepts(), conceptNames);
        endTransaction();

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
            conceptNames.addAll(concept.getConceptNames());
            findDescendentNames(concept.getChildConcepts(), conceptNames);
        }
    }


    public Collection<Concept> findAll() {
        Map<String, Object> params = new HashMap<String, Object>();
        // TODO this may not be memory efficient (may return lots of disconnected objet graphs
        return findByNamedQuery("Concept.findAll", params);
    }

    public Collection<Concept> findDescendents(Concept concept) {
        Collection<Concept> concepts = new ArrayList<Concept>();
        startTransaction();
        Concept mergedConcept = findByPrimaryKey(ConceptImpl.class, ((JPAEntity) concept).getId());
        findDescendents(mergedConcept, concepts);
        endTransaction();

        return concepts;
    }

    private void findDescendents(Concept concept, Collection<Concept> concepts) {
        concepts.add(concept);
        for (Concept child : concept.getChildConcepts()) {
            findDescendents(child, concepts);
        }
    }

    public Concept addConceptName(Concept concept, ConceptName conceptName) {
        startTransaction();
        concept.addConceptName(conceptName);
        concept = makePersistent(concept);
        endTransaction();
        return concept;
    }

}
