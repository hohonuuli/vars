package vars.jpa;

import vars.MiscFactory;
import vars.UserAccount;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 19, 2009
 * Time: 3:29:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class MiscFactoryImpl implements MiscFactory {

    public UserAccount newUserAccount() {
        return new UserAccountImpl();
    }
}
