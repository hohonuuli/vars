package vars.knowledgebase.jpa;

import vars.jpa.DAO;
import vars.knowledgebase.ConceptNameDAO;
import vars.knowledgebase.ConceptName;

import java.util.HashMap;
import java.util.List;

import com.google.inject.Inject;
import java.util.Collection;
import java.util.Map;
import javax.persistence.EntityManager;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 4:45:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConceptNameDAOImpl extends DAO implements ConceptNameDAO {

    @Inject
    public ConceptNameDAOImpl(EntityManager entityManager) {
        super(entityManager);
    }

    public ConceptName findByName(final String name) {
        List<ConceptName> names = findByNamedQuery("ConceptName.findByName", new HashMap<String, Object>() {{ put("name", name);}} );
        return names.size() == 0 ? null : names.get(0);
    }

    public Collection<ConceptName> findAll() {
        Map<String, Object> params = new HashMap<String, Object>();
        return findByNamedQuery("ConceptName.findAll", params);
    }

    public Collection<ConceptName> findByNameContaining(final String substring) {
        Map<String, Object> params = new HashMap<String, Object>() {{ put("name", "%" + substring.toLowerCase() + "%"); }};
        return findByNamedQuery("ConceptName.findByNameLike", params);
    }

    public Collection<ConceptName> findByNameStartingWith(final String s) {
        Map<String, Object> params = new HashMap<String, Object>() {{ put("name", s.toLowerCase() + "%"); }};
        return findByNamedQuery("ConceptName.findByNameLike", params);
    }
}
