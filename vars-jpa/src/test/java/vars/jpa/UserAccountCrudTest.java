package vars.jpa;

import org.junit.Before;
import org.junit.Assert;
import org.junit.Test;
import org.mbari.jpaxx.NonManagedEAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.inject.Injector;
import com.google.inject.Guice;
import vars.UserAccount;
import vars.*;
import vars.DAO;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 19, 2009
 * Time: 3:37:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserAccountCrudTest {

    MiscFactory miscFactory;
    MiscDAOFactory daoFactory;
    NonManagedEAO eao;

    public final Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setup() {
        Injector injector = Guice.createInjector(new VarsJpaTestModule());

        miscFactory = injector.getInstance(MiscFactory.class);
        daoFactory = injector.getInstance(MiscDAOFactory.class);
        eao = injector.getInstance(MiscEAO.class);
    }

    @Test
    public void basicCrud() {
        log.info("---------- TEST: basicCrud ----------");
        String testString = "test";
        UserAccount userAccount = miscFactory.newUserAccount();
        userAccount.setPassword(testString);
        log.info("Password '" + testString + "' encrypted as '" + userAccount.getPassword() + "'");
        userAccount.setUserName(testString);
        userAccount.setRole(UserAccountRoles.ADMINISTRATOR.getRoleName());
        DAO dao = daoFactory.newUserAccountDAO();
        dao.makePersistent(userAccount);
        Assert.assertNotNull(((JPAEntity) userAccount).getId());

        userAccount = dao.findByPrimaryKey(GUserAccount.class, ((JPAEntity) userAccount).getId());
        log.info("Password stored in database as '" + userAccount.getPassword() + "'");
        Assert.assertEquals("UserName wasn't stored correctly", testString, userAccount.getUserName());
        Assert.assertEquals("Role wasn't stored correctly", UserAccountRoles.ADMINISTRATOR.getRoleName(),
                userAccount.getRole());
        Assert.assertTrue("Couldn't authenticate", userAccount.authenticate(testString));

        dao.makeTransient(userAccount);

        Assert.assertNull("Primary key wasn't reset on delete", ((JPAEntity) userAccount).getId());
    }
}
