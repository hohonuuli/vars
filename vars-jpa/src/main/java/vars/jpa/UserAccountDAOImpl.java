/*
 * @(#)UserAccountDAOImpl.java   2009.09.02 at 04:26:52 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.jpa;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mbari.jpaxx.EAO;
import vars.UserAccount;
import vars.UserAccountDAO;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 19, 2009
 * Time: 3:09:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserAccountDAOImpl extends DAO implements UserAccountDAO {

    /**
     * Constructs ...
     *
     * @param eao
     */
    public UserAccountDAOImpl(EAO eao) {
        super(eao);
    }

    public Collection<UserAccount> findAllByFirstName(String firstName) {
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("firstName", firstName);

        return getEAO().findByNamedQuery("UserAccount.findByFirstName", params);
    }

    public Collection<UserAccount> findAllByLastName(String lastName) {
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("lastName", lastName);

        return getEAO().findByNamedQuery("UserAccount.findByLastName", params);
    }

    public Collection<UserAccount> findAllByRole(String role) {
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("role", role);

        return getEAO().findByNamedQuery("UserAccount.findByRole", params);
    }

    /**
     * Search for the matching username
     * @param userName The username to search for
     * @return the match, or null if no match is found
     */
    public UserAccount findByUserName(String userName) {
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("userName", userName);

        List<UserAccount> accounts = getEAO().findByNamedQuery("UserAccount.findByUserName", params);

        return (accounts.size() == 0) ? null : accounts.get(0);
    }
}
