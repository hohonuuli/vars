package vars.annotation.ui;

import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.bushe.swing.event.EventBus;
import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoState;
import vars.VarsUserPreferencesFactory;
import vars.annotation.CameraDirections;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.annotation.ui.table.ObservationTable;
import vars.avplayer.VideoController;
import vars.shared.ui.GlobalStateLookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

/**
 * @author Brian Schlining
 * @since 2016-03-29T13:02:00
 */
public class StateLookup extends GlobalStateLookup {

    public static final String RESOURCE_BUNDLE = "annotation-app";
    /**
     * Subscribers to this topic will receive Boolean objects.
     * True = status is OK,
     * false = database problems
     */
    public static final String TOPIC_DATABASE_STATUS = "vars.annotation.ui.StateLookup-DatabaseStatus";

    /**
     * Refresh the persisted objects (aka clear 2nd level cache).
     */
    public static final String TOPIC_REFRESH = "vars.annotation.ui.StateLookup-Refresh";

    /**
     * Message is sent when a concept should be selected in the concept tree. The
     * data object is a {@link String} representing the concept name to select. The
     * subscriber for the tree is in {@link MiscTabsPanel}
     */
    public static final String TOPIC_SELECT_CONCEPT = "vars-annotation.ui.StateLookup-SelectedConcept";

    private static final ObjectProperty<CameraDirections> cameraDirection = new SimpleObjectProperty<>();
    public static final Injector GUICE_INJECTOR = Guice.createInjector(new InjectorModule("vars-jpa-annotation", "vars-jpa-knowledgebase", "vars-jpa-misc"));
    private static final ObjectProperty<VideoController<? extends VideoState, ? extends VideoError>> videoController = new SimpleObjectProperty<>();
    private static final ObjectProperty<ObservationTable> observationTable = new SimpleObjectProperty<>();
    private static final ObjectProperty<Preferences> preferences = new SimpleObjectProperty<>();

    private static final ObjectProperty<VideoArchive> videoArchive = new SimpleObjectProperty<>();
    private static final ObjectProperty<Collection<Observation>> selectedObservations = new SimpleObjectProperty<>(new ArrayList<>());
    private static AnnotationFrame annotationFrame;
    private static App app;
    public static PreferencesFactory PREFERENCES_FACTORY = GUICE_INJECTOR.getInstance(PreferencesFactory.class);


    static {
        userAccountProperty().addListener((obs, oldVal, newVal) -> {
            Preferences prefs = null;
            if (newVal != null) {
                VarsUserPreferencesFactory factory = GUICE_INJECTOR .getInstance(VarsUserPreferencesFactory.class);
                prefs = factory.userRoot(newVal.getUserName());
            }
            preferences.set(prefs);
        });

        videoController.addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                oldVal.close();
            }
        });

        userAccountProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                setPreferences(null);
            }
            else {
                VarsUserPreferencesFactory factory = GUICE_INJECTOR.getInstance(VarsUserPreferencesFactory.class);
                Preferences prefs = factory.userRoot(newVal.getUserName());
                setPreferences(prefs);
            }
        });

        EventBus.subscribe(TOPIC_REFRESH, LOGGING_SUBSCRIBER);
        EventBus.subscribe(TOPIC_DATABASE_STATUS, LOGGING_SUBSCRIBER);

    }


    public static VideoArchive getVideoArchive() {
        return videoArchive.get();
    }

    public static ObjectProperty<VideoArchive> videoArchiveProperty() {
        return videoArchive;
    }

    public static void setVideoArchive(VideoArchive videoArchive) {
        StateLookup.videoArchive.set(videoArchive);
    }

    public static void setCameraDirection(CameraDirections cd) {
        cameraDirection.set(cd);
    }

    public static CameraDirections getCameraDirection() {
        return cameraDirection.get();
    }

    public static ObjectProperty<CameraDirections> cameraDirectionProperty() {
        return cameraDirection;
    }

    public Injector getInjector() {
        return GUICE_INJECTOR ;
    }

    public static VideoController getVideoController() {
        return videoController.get();
    }

    public static ObjectProperty<VideoController<? extends VideoState, ? extends VideoError>> videoControllerProperty() {
        return videoController;
    }

    public static void setVideoController(VideoController<? extends VideoState, ? extends VideoError> videoController) {
        StateLookup.videoController.set(videoController);
    }

    public static ObservationTable getObservationTable() {
        return observationTable.get();
    }

    public static ObjectProperty<ObservationTable> observationTableProperty() {
        return observationTable;
    }

    public static void setObservationTable(ObservationTable observationTable) {
        StateLookup.observationTable.set(observationTable);
    }

    public static Preferences getPreferences() {
        return preferences.get();
    }

    public static ObjectProperty<Preferences> preferencesProperty() {
        return preferences;
    }

    public static void setPreferences(Preferences preferences) {
        StateLookup.preferences.set(preferences);
    }

    public static Collection<Observation> getSelectedObservations() {
        Collection<Observation> obs = selectedObservations.get();
        if (obs == null || obs.isEmpty()) {
            return new ArrayList<>();
        }
        else {
            return new ArrayList<>(obs);
        }
    }

    public static ObjectProperty<Collection<Observation>> selectedObservationsProperty() {
        return selectedObservations;
    }

    /**
     * Makes a copy
     * @param selectedObservations
     */
    public static void setSelectedObservations(Collection<Observation> selectedObservations) {
        if (selectedObservations == null) {
            selectedObservations = new ArrayList<>();
        }
        else {
            selectedObservations = new ArrayList<>(selectedObservations);
        }
        StateLookup.selectedObservations.set(selectedObservations);
    }

    public static AnnotationFrame getAnnotationFrame() {
        return annotationFrame;
    }

    public static void setAnnotationFrame(AnnotationFrame annotationFrame) {
        StateLookup.annotationFrame = annotationFrame;
    }

    public static App getApp() {
        return app;
    }

    public static void setApp(App app) {
        StateLookup.app = app;
    }

}
