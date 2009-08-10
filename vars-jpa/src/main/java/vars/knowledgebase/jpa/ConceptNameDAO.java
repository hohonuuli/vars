package vars.knowledgebase.jpa;

import vars.jpa.DAO;
import vars.knowledgebase.IConceptNameDAO;
import org.mbari.jpax.EAO;

import java.util.Set;

import com.google.inject.Inject;

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

    public Set<String> findAllConceptNamesUsedInAnnotations() {
        return null;  // TODO implement this method.
    }
}
