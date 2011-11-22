/*
 * @(#)UserAccountComboBox.java   2009.10.01 at 04:57:37 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.shared.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.mbari.swing.FancyComboBox;
import org.mbari.swing.SortedComboBoxModel;
import org.mbari.text.IgnoreCaseToStringComparator;
import vars.MiscDAOFactory;
import vars.UserAccount;
import vars.UserAccountDAO;

/**
 * Use as:
 * <pre>
 * UserAccountComboBox cb = new UserAccountComboBox();
 * try {
 *     cb.update(); // Populates the cb from the database
 * }
 * catch (Exception e) {
 *     // Handle exception
 * }
 * UserAccount userAccount = cb.getSelectedUserAccount();
 * </pre>
 * @author brian
 *
 */
public class UserAccountComboBox extends FancyComboBox {

    private static final Comparator comparator = new IgnoreCaseToStringComparator();
    private static final Comparator<UserAccount> userAccountComparator = new Comparator<UserAccount>() {
        public int compare(UserAccount user1, UserAccount user2) {
            return comparator.compare(user1.getUserName(), user2.getUserName());
        }

    };
    private final List<UserAccount> userAccounts = Collections.synchronizedList(new ArrayList<UserAccount>());
    private final MiscDAOFactory miscDAOFactory;

    /**
     * Constructs ...
     *
     * @param miscDAOFactory
     */
    public UserAccountComboBox(MiscDAOFactory miscDAOFactory) {
        super();
        this.miscDAOFactory = miscDAOFactory;
        setComparator(comparator);
    }

    /**
     *
     * @return The selected userAccount. null if none is selected.
     */
    public UserAccount getSelectedUserAccount() {
        UserAccount userAccount = null;
        final String userName = (String) getSelectedItem();
        synchronized (userAccounts) {
            final int index = Collections.binarySearch(userAccounts, userName, comparator);
            if (index > -1) {
                userAccount = userAccounts.get(index);
            }
        }

        return userAccount;
    }

    /**
     * Sets the user name selected in the combo box. If an account matching the name does not exist then it is
     * set to the first element.
     * @param userName
     */
    public void setSelectedUserName(String userName) {
        final SortedComboBoxModel model = (SortedComboBoxModel) getModel();
        if (model.contains(userName)) {
            model.setSelectedItem(userName);
        }
        else {
            model.setSelectedItem(model.getElementAt(0));
        }
    }

    /**
     * Updates the combo box with the useraccount information stored in the database.
     */
    public void update() {
        SortedComboBoxModel model = (SortedComboBoxModel) getModel();

        // Get potential user names and add them to the comboBox
        synchronized (userAccounts) {
            userAccounts.clear();
            UserAccountDAO userAccountDAO = miscDAOFactory.newUserAccountDAO();
            userAccountDAO.startTransaction();
            Collection<UserAccount> accounts = userAccountDAO.findAll();
            userAccountDAO.endTransaction();
            userAccountDAO.close();

            userAccounts.addAll(accounts);
            Collections.sort(userAccounts, userAccountComparator);

            /*
             * Populate the comboBox with all the String names of the useraccounts
             */
            List<String> names = new ArrayList<String>();
            for (UserAccount userAccount : userAccounts) {
                names.add(userAccount.getUserName());
            }

            model.setItems(names);

        }

        /*
         * if the System.getProperty("user.name") is one of the items in
         * the list of users, select it.
         */
        setSelectedUserName(System.getProperty("user.name"));

    }
}
