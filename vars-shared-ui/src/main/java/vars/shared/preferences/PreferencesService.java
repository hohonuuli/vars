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
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.VARSException;
import vars.shared.awt.AWTUtilities;
import vars.shared.ui.GlobalStateLookup;

/**
 * A convient interface for reading/writing some specialized preferences used
 * by VARS
 * 
 * @author brian
 */
public class PreferencesService {

    private static final String PROP_LAST_VIDEO_CONNECTION_ID = "lastVideoConnectionID";
    private static final String PROP_AUTOCONNECT_VCR = "autoconnectVCR";
    private static final String PROP_IMAGETARGET = "imageTarget";
    private static final String PROP_IMAGETARGETMAPPING = "imageTargetMapping";
    private static final String PROP_IMAGEINTERPOLATION = "imageInterpolation";
    private static final String PROP_VCR_URL = "vcrUrl";
    private File defaultImageTarget;
    private URL defaultImageTargetMapping;
    private final PreferencesFactory preferencesFactory;
    /** The name of the computer */
    private String hostname;
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Constructs ...
     *
     * @param preferencesFactory
     */
    @Inject
    public PreferencesService(PreferencesFactory preferencesFactory) {
        this.preferencesFactory = preferencesFactory;
        defaultImageTarget = new File(GlobalStateLookup.getSettingsDirectory(), "images");

        /*
         * Store the login information
         */
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException ex) {
            hostname = "localhost";
            log.warn("Unable to get hostname", ex);
            //throw new VARSException("Unable to get hostname", ex);
        }

        try {
            defaultImageTarget = findDefaultImageTarget(getHostname());
            defaultImageTargetMapping = defaultImageTarget.toURI().toURL();
        }
        catch (Exception e) {
            throw new VARSException("Failed to lookup default image target", e);
        }

        try {
            defaultImageTargetMapping = findDefaultImageTargetMapping(getHostname());
        }
        catch (Exception e) {
            throw new VARSException("Failed to lookup default image target mapping", e);
        }

    }
    
    /**
     * @param username
     * @param hostname
     * @return A hint that can be used to set the image interpolation used by Java2D rendering.
     */
    public Object findImageInterpolation(String username, String hostname) {
        Preferences preferences = hostPrefs(username, hostname);
        Object value = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
        String key = preferences.get(PROP_IMAGEINTERPOLATION, value.toString());
        if (AWTUtilities.IMAGE_INTERPOLATION_MAP.containsKey(key)) {
            value = AWTUtilities.IMAGE_INTERPOLATION_MAP.get(key);
        }
        return value;
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
            String value = preferences.get(PROP_IMAGETARGET, defaultImageTarget.getCanonicalPath());
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
     * @param hostname the name of the host computer
     * @return The Base URL on a web server that maps to <i>imageTarget</i>
     */
    public URL findImageTargetMapping(String username, String hostname) {
        Preferences preferences = hostPrefs(username, hostname);
        URL imageTarget = null;
        try {
            String value = preferences.get(PROP_IMAGETARGETMAPPING, findDefaultImageTargetMapping(getHostname()).toExternalForm());
            imageTarget = new URL(value);
        }
        catch (MalformedURLException ex) {
            throw new VARSException("Failed to lookup and resolve image target mapping", ex);
        }

        return imageTarget;
    }
    
    
    /**
     * Read the default URL used to write images to.
     *
     * @param hostname
     * @return The Base URL where images should be written into
     */
    public File findDefaultImageTarget(String hostname) {
        Preferences preferences = systemPrefs(hostname);
        File imageTarget = null;
        try {
            String value = preferences.get(PROP_IMAGETARGET, defaultImageTarget.getCanonicalPath());
            imageTarget = new File(value);
        }
        catch (IOException ex) {
            throw new VARSException("Failed to lookup and resolve default image target", ex);
        }

        return imageTarget;
    }

    /**
     * Read the URL used to read images from a web server that were written to
     * <i>imageTarget</i>.
     *
     * @param hostname The computer of interest
     * @return The Base URL on a web server that maps to <i>imageTarget</i>
     */
    public URL findDefaultImageTargetMapping(String hostname) {
        Preferences preferences = systemPrefs(hostname);
        URL imageTarget = null;
        try {
            String value = preferences.get(PROP_IMAGETARGETMAPPING, defaultImageTarget.toURI().toURL().toExternalForm());
            imageTarget = new URL(value);
        }
        catch (MalformedURLException ex) {
            throw new VARSException("Failed to lookup and resolve default image target mapping", ex);
        }

        return imageTarget;
    }

    /**
     * Retrieve the port number used to access a networked VCR
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
     * Store the type of image interpolation used for rendering framegrabs
     * @param username
     * @param hostname
     * @param renderingHint The actual RenderingHint object. See RenderingHints 
     *      { VALUE_INTERPOLATION_BICUBIC, VALUE_INTERPOLATION_BILINEAR, 
     *       VALUE_INTERPOLATION_NEAREST_NEIGHBOR } for valid values.
     */
    public void persistImageInterpolation(String username, String hostname, Object renderingHint) {
        Preferences preferences = hostPrefs(username, hostname);
        if (AWTUtilities.IMAGE_INTERPOLATION_MAP.containsValue(renderingHint)) {
            preferences.put(PROP_IMAGEINTERPOLATION, renderingHint.toString());
        }
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
     * @param hostname The name of the user as logged into VARS
     * @param targetMappingURL The Base URL on a web server that maps to <i>imageTarget</i>
     */
    public void persistDefaultImageTargetMapping(String hostname, URL targetMappingURL) {
        Preferences preferences = systemPrefs(hostname);
        preferences.put(PROP_IMAGETARGETMAPPING, targetMappingURL.toExternalForm());
    }
    
    /**
     * Write the URL used to write images to.
     *
     * @param hostname The username
     * @param hostname The current hostname of the platform that VARS is running on
     * @param targetDirectory The URL to write images to.
     */
    public void persistDefaultImageTarget(String hostname, File targetDirectory) {
        Preferences preferences = systemPrefs(hostname);
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
    public void persistImageTargetMapping(String username, String hostname, URL targetMappingURL) {
        Preferences preferences = hostPrefs(username, hostname);
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
