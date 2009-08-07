package vars.knowledgebase.jpa;

import vars.jpa.DAO;
import vars.knowledgebase.IConceptNameDAO;
import org.mbari.jpax.IEAO;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 4:45:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConceptNameDAO extends DAO implements IConceptNameDAO {

    public ConceptNameDAO(IEAO eao) {
        super(eao);
    }

    public Set<String> findAllConceptNamesUsedInAnnotations() {
        return null;  // TODO implement this method.
    }
}
