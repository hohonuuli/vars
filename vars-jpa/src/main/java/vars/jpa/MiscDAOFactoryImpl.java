package vars.jpa;

import vars.MiscDAOFactory;
import vars.IUserAccountDAO;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.mbari.jpaxx.EAO;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 19, 2009
 * Time: 3:29:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class MiscDAOFactoryImpl implements MiscDAOFactory {

    private final EAO eao;

    @Inject
    public MiscDAOFactoryImpl(@Named("miscEAO") EAO eao) {
        this.eao = eao;
    }

    public IUserAccountDAO newUserAccountDAO() {
        return new UserAccountDAO(eao);
    }
}
