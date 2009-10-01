/*
 * Copyright 2005 MBARI
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1 
 * (the "License"); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package vars.knowledgebase.ui.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.util.Dispatcher;
import org.mbari.vars.dao.DAOException;
import org.mbari.vars.model.UserAccount;
import org.mbari.vars.model.dao.UserAccountDAO;
import org.mbari.vars.ui.LoginDialog;
import org.mbari.vars.util.AppFrameDispatcher;
import org.mbari.vars.knowledgebase.model.Concept;

//~--- classes ----------------------------------------------------------------

/**
 * <p><!-- Class description --></p>
 *
 * @version    $Id: LoginAction.java 295 2006-07-06 23:47:31Z hohonuuli $
 * @author     <a href="http://www.mbari.org">Monterey Bay Aquarium Research Institute</a>
 */
public class LoginAction extends ActionAdapter {

    private static final long serialVersionUID = -7928470373851496552L;

    private static final Logger log = LoggerFactory.getLogger(LoginAction.class);

    /**
     * Dialog for obtaining login user name and password.
     */
    private static LoginDialog loginDialog;

    //~--- methods ------------------------------------------------------------

    /**
     * <p><!-- Method description --></p>
     *
     */
    public void doAction() {
        if (log.isDebugEnabled()) {
            log.debug("Attempting to log in");
        }

        getLoginDialog().setVisible(true);
        
    }

    /**
     * Validates the users credentials. Sets the resulting useraccount in a
     * Dispatcher. To verify what account was set you can do the following:
     * <pre>
     *  UserAccount ua = (UserAccount) Dispatcher.getDispatcher(UserAccount.class).getValueObject();
     * </pre>
     *
     *
     * @param  userName   The name of the <code>User</code>.
     * @param  password   The password for the <code>User</code>.
     *
     *
     * @return
     */
    public static boolean login(String userName, String password) {
        UserAccount userAccount = null;
        boolean ok = false;
        try {
            userAccount = UserAccountDAO.getInstance().findByUserName(userName.trim());

            if ((userAccount != null) &&
                    userAccount.getPassword().equals(password.trim())) {
                ok = true;
            } else {
                userAccount = null;
            }
        } catch (DAOException daoe) {
            if (log.isErrorEnabled()) {
                log.error("Unable to login ", daoe);
            }

            userAccount = null;
        } finally {
            Dispatcher.getDispatcher(UserAccount.class).setValueObject(userAccount);
        }

        return ok;
    }

    public static LoginDialog getLoginDialog() {
        if (loginDialog == null) {
            loginDialog = new LoginDialog(AppFrameDispatcher.getFrame(), true);
        }
        return loginDialog;
    }
}
