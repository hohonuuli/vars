package vars.knowledgebase.jpa;

import vars.knowledgebase.IConceptMetadataDAO;
import vars.jpa.DAO;
import org.mbari.jpax.EAO;
import com.google.inject.Inject;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 4:44:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConceptMetadataDAO extends DAO implements IConceptMetadataDAO {

    @Inject
    public ConceptMetadataDAO(EAO eao) {
        super(eao);
    }
    
}
