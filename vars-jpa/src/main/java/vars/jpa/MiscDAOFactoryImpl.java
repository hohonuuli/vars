package vars.jpa;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import javax.persistence.EntityManagerFactory;
import vars.MiscDAOFactory;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 19, 2009
 * Time: 3:29:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class MiscDAOFactoryImpl implements MiscDAOFactory {

    private final EntityManagerFactory entityManagerFactory;

    @Inject
    public MiscDAOFactoryImpl(@Named("miscEAO") EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public vars.UserAccountDAO newUserAccountDAO() {
        return new UserAccountDAOImpl(entityManagerFactory.createEntityManager());
    }
}
