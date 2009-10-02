package vars;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 19, 2009
 * Time: 3:10:38 PM
 * To change this template use File | Settings | File Templates.
 */
public interface UserAccountDAO extends DAO {

    UserAccount findByUserName(String userName);

    Collection<UserAccount> findAllByLastName(String lastName);

    Collection<UserAccount> findAllByFirstName(String firstName);

    Collection<UserAccount> findAllByRole(String role);

    Collection<UserAccount> findAll();
}
