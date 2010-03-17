/*
 * @(#)PreferencesService.java   2010.03.17 at 11:42:19 PDT
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



package vars.shared.preferences;

import com.google.inject.Inject;
import java.io.File;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;
import vars.VARSException;
import vars.shared.ui.GlobalLookup;

/**
 * A convient interface for reading/writing some specialized preferences used
 * by VARS
 * 
 * @author brian
 */
public class PreferencesService {

    private static final String PROP_IMAGETARGET = "imageTarget";
    private static final String PROP_IMAGETARGETMAPPING = "imageTargetMapping";
    private final URL DEFAULT_IMAGETARGET;
    private final PreferencesFactory preferencesFactory;
    /** The name of the computer */
    private final String hostname;

    /**
     * Constructs ...
     *
     * @param preferencesFactory
     */
    @Inject
    public PreferencesService(PreferencesFactory preferencesFactory) {
        this.preferencesFactory = preferencesFactory;

        try {
            // Call toURI first to escape out illegal URL characters
            DEFAULT_IMAGETARGET = (new File(GlobalLookup.getSettingsDirectory(), "images")).toURI().toURL();
        }
        catch (MalformedURLException ex) {
            // This should never get thrown... ;-)
            throw new VARSException("Failed to map default image target to a URL", ex);
        }

        /*
         * Store the login information
         */
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException ex) {
            // This should never get thrown... ;-)
            throw new VARSException("Unable to get hostname", ex);
        }
    }

    /**
     * Read the URL used to write images to.
     *
     * @param username
     * @param hostname
     * @return The Base URL where images should be written into
     */
    public URL findImageTarget(String username, String hostname) {
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
     * @param username The name of the user as logged into VARS
     * @return The Base URL on a web server that maps to <i>imageTarget</i>
     */
    public URL findImageTargetMapping(String username) {
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
     * /[username]/simpa/annotation/UserLookupService/[hostname]
     *
     * @param username The name of the user as logged into VARS
     * @param hostname The host name of the computer running VARS
     * @return The preference node like /[username]/vars/annotation/ui/PreferencesService/[hostname]
     */
    private Preferences hostPrefs(String username, String hostname) {
        final Preferences lookupPreferences = userPrefs(username);
        return lookupPreferences.node(hostname);
    }

    /**
     * Write the URL used to write images to.
     *
     * @param username The username
     * @param hostname The current hostname of the platform that VARS is running on
     * @param targetURL The URL to write images to.
     */
    public void persistImageTarget(String username, String hostname, URL targetURL) {
        Preferences preferences = hostPrefs(username, hostname);
        preferences.put(PROP_IMAGETARGET, targetURL.toExternalForm());
    }

    /**
     * Write the URL used to read images from a web server that were written to
     * <i>imageTarget</i>.
     *
     * @param username The name of the user as logged into VARS
     * @param targetMappingURL The Base URL on a web server that maps to <i>imageTarget</i>
     */
    public void persistImageTargetMapping(String username, URL targetMappingURL) {
        Preferences preferences = userPrefs(username);
        preferences.put(PROP_IMAGETARGETMAPPING, targetMappingURL.toExternalForm());
    }

    /**
     * Looks up the node that contains the preferences for the UserLookupService
     * given a username and hostname. THe node key is something like:
     * /[username]/simpa/annotation/UserLookupService
     *
     * @param username The name of the user as logged into VARS
     * @param hostname The host name of the computer running VARS
     * @return The preference node like /[username]/vars/annotation/ui/PreferencesService
     */
    private Preferences userPrefs(String username) {
        final Preferences userPreferences = preferencesFactory.systemRoot().node(username);
        String node = PreferencesService.class.getCanonicalName().replace(".", "/");
        return userPreferences.node(node);
    }

    public String getHostname() {
        return hostname;
    }
}
