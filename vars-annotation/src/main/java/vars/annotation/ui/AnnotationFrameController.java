/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;
import javax.swing.SwingUtilities;

import com.google.inject.Injector;
import org.bushe.swing.event.EventBus;
import org.mbari.swing.LabeledSpinningDialWaitIndicator;
import org.mbari.swing.WaitIndicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.CacheClearedEvent;
import vars.CacheClearedListener;
import vars.UserAccount;
import vars.VARSException;
import vars.VarsUserPreferencesFactory;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.annotation.ui.video.VideoControlService;
import vars.annotation.ui.video.VideoControlServiceFactory;
import vars.shared.preferences.PreferenceUpdater;
import vars.shared.preferences.PreferencesService;
import vars.shared.ui.video.ImageCaptureService;

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
        Thread cleanupThread = new Thread(new Runnable() {
            public void run() {

                // Persist prefs BEFORE shutting off services. Otherwise video connection
                // information is lost.
                log.info("Persisting preferences");
                persistPreferences();

                // Clean up NATIVE resources when we exit
                log.info("Closing ImageCaptureService");
                try {
                    ImageCaptureService imageCaptureService = (ImageCaptureService) Lookup.getImageCaptureServiceDispatcher().getValueObject();
                    imageCaptureService.dispose();
                }
                catch (Throwable e) {
                    log.warn("An error occurred while closing the image capture services", e);
                }

                log.info("Closing VideoControlService");
                try {
                    VideoControlService videoControlService = (VideoControlService) Lookup.getVideoControlServiceDispatcher().getValueObject();
                    videoControlService.kill();
                }
                catch (Exception e) {
                     log.warn("An error occurred while closing the video control services", e);
                }

                log.info("Saving last Observations to persistent storage during JVM shutdown");
                Collection<Observation> observations = (Collection<Observation>) Lookup.getSelectedObservationsDispatcher().getValueObject();
                toolBelt.getPersistenceController().updateAndValidate(new ArrayList<Observation>(observations));

                // Update current videoarchive's image URLs on shutdown
                VideoArchive videoArchive = (VideoArchive) Lookup.getVideoArchiveDispatcher().getValueObject();
                if (videoArchive != null) {
                    updateCameraData(videoArchive);
                }


                log.info("Shutdown thread is finished. Bye Bye");
            }
        }, "VARS-cleanupBeforeShutdownThread");
        //cleanupThread.setDaemon(false);
        Runtime.getRuntime().addShutdownHook(cleanupThread);

                
        // This listener displays a wait indicator while the cache is being cleared
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

        // When new observations are selected we need to persist changes to the old
        // observations to the database, then redraw them in the UI
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

        // When preferences change (i.e. when a new user logs in) we need save
        // the old preferences into the database and load the new ones
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


        /*
         * This listener updates the URL's of the image you've captured in
         * a background thread.
         */
        Lookup.getVideoArchiveDispatcher().addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(final PropertyChangeEvent evt) {
                Runnable runnable = new Runnable() {
                    public void run() {
                        VideoArchive videoArchive = (VideoArchive) evt.getOldValue();
                        if (videoArchive != null) {
                            updateCameraData(videoArchive);

                            // Evict the videoArchive from the JPA cache. or we
                            // won't get a fresh copy when we reopen it.
                            toolBelt.getPersistenceCache().evict(videoArchive);
                        }
                    }
                };

                new Thread(runnable, "UpdateCameraDataThread-" + System.currentTimeMillis()).start();

            }
        });

    }

    public void persistPreferences() {
        Preferences userPreferences = (Preferences) Lookup.getPreferencesDispatcher().getValueObject();
        persistPreferences(userPreferences);
    }

    private void persistPreferences(Preferences userPreferences) {
        if (userPreferences != null) {

            // Persist UI preferences
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

            // Persist video control info
            Injector injector = (Injector) Lookup.getGuiceInjectorDispatcher().getValueObject();
            PreferencesFactory preferencesFactory = injector.getInstance(PreferencesFactory.class);
            PreferencesService preferencesService = new PreferencesService(preferencesFactory);
            VideoControlService videoControlService = (VideoControlService) Lookup.getVideoControlServiceDispatcher().getValueObject();
            try {
                preferencesService.persistLastVideoConnectionId(preferencesService.getHostname(),
                        videoControlService.getVideoControlInformation().getVideoConnectionID());
            }
            catch (NullPointerException e) {
                log.info("Did not save Last VideoConnection ID preference. Most likely this " +
                        "was attempted after the video connection was closed");
            }

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

            // Load video control info
            Injector injector = (Injector) Lookup.getGuiceInjectorDispatcher().getValueObject();
            PreferencesFactory preferencesFactory = injector.getInstance(PreferencesFactory.class);
            PreferencesService preferencesService = new PreferencesService(preferencesFactory);
            if (preferencesService.findAutoconnectVcr(preferencesService.getHostname())) {
                String videoID = preferencesService.findLastVideoConnectionId(preferencesService.getHostname());
                VideoControlService videoControlService = VideoControlServiceFactory.newVideoControlService(videoID);
                Lookup.getVideoControlServiceDispatcher().setValueObject(videoControlService);
            }

        }
    }

    /**
     * Updates the image URL's for all the CameraData objects in a VideoArchive
     * so that local references are converted to http URL's
     * @param videoArchive
     */
    public void updateCameraData(VideoArchive videoArchive) {
        PreferencesFactory preferencesFactory = Lookup.getPreferencesFactory();
        UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();
        if (videoArchive != null &&
                preferencesFactory instanceof VarsUserPreferencesFactory &&
                userAccount != null) {
            VarsUserPreferencesFactory vpf = (VarsUserPreferencesFactory) preferencesFactory;
            PreferencesService preferencesService = new PreferencesService(vpf);
            File imageTarget = preferencesService.findImageTarget(userAccount.getUserName(), preferencesService.getHostname());
            URL imageTargetMapping = preferencesService.findImageTargetMapping(userAccount.getUserName(),
                    preferencesService.getHostname());
            try {
                toolBelt.getPersistenceController().updateCameraDataUrls(videoArchive, imageTarget, imageTargetMapping);
            } catch (MalformedURLException ex) {
                EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, ex);
            }
        }
    }


    
    

}
