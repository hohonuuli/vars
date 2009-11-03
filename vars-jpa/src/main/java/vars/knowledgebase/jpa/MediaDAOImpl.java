package vars.knowledgebase.jpa;

import vars.knowledgebase.MediaDAO;
import vars.jpa.DAO;
import com.google.inject.Inject;
import javax.persistence.EntityManager;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 4:48:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class MediaDAOImpl extends DAO implements MediaDAO {

    @Inject
    public MediaDAOImpl(EntityManager entityManager) {
        super(entityManager);
    }
    
}
