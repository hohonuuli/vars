package vars.knowledgebase.jpa;

import vars.jpa.DAO;
import vars.knowledgebase.IConceptNameDAO;
import vars.knowledgebase.IConceptName;
import org.mbari.jpax.EAO;

import java.util.HashMap;
import java.util.List;

import com.google.inject.Inject;
import java.util.Collection;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 4:45:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConceptNameDAO extends DAO implements IConceptNameDAO {

    @Inject
    public ConceptNameDAO(EAO eao) {
        super(eao);
    }

    public IConceptName findByName(final String name) {
        List<IConceptName> names = getEAO().findByNamedQuery("ConceptName.findByName", new HashMap<String, Object>() {{ put("name", name);}} );
        return names.size() == 0 ? null : names.get(0);
    }

    public Collection<IConceptName> findAll() {
        Map<String, Object> params = new HashMap<String, Object>();
        return getEAO().findByNamedQuery("ConceptName.findAll", params);
    }
}
