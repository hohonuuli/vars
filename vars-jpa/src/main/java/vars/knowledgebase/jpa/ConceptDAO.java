package vars.knowledgebase.jpa;

import vars.jpa.DAO;
import vars.jpa.JPAEntity;
import vars.knowledgebase.IConceptDAO;
import vars.knowledgebase.IConcept;
import vars.knowledgebase.IConceptName;
import vars.knowledgebase.IConceptNameDAO;
import vars.VARSPersistenceException;
import org.mbari.jpax.EAO;
import org.mbari.jpax.NonManagedEAO;
import com.google.inject.Inject;

import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 4:43:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConceptDAO extends DAO implements IConceptDAO {

    private final IConceptNameDAO conceptNameDAO;

    @Inject
    public ConceptDAO(EAO eao, IConceptNameDAO conceptNameDAO) {
        super(eao);
        this.conceptNameDAO = conceptNameDAO;
    }

    public IConcept findRoot() {
        List<IConcept> roots = getEAO().findByNamedQuery("Concept.findRoot", new HashMap<String, Object>());
        if (roots.size() > 1) {
            throw new VARSPersistenceException("ERROR!! More than one root was found in the knowedgebase");
        }
        else if (roots.size() == 0) {
            throw new VARSPersistenceException("ERROR!! No root was found in the knowedgebase");
        }
        return roots.get(0);
    }

    public IConcept findByName(String name) {
        IConceptName conceptName = conceptNameDAO.findByName(name);
        return conceptName == null ? null : conceptName.getConcept();
    }

    public Collection<IConceptName> findDescendentNames(IConcept concept) {

        Collection<IConceptName> conceptNames = new ArrayList<IConceptName>();

        EAO eao = getEAO();

        // Do all lookup in a single transaction to account for lazy loading
        NonManagedEAO nmEao = null;
        if (eao instanceof NonManagedEAO) {
            nmEao = (NonManagedEAO) eao;
            nmEao.startTransaction();
        }

        IConcept mergedConcept = eao.find(Concept.class, ((JPAEntity) concept).getId());
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
    private void findDescendentNames(Collection<IConcept> concepts, Collection<IConceptName> conceptNames) {
        for (IConcept concept : concepts) {
            conceptNames.addAll(concept.getConceptNames());
            findDescendentNames(concept.getChildConcepts(), conceptNames);
        }
    }
}
