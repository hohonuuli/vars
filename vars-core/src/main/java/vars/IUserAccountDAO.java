package vars;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 19, 2009
 * Time: 3:10:38 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IUserAccountDAO extends IDAO {

    IUserAccount findByUserName(String userName);

    Collection<IUserAccount> findAllByLastName(String lastName);

    Collection<IUserAccount> findAllByFirstName(String firstName);

    Collection<IUserAccount> findAllByRole(String role);
}
