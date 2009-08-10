package vars.knowledgebase.jpa;

import vars.jpa.DAO;
import vars.knowledgebase.IConceptDAO;
import vars.knowledgebase.IConcept;
import org.mbari.jpax.EAO;
import com.google.inject.Inject;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 4:43:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConceptDAO extends DAO implements IConceptDAO {

    @Inject
    public ConceptDAO(EAO eao) {
        super(eao);
    }

    public IConcept findRoot() {
        return null;  // TODO implement this method.
    }

    public IConcept findByName(String name) {
        return null;  // TODO implement this method.
    }
}
