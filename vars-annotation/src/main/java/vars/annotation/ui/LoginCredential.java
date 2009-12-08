/*
 * @(#)LoginCredential.java   2009.12.05 at 10:54:16 PST
 *
 * Copyright 2009 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package vars.annotation.ui;

/**
 * Encapsulates the login credentials of a user
 * @author brian
 */
public class LoginCredential {

    private final String hostName;
    private final String login;
    private final char[] password;

    /**
     * Constructs ...
     *
     * @param login
     * @param password
     * @param hostName
     */
    public LoginCredential(String login, char[] password, String hostName) {
        this.login = login;
        this.password = password;
        this.hostName = hostName;
    }

    /**
     *
     * @return The specified hostName. For the simpa apps this will be the computer
     *      running the applications
     */
    public String getHostName() {
        return hostName;
    }

    /**
     *
     * @return The login name of the user
     */
    public String getLogin() {
        return login;
    }

    /**
     *
     * @return The password used for login
     */
    public char[] getPassword() {
        return password;
    }
}
