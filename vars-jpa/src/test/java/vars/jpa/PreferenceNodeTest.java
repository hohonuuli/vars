/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.jpa;

import vars.VarsUserPreferencesFactory;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.logging.Level;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.MiscDAOFactory;
import vars.MiscFactory;
import vars.PersistenceCache;

/**
 *
 * @author brian
 */
public class PreferenceNodeTest {
    
    MiscFactory miscFactory;
    MiscDAOFactory daoFactory;
    VarsUserPreferencesFactory prefsFactory;
    PersistenceCache cache;
    
    public final Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setup() {
        Injector injector = Guice.createInjector(new VarsJpaTestModule());

        miscFactory = injector.getInstance(MiscFactory.class);
        daoFactory = injector.getInstance(MiscDAOFactory.class);
        prefsFactory = injector.getInstance(VarsUserPreferencesFactory.class);
        cache = injector.getInstance(PersistenceCache.class);
    }

    @Test
    public void test01() {

        int testOrder = 0;
        String testName = "test-button";


        // Create nodes
        Preferences root = prefsFactory.userRoot("test");
        log.info("Absolutepath is " + root.absolutePath());
        Preferences test01 = root.node("test01");
        Preferences buttonNode = test01.node("abutton");
        buttonNode.putInt("buttonOrder", testOrder);
        buttonNode.put("buttonName", testName);
        Preferences buttonNode2 = test01.node("bbutton");
        buttonNode2.putInt("buttonOrder", testOrder + 1);
        buttonNode2.put("buttonName", testName + "-not");
        
        // Clear cache
        cache.clear();

        // Read nodes
        root = prefsFactory.userRoot("test");
        test01 = root.node("test01");
        buttonNode = test01.node("abutton");
        int buttonOrder = buttonNode.getInt("buttonOrder", 1000);
        String buttonName = buttonNode.get("buttonName", "boom");
        try {
            // Clean up
            root.removeNode();
        } catch (BackingStoreException ex) {
            Assert.fail(ex.getMessage());
        }

        Assert.assertEquals(testName, buttonName);
        Assert.assertTrue(testOrder == buttonOrder);


    }

}
