package vars.knowledgebase.jpa;

import vars.knowledgebase.ConceptMetadataDAO;
import vars.jpa.DAO;
import org.mbari.jpaxx.EAO;
import com.google.inject.Inject;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 4:44:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConceptMetadataDAOImpl extends DAO implements ConceptMetadataDAO {

    @Inject
    public ConceptMetadataDAOImpl(EAO eao) {
        super(eao);
    }
    
}
