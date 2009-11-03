package vars.knowledgebase.jpa;

import vars.knowledgebase.UsageDAO;
import vars.jpa.DAO;
import com.google.inject.Inject;
import javax.persistence.EntityManager;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 4:48:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class UsageDAOImpl extends DAO implements UsageDAO {

    @Inject
    public UsageDAOImpl(EntityManager entityManager) {
        super(entityManager);
    }

}
