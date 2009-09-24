package vars.knowledgebase.jpa;

import vars.jpa.DAO;
import vars.jpa.JPAEntity;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameDAO;
import vars.VARSPersistenceException;
import org.mbari.jpax.EAO;
import org.mbari.jpax.NonManagedEAO;
import com.google.inject.Inject;

import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 4:43:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConceptDAOImpl extends DAO implements ConceptDAO {

    private final ConceptNameDAO conceptNameDAO;

    @Inject
    public ConceptDAOImpl(EAO eao, ConceptNameDAO conceptNameDAO) {
        super(eao);
        this.conceptNameDAO = conceptNameDAO;
    }

    public Concept findRoot() {
        List<Concept> roots = getEAO().findByNamedQuery("Concept.findRoot", new HashMap<String, Object>());
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

        EAO eao = getEAO();

        // Do all lookup in a single transaction to account for lazy loading
        NonManagedEAO nmEao = null;
        if (eao instanceof NonManagedEAO) {
            nmEao = (NonManagedEAO) eao;
            nmEao.startTransaction();
        }

        Concept mergedConcept = eao.find(Concept.class, ((JPAEntity) concept).getId());
        conceptNames.addAll(mergedConcept.getConceptNames());
        findDescendentNames(mergedConcept.getChildConcepts(), conceptNames);

        if (nmEao != null) {
            nmEao.endTransaction();
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
            conceptNames.addAll(concept.getConceptNames());
            findDescendentNames(concept.getChildConcepts(), conceptNames);
        }
    }


    public Collection<Concept> findAll() {
        Map<String, Object> params = new HashMap<String, Object>();
        return getEAO().findByNamedQuery("Concept.findAll", params);
    }



}
