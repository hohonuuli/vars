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
import java.io.IOException;
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

    private static final String PROP_LAST_VIDEO_CONNECTION_ID = "defaultVcrUrl";
    private static final String PROP_AUTOCONNECT_VCR = "autoconnectVCR";
    private static final String PROP_IMAGETARGET = "imageTarget";
    private static final String PROP_IMAGETARGETMAPPING = "imageTargetMapping";
    private static final String PROP_VCR_URL = "vcrUrl";
    private final File DEFAULT_IMAGETARGET;
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
        DEFAULT_IMAGETARGET = new File(GlobalLookup.getSettingsDirectory(), "images");

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
    public File findImageTarget(String username, String hostname) {
        Preferences preferences = hostPrefs(username, hostname);
        File imageTarget = null;
        try {
            String value = preferences.get(PROP_IMAGETARGET, DEFAULT_IMAGETARGET.getCanonicalPath());
            imageTarget = new File(value);
        }
        catch (IOException ex) {
            throw new VARSException("Failed to lookup and resolve image target", ex);
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
        URL imageTarget = null;
        try {
            String value = preferences.get(PROP_IMAGETARGETMAPPING, DEFAULT_IMAGETARGET.toURI().toURL().toExternalForm());
            imageTarget = new URL(value);
        }
        catch (MalformedURLException ex) {
            throw new VARSException("Failed to lookup and resolve image target mapping", ex);
        }

        return imageTarget;
    }

    /**
     * Retreive the port number used to access a networked VCR
     * @param username UserAccount.getUserName()
     * @param hostname preferencesService.getHostname()
     * @return
     */
    public String findVcrPort(String username, String hostname) {
        String fullUrl = findFullVcrUrl(username, hostname);
        String[] parts = fullUrl.split(":");
        return (parts.length > 1) ? parts[1] : "9000";
    }

    /**
     * Retrieve the hostname used to access a networked VCR
     * @param username UserAccount.getUserName()
     * @param hostname preferencesService.getHostname()
     * @return
     */
    public String findVcrHostname(String username, String hostname) {
        String fullUrl = findFullVcrUrl(username, hostname);
        String[] parts = fullUrl.split(":");
        return (parts.length >= 1) ? parts[0] : getHostname();
    }

    public String findFullVcrUrl(String username, String hostname) {
        Preferences preferences = hostPrefs(username, hostname);
        return preferences.get(PROP_VCR_URL, getHostname() + ":9000");
    }
    
    /**
     * Returns the default VCR URL. In general this will be the last VCR 
     * that was connected to on that host.
     * 
     * @param hostname The hostname of the current system
     * @return The VCR URL. If it contains a colon its a UDP URL otherwise
     *  it's a comm port.
     */
    public String findLastVideoConnectionId(String hostname) {
        Preferences preferences = systemPrefs(hostname);
        return preferences.get(PROP_LAST_VIDEO_CONNECTION_ID, getHostname() + ":9000");
    }
    
    /**
     * Store the default (i.e. last connect VCR URL in prefs). This should be 
     * either the comm port name or a URL in the form of 'hostname:port' (for
     * example: oyashio.shore.mbari.org:9000)
     * @param hostname
     * @param vcrUrl
     */
    public void persistLastVideoConnectionId(String hostname, String vcrUrl) {
        // If it contains a colon it's a url, otherwise it's a com port
        Preferences preferences = systemPrefs(hostname);
        preferences.put(PROP_LAST_VIDEO_CONNECTION_ID, vcrUrl);
    }
    
    /**
     * Returns the autoconnect setting
     * @param hostname
     * @return true if you should autoconnect on startup, false otherwise
     */
    public boolean findAutoconnectVcr(String hostname) {
        Preferences preferences = systemPrefs(hostname);
        String autoconnect = preferences.get(PROP_AUTOCONNECT_VCR, "false");
        return autoconnect.equals("true");
    }

    /**
     * Store the autoconnect setting
     * @param hostname
     * @param autoconnect
     */
    public void persistAutoconnectVcr(String hostname, boolean autoconnect) {
        String a = autoconnect ? "true" : "false";
        Preferences preferences = systemPrefs(hostname);
        preferences.put(PROP_AUTOCONNECT_VCR, a);
    }
    /**
     * Save the UDP VCR's information
     * @param username
     * @param hostname
     * @param vcrhost
     * @param vcrport
     */
    public void persistVcrUrl(String username, String hostname, String vcrhost, String vcrport) {
        Preferences preferences = hostPrefs(username, hostname);
        preferences.put(PROP_VCR_URL, vcrhost + ":" + vcrport);
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
     * Looks up the node that contains the preferences that apply to the 
     * current hostname irregardless of username.
     * 
     * @param hostname The name of the host
     * @return A preferences object for the given host
     */
    private Preferences systemPrefs(String hostname) {
        final Preferences systemPreferences = preferencesFactory.systemRoot().node("VARS");
        return systemPreferences.node(hostname);
    }

    /**
     * Write the URL used to write images to.
     *
     * @param username The username
     * @param hostname The current hostname of the platform that VARS is running on
     * @param targetDirectory The URL to write images to.
     */
    public void persistImageTarget(String username, String hostname, File targetDirectory) {
        Preferences preferences = hostPrefs(username, hostname);
        try {
            preferences.put(PROP_IMAGETARGET, targetDirectory.getCanonicalPath());
        } catch (IOException ex) {
            throw new VARSException("Failed to save image target to preferences");
        }
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
