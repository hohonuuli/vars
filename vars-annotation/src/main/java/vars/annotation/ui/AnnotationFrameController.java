/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.prefs.Preferences;
import javax.swing.SwingUtilities;
import org.mbari.swing.LabeledSpinningDialWaitIndicator;
import org.mbari.swing.WaitIndicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.CacheClearedEvent;
import vars.CacheClearedListener;
import vars.VARSException;
import vars.annotation.Observation;
import vars.shared.preferences.PreferenceUpdater;

/**
 *
 * @author brian
 */
public class AnnotationFrameController implements PreferenceUpdater {

    private final String PREF_WIDTH = "width";
    private final String PREF_HEIGHT = "height";
    private final AnnotationFrame annotationFrame;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ToolBelt toolBelt;

    public AnnotationFrameController(final AnnotationFrame annotationFrame, final ToolBelt toolBelt) {
        this.annotationFrame = annotationFrame;
        this.toolBelt = toolBelt;
        
        // Make sure we save the last observations we annotated to the database
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                log.debug("Saving last Observations to persistent storage during JVM shutdown");
                Collection<Observation> observations = (Collection<Observation>) Lookup.getSelectedObservationsDispatcher().getValueObject();
                toolBelt.getPersistenceController().updateAndValidate(new ArrayList<Observation>(observations));
                persistPreferences();
            }
        }));

        toolBelt.getPersistenceCache().addCacheClearedListener(new CacheClearedListener() {

            private WaitIndicator waitIndicator;

            public void afterClear(CacheClearedEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        waitIndicator.dispose();
                        waitIndicator = null;
                    }
                });
                
            }

            public void beforeClear(CacheClearedEvent evt) {
                waitIndicator = new LabeledSpinningDialWaitIndicator(AnnotationFrameController.this.annotationFrame, "Refreshing");
            }
        });

        Lookup.getSelectedObservationsDispatcher().addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                PersistenceController persistenceController = toolBelt.getPersistenceController();
                Collection<Observation> oldObservations = (Collection<Observation>) evt.getOldValue();
                if (oldObservations != null) {
                    oldObservations = new ArrayList<Observation>(oldObservations);
                    oldObservations = persistenceController.updateAndValidate(oldObservations);
                    persistenceController.updateUI(oldObservations, false);
                }
            }
        });

        /*
         * Get/set size of frame from user preferences
         */
        Lookup.getPreferencesDispatcher().addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {


                /*
                 * Save old preferences
                 */
                persistPreferences((Preferences) evt.getOldValue());

                /*
                 * Load new preferences
                 */
                loadPreferences((Preferences) evt.getNewValue());
                
            }
        });

    }

    public void persistPreferences() {
        Preferences userPreferences = (Preferences) Lookup.getPreferencesDispatcher().getValueObject();
        persistPreferences(userPreferences);
    }

    private void persistPreferences(Preferences userPreferences) {
        if (userPreferences != null) {

            String hostName = null;
            try {
                hostName = InetAddress.getLocalHost().getHostName();
            }
            catch (UnknownHostException ex) {
                throw new VARSException("Unable to get hostname", ex);
            }

            String className = getClass().getCanonicalName();

            Preferences preferences = userPreferences.node(hostName).node(className);
            Dimension size = annotationFrame.getSize();
            preferences.putInt(PREF_WIDTH, size.width);
            preferences.putInt(PREF_HEIGHT, size.height);

        }
    }


    private void loadPreferences(Preferences userPreferences) {
        if (userPreferences != null) {

            String hostName = null;
            try {
                hostName = InetAddress.getLocalHost().getHostName();
            }
            catch (UnknownHostException ex) {
                throw new VARSException("Unable to get hostname", ex);
            }

            String className = getClass().getCanonicalName();

            Preferences hostPreferences = userPreferences.node(hostName);

            Preferences preferences = hostPreferences.node(className);
            Dimension currentSize = annotationFrame.getSize();
            int width = preferences.getInt(PREF_WIDTH, currentSize.width);
            int height = preferences.getInt(PREF_HEIGHT, currentSize.height);

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            width = width <= screenSize.width ? width : screenSize.width;
            height = height <= screenSize.height ? height : screenSize.height;
            annotationFrame.setSize(width, height);

        }
    }


    
    

}
