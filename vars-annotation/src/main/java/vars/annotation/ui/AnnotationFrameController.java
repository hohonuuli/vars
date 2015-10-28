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
import java.util.List;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;
import javax.swing.SwingUtilities;

import com.google.inject.Injector;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
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
import vars.annotation.VideoFrame;
import vars.annotation.ui.commandqueue.ClearCommandQueueEvent;
import vars.annotation.ui.commandqueue.CommandQueue;
import vars.annotation.ui.eventbus.ObservationsAddedEvent;
import vars.annotation.ui.eventbus.ObservationsChangedEvent;
import vars.annotation.ui.eventbus.ObservationsRemovedEvent;
import vars.annotation.ui.eventbus.ObservationsSelectedEvent;
import vars.annotation.ui.eventbus.UIEventSubscriber;
import vars.annotation.ui.eventbus.VideoArchiveChangedEvent;
import vars.annotation.ui.eventbus.VideoArchiveSelectedEvent;
import vars.annotation.ui.eventbus.VideoFramesChangedEvent;
import vars.annotation.ui.video.DoNothingVideoControlService;
import vars.avplayer.VideoControlService;
import vars.annotation.ui.video.VideoControlServiceFactory;
import vars.shared.preferences.PreferenceUpdater;
import vars.shared.preferences.PreferencesService;

/**
 *
 * @author brian
 */
public class AnnotationFrameController implements PreferenceUpdater, UIEventSubscriber {

    private final String PREF_WIDTH = "width";
    private final String PREF_HEIGHT = "height";
    private final String PREF_OUTER_DIVIDER_LOCATION = "outerSplitPaneDividerLocation";
    private final String PREF_INNER_DIVIDER_LOCATION = "innerSplitPaneDividerLocation";
    private final String PREF_ALLCONTROLS_DIVIDER_LOCATION = "allControlsSplitPaneDividerLocation";
    private final String PREF_CONTROLS_DIVIDER_LOCATION = "controlsSplitPaneDividerLocation";
    private final String PREF_TABLE_WIDTH = "tableScrollPaneWidth";
    private final String PREF_TABLE_HEIGHT = "tableScrollPaneHeight";
    private final AnnotationFrame annotationFrame;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ToolBelt toolBelt;
    /** VERY IMPORTANT - the CommandQueue process the EventBus commands that modify model classes */
    private final CommandQueue commandQueue;

    public AnnotationFrameController(final AnnotationFrame annotationFrame, final ToolBelt toolBelt) {
        this.annotationFrame = annotationFrame;
        this.toolBelt = toolBelt;
        this.commandQueue = new CommandQueue(toolBelt);
        AnnotationProcessor.process(this); // Make EventBus Aware
        
        // Make sure we save the last observations we annotated to the database
        Thread cleanupThread = new Thread(new Runnable() {
            public void run() {

                // Persist prefs BEFORE shutting off services. Otherwise video connection
                // information is lost.
                log.info("Persisting preferences");
                persistPreferences();

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

            // -- Save AnnotationFrame size
            Dimension size = annotationFrame.getSize();
            preferences.putInt(PREF_WIDTH, size.width);
            preferences.putInt(PREF_HEIGHT, size.height);

            // -- Save TableScrollPane size();
            Dimension tablePaneSize = annotationFrame.getTableScrollPane().getSize();
            preferences.putInt(PREF_TABLE_WIDTH, tablePaneSize.width);
            preferences.putInt(PREF_TABLE_HEIGHT, tablePaneSize.height);

            // -- Save OuterSplitPane split location
            int outerDividerLocation = annotationFrame.getOuterSplitPane().getDividerLocation();
            preferences.putInt(PREF_OUTER_DIVIDER_LOCATION, outerDividerLocation);

            // -- Save InnerSplitPane split location
            int innerDividerLocation = annotationFrame.getInnerSplitPane().getDividerLocation();
            preferences.putInt(PREF_INNER_DIVIDER_LOCATION, innerDividerLocation);

            // -- Save AllControlsSplitPane location
            int allControlsDividerLocation = annotationFrame.getAllControlsSplitPane().getDividerLocation();
            preferences.putInt(PREF_ALLCONTROLS_DIVIDER_LOCATION, allControlsDividerLocation);

            // -- Save ControlSplitPane location
            int controlsDividerLocation = annotationFrame.getControlsPanelSplitPane().getDividerLocation();
            preferences.putInt(PREF_CONTROLS_DIVIDER_LOCATION, controlsDividerLocation);

            // -- Save video control info
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
                log.warn("Unable to get hostname, defaulting to 'localhost'.", ex);
                hostName = "localhost";
            }

            String className = getClass().getCanonicalName();

            Preferences hostPreferences = userPreferences.node(hostName);
            Preferences preferences = hostPreferences.node(className);

            // -- Set AnnotationFrame size
            Dimension currentSize = annotationFrame.getSize();
            int width = preferences.getInt(PREF_WIDTH, currentSize.width);
            int height = preferences.getInt(PREF_HEIGHT, currentSize.height);

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            width = width <= screenSize.width ? width : screenSize.width;
            height = height <= screenSize.height ? height : screenSize.height;
            annotationFrame.setSize(width, height);
            annotationFrame.validate();

            // -- Set TableScrollPane size
            int tablePaneWidth = preferences.getInt(PREF_TABLE_WIDTH, -1);
            int tablePaneHeight = preferences.getInt(PREF_TABLE_HEIGHT, -1);
            if (tablePaneWidth > 0 && tablePaneHeight > 0) {
                annotationFrame.getTableScrollPane().setPreferredSize(new Dimension(tablePaneWidth, tablePaneHeight));
            }

            // -- Set OuterSplitPane divider location
            int outerDividerLocation = preferences.getInt(PREF_OUTER_DIVIDER_LOCATION, -1);
            if (outerDividerLocation > 0) {
                annotationFrame.getOuterSplitPane().setDividerLocation(outerDividerLocation);
            }

            // -- Set innerSplitPane divider location
            int innerDividerLocation = preferences.getInt(PREF_INNER_DIVIDER_LOCATION, -1);
            if (innerDividerLocation > 0) {
                annotationFrame.getInnerSplitPane().setDividerLocation(innerDividerLocation);
            }

            // -- Set AllControlsSplitPane divider location
            int allControlsDividerLocation = preferences.getInt(PREF_ALLCONTROLS_DIVIDER_LOCATION, -1);
            if (allControlsDividerLocation > 0) {
                annotationFrame.getAllControlsSplitPane().setDividerLocation(allControlsDividerLocation);
            }

            // -- Set ControlsSplitPane divider location
            int controlsDividerLocation = preferences.getInt(PREF_CONTROLS_DIVIDER_LOCATION, -1);
            if (controlsDividerLocation > 0) {
                annotationFrame.getControlsPanelSplitPane().setDividerLocation(controlsDividerLocation);
            }

            // -- Load video control info
            Injector injector = (Injector) Lookup.getGuiceInjectorDispatcher().getValueObject();
            PreferencesFactory preferencesFactory = injector.getInstance(PreferencesFactory.class);
            PreferencesService preferencesService = new PreferencesService(preferencesFactory);
            if (preferencesService.findAutoconnectVcr(preferencesService.getHostname())) {
                String videoID = preferencesService.findLastVideoConnectionId(preferencesService.getHostname());
                VideoControlService videoControlService;
                try {
                    videoControlService = VideoControlServiceFactory.newVideoControlService(videoID);
                }
                catch (Exception e) {
                    log.warn("Failed to create a VideoControlService for " + videoID);
                    videoControlService = new DoNothingVideoControlService();
                }
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



    @Override
    public void respondTo(ObservationsAddedEvent event) {
        // Do Nothing
    }

    /**
     * When observations are changed we need to sync any that are in the dispatcher.
     * @param event
     */
    @EventSubscriber(eventClass = ObservationsChangedEvent.class)
    @Override
    public void respondTo(ObservationsChangedEvent event) {
        Collection<Observation> observations = (Collection<Observation>) Lookup.getSelectedObservationsDispatcher().getValueObject();
        List<Observation> selectedObservations = new ArrayList<Observation>(observations);
        List<Observation> changedObservations = new ArrayList<Observation>(event.get());
        Collection<Observation> updatedObservations = new ArrayList<Observation>(selectedObservations.size());
        for (Observation observation : selectedObservations) {
            int idx = changedObservations.indexOf(observation);
            if (idx >= 0) {
                updatedObservations.add(changedObservations.get(idx));
            }
            else {
                updatedObservations.add(observation);
            }
        }
        Lookup.getSelectedObservationsDispatcher().setValueObject(updatedObservations);
    }

    /**
     * When an observation is deleted we need to sync that with the dispatcher selected Observations
     * @param event
     */
    @EventSubscriber(eventClass = ObservationsRemovedEvent.class)
    @Override
    public void respondTo(ObservationsRemovedEvent event) {
        Collection<Observation> observations = (Collection<Observation>) Lookup.getSelectedObservationsDispatcher().getValueObject();
        List<Observation> selectedObservations = new ArrayList<Observation>(observations);
        List<Observation> removedObservations = new ArrayList<Observation>(event.get());
        Collection<Observation> updatedObservations = new ArrayList<Observation>(selectedObservations.size());
        for (Observation observation : selectedObservations) {
            int idx = removedObservations.indexOf(observation);
            if (idx >= 0) {
                // DO nothing. Observation has been removed
            }
            else {
                updatedObservations.add(observation);
            }
        }
        Lookup.getSelectedObservationsDispatcher().setValueObject(updatedObservations);
    }

    @EventSubscriber(eventClass = ObservationsSelectedEvent.class)
    @Override
    public void respondTo(ObservationsSelectedEvent event) {
        Collection<Observation> observations = event.get() == null ?
                new ArrayList<Observation>() : event.get();
        Lookup.getSelectedObservationsDispatcher().setValueObject(observations);
    }

    @EventSubscriber(eventClass = VideoArchiveChangedEvent.class)
    @Override
    public void respondTo(VideoArchiveChangedEvent event) {
        Lookup.getVideoArchiveDispatcher().setValueObject(event.get());
    }

    @EventSubscriber(eventClass = VideoArchiveSelectedEvent.class)
    @Override
    public void respondTo(VideoArchiveSelectedEvent event) {
        Lookup.getVideoArchiveDispatcher().setValueObject(event.get());
        EventBus.publish(new ClearCommandQueueEvent());
    }

    @Override
    public void respondTo(VideoFramesChangedEvent event) {
        VideoArchive videoArchive = (VideoArchive) Lookup.getVideoArchiveDispatcher().getValueObject();
        if (videoArchive != null) {
            Collection<VideoFrame> videoFrames = event.get();
            for (VideoFrame videoFrame : videoFrames) {
                if (videoFrame.getVideoArchive().equals(videoArchive)) {
                    Lookup.getVideoArchiveDispatcher().setValueObject(null);
                    Lookup.getVideoArchiveDispatcher().setValueObject(videoArchive);
                    break;
                }
            }
        }
    }
}
