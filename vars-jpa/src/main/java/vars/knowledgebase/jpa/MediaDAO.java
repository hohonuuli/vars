package vars.knowledgebase.jpa;

import vars.knowledgebase.IMediaDAO;
import vars.jpa.DAO;
import org.mbari.jpax.EAO;
import com.google.inject.Inject;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 4:48:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class MediaDAO extends DAO implements IMediaDAO {

    @Inject
    public MediaDAO(EAO eao) {
        super(eao);
    }
    
}
