package vars.knowledgebase.jpa;

import vars.knowledgebase.IConceptMetadataDAO;
import vars.jpa.DAO;
import org.mbari.jpax.IEAO;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 4:44:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConceptMetadataDAO extends DAO implements IConceptMetadataDAO {

    public ConceptMetadataDAO(IEAO eao) {
        super(eao);
    }
    
}
