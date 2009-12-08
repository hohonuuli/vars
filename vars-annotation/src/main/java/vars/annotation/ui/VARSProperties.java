/*
 * VARSProperties.java
 *
 * Created on November 23, 2005, 12:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package vars.annotation.ui;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.ui.Lookup;

/**
 * Retrieve VARS properties. Most of the properties are configured in the file 'vars.properties'. However,
 * Some can be overridden on the command line with the -D switch. This class first checks the System properties
 * for a given property, if it's not foudn there it then checks the vars.properties file.
 * @author brian
 */
public class VARSProperties {
    
    /**
     * Stores cameraplatform, ship pairs. For example a vars.properties file with
     * the lines:
     * <pre>
     *  cameraplatform.0 = Tiburon
     *  ship.0 = Western Flyer
     * </pre>
     * would be store in this map as 
     * <pre>platforms.put("Tiburon", "Western Flyer");</pre>
     *
     * Map<String, String>. key = cameraplatform, value = ship
     */
    private static Map<String, String> platforms;
    
    private static final Logger log = LoggerFactory.getLogger(VARSProperties.class);
    
    private static String deploymentLocale;
    
    private static String imageCopyrightOwner;
    
    /**
     * This is the name of the VCR URL used to connect to remote VCRs
     */
    private static String vcrUrl;
    
    /**
     * This is the port # used to connect to remote VCRs
     */
    private static int vcrPort;
    
    /**
     * The directory that images will be copied to.
     */
    private static String imageArchiveDirectory;
    
    /**
     * The URL on a web server that corresponds to imageArchiveDirectory
     */
    private static String imageArchiveURL;
    
            
    
    static {
        /*
         * Fetch all the keys from the vars.properties file and look for the 
         * cameraplatform keys. Sort by name.
         */
        platforms = new HashMap<String, String>();
        ResourceBundle bundle = ResourceBundle.getBundle(Lookup.RESOURCE_BUNDLE);
        Enumeration<String> keys = bundle.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            if (key.startsWith("cameraplatform")) {
                String cameraPlatform = bundle.getString(key);
                log.debug("Found Camera platform: " + key + " = " + cameraPlatform);
                String idx = key.substring(key.indexOf("."));
                String shipKey = "ship" + idx;
                String ship = "";
                try {
                    log.debug("Looking for " + shipKey + " in " + Lookup.RESOURCE_BUNDLE);
                    ship = bundle.getString(shipKey);
                }
                catch (MissingResourceException e) {
                    log.info("Failed to find key '" + shipKey + "' in " + Lookup.RESOURCE_BUNDLE);
                    /*
                     * If we fail to find a corresponding ship just use the
                     * cameraPlatform as the ship name.
                     */
                    ship = cameraPlatform;
                }
                platforms.put(cameraPlatform, ship);
            }
        }    
        
        /*
         * Get the deployment.locale. This is the location that VARS is instaled
         * at.
         */
        try {
            deploymentLocale = getProperty("deployment.locale");
        } 
        catch (MissingResourceException e) {
            log.info("The property 'deployment.locale' was not found in " + Lookup.RESOURCE_BUNDLE);
            deploymentLocale = null;
        }
        
        try {
            imageCopyrightOwner = getProperty("image.copyright.owner");
        } 
        catch (MissingResourceException e) {
            log.info("The property 'image.copyright.owner' was not found in " + Lookup.RESOURCE_BUNDLE);
            imageCopyrightOwner = "";
        }
        
        try {
            String vcrLocation = getProperty("vcr.url");
            int i = vcrLocation.indexOf(":");
            vcrUrl = vcrLocation.substring(0, i);
            if (i > -1) {
                vcrPort = Integer.parseInt(vcrLocation.substring(i + 1, vcrLocation.length()));
            }
            else {
                vcrPort = 9000;
            }
        }
        catch (MissingResourceException e) {
            log.info("The property 'vcr.url' was not found in " + Lookup.RESOURCE_BUNDLE);
            vcrUrl = null;
            vcrPort = -1;
        }
        
        try {
            imageArchiveDirectory = getProperty("image.archive.dir");
        }
        catch (MissingResourceException e) {
            log.info("The property 'image.archive.dir' was not found in " + Lookup.RESOURCE_BUNDLE);
        }
        
        try {
            imageArchiveURL = getProperty("image.archive.url");
        }
        catch (MissingResourceException e) {
            log.info("The property 'image.archive.url' was not found in + " + Lookup.RESOURCE_BUNDLE);
        }
        

    }
    
    private VARSProperties() {
        // No instantiation allowed
    }
    
    /**
     * This method checks the system properties for a giving line first, in case any were set at the command line.
     * If none are found it trys to get them from the vars.properties file.
     * 
     * @param key The key to search for
     * @return The property for the key
     * @throws MissingResourceException If no property is found for the key
     */
    private static String getProperty(String key) {
        String property = System.getProperty(key);
        if (property == null) {
            ResourceBundle bundle = ResourceBundle.getBundle("vars");
            property = bundle.getString(key);
        }
        if (log.isDebugEnabled()) {
            log.debug("VARS Property found: " + key + " = " + property);
        }
        return property;
    }
    
    /**
     * @return The collection of cameraplatforms found in the vars.properties file.
     * The collecition is Collection<String>
     */
    public static Collection getCameraPlatforms() {
        return platforms.keySet();
    }
    
    /**
     * @return the collection of ships found in the vars.properties file. The 
     * collection is a Collection<String>
     */
    public static Collection getShips() {
        return platforms.values();
    }
    
    /**
     * @return the Ship that corresponds to the cameraplatform.
     */
    public static String getShip(String cameraPlatform) {
        return (String) platforms.get(cameraPlatform);
    }
    
    /**
     * @return the Camera platform the corresponds to the ship.
     */
    public static String getCameraPlatform(String ship) {
        String cameraPlatform = null;
        if (ship != null) {
            for (Iterator i = platforms.keySet().iterator(); i.hasNext();) {
                cameraPlatform = (String) i.next();
                String matchingShip = (String) platforms.get(cameraPlatform);
                if (ship.equals(matchingShip)) {
                    break;
                }
            }
        }
        return cameraPlatform;
    }
    
    /**
     * Retrieves the deployment locale as specified by the 'deployment.locale'
     * property in 'vars.properties. This should be the location that VARS is 
     * installed to. Typically this is used to indicate whether VARS is installed
     * on a ship or not. 
     *
     * @return The deployment.locale property or null if it is not specified in
     *  'vars.properties'
     */
    public static String getDeploymentLocale() {
        return deploymentLocale;
    }

    public static String getImageCopyrightOwner() {
        return imageCopyrightOwner;
    }

    /**
     * @return Returns the vcrPort.
     */
    public static int getVcrPort() {
        return vcrPort;
    }

    /**
     * @return Returns the vcrUrl.
     */
    public static String getVcrUrl() {
        return vcrUrl;
    }
    
    public static String getImageArchiveURL() {
        return imageArchiveURL;
    }
    
    public static String getImageArchiveDirectory() {
        return imageArchiveDirectory;
    }
    
    
}
