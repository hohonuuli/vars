/*
 * @(#)UserAccountService.java   2009.12.23 at 11:29:05 PST
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



package vars;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.prefs.Preferences;

/**
 * Class with convienience methods for reading and writing
 * @deprecated 
 * @author brian
 */
public class UserAccountService {

    private static final String PROP_IMAGETARGET = "imageTarget";
    private static final String PROP_IMAGETARGETMAPPING = "imageTargetMapping";
    private static URL DEFAULT_IMAGETARGET;
    private final VarsUserPreferencesFactory preferencesFactory;

    /**
     * Constructs ...
     *
     * @param preferencesFactory
     */
    public UserAccountService(VarsUserPreferencesFactory preferencesFactory) {
        this.preferencesFactory = preferencesFactory;
    }

    /**
     * Looks up the node that contains the preferences for the UserAccountService
     * given a username and hostname. THe node key is something like:
     * /[username]/simpa/annotation/UserLookupService/[hostname]
     *
     * @param username The name of the user as logged into SIMPA
     * @param hostname The host name of the computer running SIMPA
     * @return The preference node like /[username]/simpa/annotation/UserAccountService/[hostname]
     */
    private Preferences hostPrefs(String username, String hostname) {
        final Preferences lookupPreferences = userPrefs(username);
        return lookupPreferences.node(hostname);
    }

    /**
     * Read the URL used to write images to.
     *
     * @param username
     * @param hostname
     * @return The Base URL where images should be written into
     */
    public URL readImageTarget(String username, String hostname) {
        Preferences preferences = hostPrefs(username, hostname);
        String value = preferences.get(PROP_IMAGETARGET, DEFAULT_IMAGETARGET.toExternalForm());
        URL imageTarget = null;
        try {
            imageTarget = new URL(value);
        }
        catch (MalformedURLException ex) {
            throw new VARSException("Failed to convert '" + value + "' to a URL", ex);
        }

        return imageTarget;
    }

    /**
     * Read the URL used to read images from a web server that were written to
     * <i>imageTarget</i>.
     *
     * @param username The name of the user as logged into SIMPA
     * @return The Base URL on a web server that maps to <i>imageTarget</i>
     */
    public URL readImageTargetMapping(String username) {
        Preferences preferences = userPrefs(username);
        String value = preferences.get(PROP_IMAGETARGETMAPPING, DEFAULT_IMAGETARGET.toExternalForm());
        URL imageTarget = null;
        try {
            imageTarget = new URL(value);
        }
        catch (MalformedURLException ex) {
            throw new VARSException("Failed to convert '" + value + "' to a URL", ex);
        }

        return imageTarget;
    }

    /**
     * Looks up the node that contains the preferences for the UserLookupService
     * given a username and hostname. THe node key is something like:
     * /[username]/simpa/annotation/UserAccountService
     *
     * @param username The name of the user as logged into SIMPA
     * @param hostname The host name of the computer running SIMPA
     * @return The preference node like /[username]/simpa/annotation/UserAccountService
     */
    private Preferences userPrefs(String username) {
        final Preferences userPreferences = preferencesFactory.userRoot(username);
        String node = UserAccountService.class.getCanonicalName().replace(".", "/");
        return userPreferences.node(node);
    }

    /**
     * Write the URL used to write images to.
     *
     * @param username The username
     * @param hostname The current hostname fo the platform that SIMPA is running on
     * @param targetURL The URL to write images to.
     */
    public void writeImageTarget(String username, String hostname, URL targetURL) {
        Preferences preferences = hostPrefs(username, hostname);
        preferences.put(PROP_IMAGETARGET, targetURL.toExternalForm());
    }

    /**
     * Write the URL used to read images from a web server that were written to
     * <i>imageTarget</i>.
     *
     * @param username The name of the user as logged into SIMPA
     * @param targetMappingURL The Base URL on a web server that maps to <i>imageTarget</i>
     */
    public void writeImageTargetMapping(String username, URL targetMappingURL) {
        Preferences preferences = userPrefs(username);
        preferences.put(PROP_IMAGETARGETMAPPING, targetMappingURL.toExternalForm());
    }
}
