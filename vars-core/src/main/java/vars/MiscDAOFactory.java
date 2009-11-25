package vars;

import javax.persistence.EntityManager;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 19, 2009
 * Time: 3:27:38 PM
 * To change this template use File | Settings | File Templates.
 */
public interface MiscDAOFactory {
    UserAccountDAO newUserAccountDAO();

    UserAccountDAO newUserAccountDAO(EntityManager entityManager);
}
