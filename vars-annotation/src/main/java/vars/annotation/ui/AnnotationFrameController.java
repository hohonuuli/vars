/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
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
import mbarix4j.swing.LabeledSpinningDialWaitIndicator;
import mbarix4j.swing.WaitIndicator;
import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoState;
import org.mbari.vcr4j.adapter.noop.NoopVideoIO;
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
import vars.annotation.ui.video.VideoControlServiceFactory;
import vars.avplayer.VideoController;
import vars.avplayer.noop.NoopImageCaptureService;
import vars.avplayer.rx.SetVideoArchiveMsg;
import vars.avplayer.rx.SetVideoControllerMsg;
import vars.shared.preferences.PreferenceUpdater;
import vars.shared.preferences.PreferencesService;
import vars.shared.rx.RXEventBus;

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
    private final RXEventBus eventBus;
    /** VERY IMPORTANT - the CommandQueue process the EventBus commands that modify model classes */
    private final CommandQueue commandQueue;

    public AnnotationFrameController(final AnnotationFrame annotationFrame,
            final ToolBelt toolBelt,
            final RXEventBus eventBus) {
        this.annotationFrame = annotationFrame;
        this.toolBelt = toolBelt;
        this.eventBus = eventBus;
        this.commandQueue = new CommandQueue(toolBelt);
        initialize();
        AnnotationProcessor.process(this); // Make EventBus Aware
    }

    private void initialize() {
        // Make sure we save the last observations we annotated to the database
        Thread cleanupThread = new Thread(() -> {

            // Persist prefs BEFORE shutting off services. Otherwise video connection
            // information is lost.
            log.info("Persisting preferences");
            persistPreferences();

            log.info("Saving last Observations to persistent storage during JVM shutdown");
            Collection<Observation> observations = StateLookup.getSelectedObservations();
            toolBelt.getPersistenceController().updateAndValidate(new ArrayList<Observation>(observations));

            // Update current videoarchive's image URLs on shutdown
            VideoArchive videoArchive = StateLookup.getVideoArchive();
            if (videoArchive != null) {
                updateCameraData(videoArchive);
            }

            log.info("Shutdown thread is finished. Bye Bye");

        }, "VARS-cleanupBeforeShutdownThread");

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
        StateLookup.preferencesProperty().addListener((obs, oldVal, newVal) -> {
            persistPreferences(oldVal); // Save the old
            loadPreferences(newVal);    // load the new
        });


        /*
         * This listener updates the URL's of the image you've captured in
         * a background thread.
         */
        StateLookup.videoArchiveProperty().addListener((obs, oldVal, videoArchive) -> {
            Runnable runnable = () -> {
                if (videoArchive != null) {
                    updateCameraData(videoArchive);
                    // Evict the videoArchive from the JPA cache. or we
                    // won't get a fresh copy when we reopen it.
                    toolBelt.getPersistenceCache().evict(videoArchive);
                }
            };
            new Thread(runnable, "UpdateCameraDataThread-" + System.currentTimeMillis()).start();
        });

        /*
            Bridge between the RXEventBus and the EventBus/StateLookup's videoArchive property
         */
        eventBus.toObserverable()
                .ofType(SetVideoArchiveMsg.class)
                .subscribe(msg -> EventBus.publish(new VideoArchiveChangedEvent(null, msg.getVideoArchive())));


        /*
            Bridges between the RXEventBus and the StateLookup's videoController property
         */
        eventBus.toObserverable()
                .ofType(SetVideoControllerMsg.class)
                .subscribe(msg -> StateLookup.setVideoController(msg.getVideoController()));
    }

    public void persistPreferences() {
        Preferences userPreferences = StateLookup.getPreferences();
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
            Injector injector = StateLookup.GUICE_INJECTOR;
            PreferencesFactory preferencesFactory = injector.getInstance(PreferencesFactory.class);
            PreferencesService preferencesService = new PreferencesService(preferencesFactory);
            VideoController videoController = StateLookup.getVideoController();

            // TODO this was added to support ships connection. So user doen't have to manually connect
            // to UDP. We will need a workaround in the new VARS.
            //VideoControlService videoControlService = (VideoControlService) Lookup.getVideoControlServiceDispatcher().getValueObject();
            try {
                preferencesService.persistLastVideoConnectionId(preferencesService.getHostname(),
                        videoController.getConnectionID());
            }
            catch (NullPointerException e) {
                log.info("Did not save Last VideoController ID preference. Most likely this " +
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
            Injector injector = StateLookup.GUICE_INJECTOR;
            PreferencesFactory preferencesFactory = injector.getInstance(PreferencesFactory.class);
            PreferencesService preferencesService = new PreferencesService(preferencesFactory);
            /* TODO Fix this for ships
            if (preferencesService.findAutoconnectVcr(preferencesService.getHostname())) {

                // TODO this was added for ships. We will need a workaround for the new VARS
                String videoID = preferencesService.findLastVideoConnectionId(preferencesService.getHostname());
                VideoController<? extends VideoState, ? extends VideoError> videoController;
                try {
                    videoController = VideoControlServiceFactory.newVideoController(videoID);
                }
                catch (Exception e) {
                    log.warn("Failed to create a VideoControlService for " + videoID);
                    videoController = new VideoController<>(new NoopImageCaptureService(), new NoopVideoIO());
                }
                StateLookup.setVideoController(videoController);
            }
            */

        }
    }

    /**
     * Updates the image URL's for all the CameraData objects in a VideoArchive
     * so that local references are converted to http URL's
     * @param videoArchive
     */
    public void updateCameraData(VideoArchive videoArchive) {
        PreferencesFactory preferencesFactory = StateLookup.PREFERENCES_FACTORY;
        UserAccount userAccount = StateLookup.getUserAccount();
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
                EventBus.publish(StateLookup.TOPIC_NONFATAL_ERROR, ex);
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
        Collection<Observation> observations = StateLookup.getSelectedObservations();
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
        StateLookup.setSelectedObservations(updatedObservations);
    }

    /**
     * When an observation is deleted we need to sync that with the dispatcher selected Observations
     * @param event
     */
    @EventSubscriber(eventClass = ObservationsRemovedEvent.class)
    @Override
    public void respondTo(ObservationsRemovedEvent event) {
        Collection<Observation> observations = StateLookup.getSelectedObservations();
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
        StateLookup.setSelectedObservations(updatedObservations);
    }

    @EventSubscriber(eventClass = ObservationsSelectedEvent.class)
    @Override
    public void respondTo(ObservationsSelectedEvent event) {
        Collection<Observation> observations = event.get() == null ?
                new ArrayList<>() : event.get();
        StateLookup.setSelectedObservations(observations);
    }

    @EventSubscriber(eventClass = VideoArchiveChangedEvent.class)
    @Override
    public void respondTo(VideoArchiveChangedEvent event) {
        StateLookup.setVideoArchive(event.get());
    }

    @EventSubscriber(eventClass = VideoArchiveSelectedEvent.class)
    @Override
    public void respondTo(VideoArchiveSelectedEvent event) {
        StateLookup.setVideoArchive(event.get());
        EventBus.publish(new ClearCommandQueueEvent());
    }

    @Override
    public void respondTo(VideoFramesChangedEvent event) {
        VideoArchive videoArchive = StateLookup.getVideoArchive();
        if (videoArchive != null) {
            Collection<VideoFrame> videoFrames = event.get();
            for (VideoFrame videoFrame : videoFrames) {
                if (videoFrame.getVideoArchive().equals(videoArchive)) {
                    StateLookup.setVideoArchive(null);
                    StateLookup.setVideoArchive(videoArchive);
                    break;
                }
            }
        }
    }
}
