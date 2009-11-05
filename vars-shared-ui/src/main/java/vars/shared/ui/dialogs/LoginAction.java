/*
 * @(#)LoginAction.java   2009.10.02 at 08:58:59 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.shared.ui.dialogs;

import java.awt.Frame;
import org.mbari.awt.event.ActionAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.MiscDAOFactory;
import vars.MiscFactory;
import vars.UserAccount;
import vars.UserAccountDAO;
import vars.shared.ui.GlobalLookup;

/**
 * Displays a login dialog and registers the resulting UserAccount in the
 * {@link GlobalLookup}
 *
 * @version    $Id: LoginAction.java 295 2006-07-06 23:47:31Z hohonuuli $
 * @author     <a href="http://www.mbari.org">Monterey Bay Aquarium Research Institute</a>
 */
public class LoginAction extends ActionAdapter {

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Dialog for obtaining login user name and password.
     */
    private final LoginDialog loginDialog;

    /**
     * Constructs ...
     *
     * @param miscFactory
     * @param userAccountDAO
     */
    public LoginAction(MiscDAOFactory miscDAOFactory, MiscFactory miscFactory) {
        final Frame frame = (Frame) GlobalLookup.getSelectedFrameDispatcher().getValueObject();
        loginDialog = new LoginDialog(frame, true, miscDAOFactory, miscFactory);
    }

    /**
     * <p><!-- Method description --></p>
     *
     */
    public void doAction() {
        if (log.isDebugEnabled()) {
            log.debug("Attempting to log in");
        }
        loginDialog.setVisible(true);
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
    public boolean login(String userName, String password, UserAccountDAO userAccountDAO) {
        UserAccount userAccount = null;
        boolean ok = false;
        try {
            userAccount = userAccountDAO.findByUserName(userName.trim());

            if ((userAccount != null) && userAccount.authenticate(password)) {
                ok = true;
            }
            else {
                userAccount = null;
            }
        }
        catch (Exception daoe) {
            if (log.isErrorEnabled()) {
                log.error("Unable to login ", daoe);
            }
            userAccount = null;
        }
        finally {
            GlobalLookup.getUserAccountDispatcher().setValueObject(userAccount);
        }

        return ok;
    }
}
