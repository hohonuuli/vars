package vars.jpa;


import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.mbari.jpaxx.EAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 25, 2009
 * Time: 9:49:54 AM
 * To change this template use File | Settings | File Templates. 
 */
class VarsUserPreferences extends AbstractPreferences {

    private final EAO eao;
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * This is a default constructor that simply calls another constructor.  This is designed to
     * construct a preferences object that has the whole preferences tree (at the root with all
     * users)
     */
    @Inject
    public VarsUserPreferences(@Named("miscEAO") EAO eao) {
        this(eao, null, "");
    }

    /**
     * This is the main constructor that takes in a VARSPreferences object that will serve
     * as the parent preference to the new VARSPreference that will be created with the
     * name that is passed in
     *
     * @param  parent This is the VARSPreferences object that will be the parent of the new
     * VARSPreferences object created with this constructor
     * @param  name This is the name that will be used for the new VARSPreferences node
     */
    public VarsUserPreferences(EAO eao, VarsUserPreferences parent, String name) {
        super(parent, name);
        this.eao = eao;
    }

        /**
     * This method puts the give key/value association into this preference node (and persists it to the database)
     *
     * @param  key This is the key that will be used to insert and extract the associated value
     * from the database
     * @param  value This is the value that is associated with the give key and will be stored
     * in the database
     */
    protected void putSpi(String key, String value) {
        /*
         * Check to see if the node/key combo exists in the database already. If so
         * we update it with the new value. If not we insert a record.
         */
        try {
            PreferenceNode node = findByKey(key);
            if (node == null) {
                node = new PreferenceNode();
                node.setNodeName(absolutePath());
                node.setPrefKey(key);
                node.setPrefValue(value);
                eao.insert(node);
            }
            else {
                if (!value.equals(node.getPrefValue())) {
                    node.setPrefValue(value);
                    eao.update(node);
                }
            }
        }
        catch (Exception e) {
            log.error("Failed to on call of putSpi(" + key + ", " + value + ")",
                    e);
        }
    }

     /**
     * This method returns the value associated with the specified key at the
     * preference node, or <code>null</code> if there is no association for this key,
     * or the association cannot be determined at this time.  It is guaranteed that
     * <code>key</code> is non-null.  Also, it is guaranteed that this node has not
     * been removed.
     *
     * @param  key The key to be searched for
     * @return  A String that is the value associated with the given key
     */
    protected String getSpi(String key) {
        String prefValue = null;
        try {
            PreferenceNode node = findByKey(key);
            if (node != null) {
                prefValue = node.getPrefValue();
            }
        }
        catch (Exception e) {
            log.error("Call of getSpi(" + key + ") failed", e);
        }

        return prefValue;
    }

    /**
     *  Remove the association (if any) for the specified key at this preference node.  It is
     * guaranteed that <code>key</code> is non-null.  Also, it is guaranteed that this node
     * has not been removed.
     *
     * @param  key This is the key (name) of the preference to be removed
     */
    protected void removeSpi(String key) {
        try {
            PreferenceNode node = findByKey(key);
            eao.delete(node);
        }
        catch (Exception e) {
            log.error("Failed to retrieve keys for " + absolutePath(), e);
        }
    }

    /**
     * This method rmoves this prefernce node, invalidating it and any prefernces that it contains.  The named
     * child will have no descendants at the time this invocation is made (the removeNode() will be called
     * recursively from the bottom up.
     *
     * @throws  BackingStoreException - if something goes wrong with the removal from the back end storage and
     * this operation cannot be peformed successfully
     */
    protected void removeNodeSpi() throws BackingStoreException {
        try {
            List<PreferenceNode> preferenceNodes = findByNodeNameLike(absolutePath());
            for(PreferenceNode node : preferenceNodes) {
                eao.delete(node);
            }
        }
        catch (Exception e) {
            log.error("Failed to retrieve keys for " + absolutePath(), e);
            throw new BackingStoreException(e);
        }
    }

    /**
     * Returns all of the keys that have an associated value in this preference node.  The returned array will
     * have size of zero if this node has no preferences.
     *
     * @return  String[] that contains the keys of the preferences that have associated values at this node
     * @throws  BackingStoreException - if something goes wrong and the operation cannot be completed successfully
     */
    protected String[] keysSpi() throws BackingStoreException {
        ArrayList keys = new ArrayList();
        try {
            Collection<PreferenceNode> preferenceNodes = findByNodeName(absolutePath());
            for (PreferenceNode node : preferenceNodes) {
                keys.add(node.getPrefKey());
            }
        }
        catch (Exception e) {
            log.error("Failed to retrieve keys for " + absolutePath(), e);
            throw new BackingStoreException(e);
        }

        return (String[]) keys.toArray(new String[keys.size()]);
    }

/**
     * This method returns an array of strings that represent the names of the children that are directly
     * below this node.
     *
     * @return  Description of the Return Value
     * @returns  String [] which is the list of children node names directly below this node
     * @throws  BackingStoreException - if anything goes wrong and the operation cannot complete
     */
    protected String[] childrenNamesSpi() throws BackingStoreException {
        List keys = new ArrayList();
        String parentNode = absolutePath();
        try {

            List<PreferenceNode> preferenceNodes = findByNodeNameLike(parentNode);
            for (PreferenceNode node : preferenceNodes) {
                 // Grab the next child name
                String nodeName = node.getNodeName();

                // The first thing to do is strip off the base path
                String childPath = nodeName.substring(parentNode.length(), nodeName.length());

                // Now check to see if the child without base name starts with a / and if so,
                // strip it off
                if (childPath.startsWith("/")) {
                    childPath = childPath.substring(1, childPath.length());
                }

                // Now we must take only the part up to the first / (or the whole thing if there is no /)
                String bareName = null;
                if (childPath.indexOf("/") >= 0) {
                    bareName = childPath.substring(0, childPath.indexOf("/"));
                }
                else {
                    bareName = childPath;
                }

                if ((!keys.contains(bareName)) && (bareName.compareTo("") != 0)) {
                    keys.add(bareName);
                }
            }

        }
        catch (Exception e) {
            log.error("Failed to retrieve keys for " + absolutePath(), e);
            throw new BackingStoreException(e);
        }

        return (String[]) keys.toArray(new String[keys.size()]);
    }


    /**
     * This method is used to get a child node with a specific name from the current node
     *
     * @param  name The name of the child node to return from the current node
     * @return  AbstractPreferences This is the new VARSPreferences that has the name supplied
     * in the parameter.  If no node exists, one will be created.
     */
    protected AbstractPreferences childSpi(String name) {
        // Return a new VARSPreferences with the name of the node
        return new VarsUserPreferences(eao, this, name);
    }

    protected void syncSpi() {
        // Do nothing
    }

    protected void flushSpi() {
        // No implementation
    }

    /**
     * This method is used to copy one preference 'tree' to another.  It provides a 'deep' copy.
     *
     * @param  source This is the Preferences object that contains the preferences object you want to copy
     * @param  destination This is the Preferences object that contains the preferences object you want to
     * copy to
     */
    public static void copyPrefs(Preferences source, Preferences destination) {
        try {
            // Copy the key/value pairs first
            String[] prefKeys = source.keys();
            for (int i = 0; i < prefKeys.length; i++) {
                destination.put(prefKeys[i], source.get(prefKeys[i], ""));
            }

            // Now grab all the names of the children nodes
            String[] childrenNames = source.childrenNames();

            // Recursively copy the preference nodes
            for (int i = 0; i < childrenNames.length; i++) {
                copyPrefs(source.node(childrenNames[i]),
                        destination.node(childrenNames[i]));
            }
        }
        catch (Exception e) {
            //log.error("Failed to copy preferences.", e);
        }
    }

    private PreferenceNode findByKey(String key) {
        PreferenceNode node = null;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("nodeName", absolutePath());
        params.put("prefKey", key);
        List<PreferenceNode> preferenceNodes = eao.findByNamedQuery("PreferenceNode.findByNodeNameAndPrefKey", params);
        if (preferenceNodes.size() > 0) {
            node = preferenceNodes.get(0);
        }
        return node;
    }

    private List<PreferenceNode> findByNodeName(String nodeName) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("nodeName", nodeName);
        return eao.findByNamedQuery("PreferenceNode.findAllByNodeName", params);
    }

    private List<PreferenceNode> findByNodeNameLike(String nodeName) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("nodeName", nodeName + "%");
        return eao.findByNamedQuery("PreferencesNode.findAllLikeNodeName", params);
    }

}
