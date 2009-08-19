package vars.jpa;

import org.mbari.jpax.EAO;
import vars.IUserAccountDAO;
import vars.IUserAccount;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 19, 2009
 * Time: 3:09:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserAccountDAO extends DAO implements IUserAccountDAO {

    public UserAccountDAO(EAO eao) {
        super(eao);
    }

    /**
     * Search for the matching username
     * @param userName The username to search for
     * @return the match, or null if no match is found
     */
    public IUserAccount findByUserName(String userName) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userName", userName);
        List<IUserAccount> accounts = getEAO().findByNamedQuery("UserAccount.findByUserName", params);
        return (accounts.size() == 0) ? null : accounts.get(0);
    }

    public Collection<IUserAccount> findAllByLastName(String lastName) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("lastName", lastName);
        return getEAO().findByNamedQuery("UserAccount.findByLastName", params);
    }

    public Collection<IUserAccount> findAllByFirstName(String firstName) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("firstName", firstName);
        return getEAO().findByNamedQuery("UserAccount.findByFirstName", params);
    }

    public Collection<IUserAccount> findAllByRole(String role) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("role", role);
        return getEAO().findByNamedQuery("UserAccount.findByRole", params);
    }

}
