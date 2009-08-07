package vars;

import vars.IUserAccount;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 6, 2009
 * Time: 2:52:07 PM
 * To change this template use File | Settings | File Templates.
 */
public interface VARSObjectFactory {

    /* --- Misc --- */

    IUserAccount newUserAccount();
}
