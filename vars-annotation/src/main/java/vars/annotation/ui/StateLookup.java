package vars.annotation.ui;

import com.google.common.base.Preconditions;
import com.google.inject.Injector;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import vars.UserAccount;
import vars.VarsUserPreferencesFactory;
import vars.annotation.CameraDirections;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.annotation.ui.table.ObservationTable;
import vars.avplayer.VideoController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.prefs.Preferences;

/**
 * @author Brian Schlining
 * @since 2016-03-29T13:02:00
 */
public class StateLookup {

    public static final String RESOURCE_BUNDLE = "annotation-app";
    /**
     * Subscribers to this topic will receive Boolean objects.
     * True = status is OK,
     * false = database problems
     */
    public static final String TOPIC_DATABASE_STATUS = "vars.annotation.ui.Lookup-DatabaseStatus";

    /**
     * Refresh the persisted objects (aka clear 2nd level cache).
     */
    public static final String TOPIC_REFRESH = "vars.annotation.ui.Lookup-Refresh";

    /**
     * Message is sent when a concept should be selected in the concept tree. The
     * data object is a {@link String} representing the concept name to select. The
     * subscriber for the tree is in {@link MiscTabsPanel}
     */
    public static final String TOPIC_SELECT_CONCEPT = "vars-annotation.ui.Lookup-SelectedConcept";

    private static final ObjectProperty<UserAccount> userAccount = new SimpleObjectProperty<>();
    private static final ObjectProperty<CameraDirections> cameraDirection = new SimpleObjectProperty<>();
    private static Injector injector;
    private static final ObjectProperty<VideoController> videoController = new SimpleObjectProperty<>();
    private static final ObjectProperty<ObservationTable> observationTable = new SimpleObjectProperty<>();
    private static final ObjectProperty<Preferences> preferences = new SimpleObjectProperty<>();

    private static final ObjectProperty<VideoArchive> videoArchive = new SimpleObjectProperty<>();
    private static final ObjectProperty<Collection<Observation>> selectedObservations = new SimpleObjectProperty<>(new ArrayList<>());
    private static AnnotationFrame annotationFrame;
    private static App app;


    static {
        userAccount.addListener((obs, oldVal, newVal) -> {
            Preferences prefs = null;
            if (newVal != null) {
                VarsUserPreferencesFactory factory = injector.getInstance(VarsUserPreferencesFactory.class);
                prefs = factory.userRoot(newVal.getUserName());
            }
            preferences.set(prefs);
        });
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
        return injector;
    }

    public static VideoController getVideoController() {
        return videoController.get();
    }

    public static ObjectProperty<VideoController> videoControllerProperty() {
        return videoController;
    }

    public static void setVideoController(VideoController videoController) {
        StateLookup.videoController.set(videoController);
    }

    public void setInjector(Injector injector) {
        this.injector = injector;
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
        return selectedObservations.get();
    }

    public static ObjectProperty<Collection<Observation>> selectedObservationsProperty() {
        return selectedObservations;
    }

    public static void setSelectedObservations(Collection<Observation> selectedObservations) {
        if (selectedObservations == null) {
            selectedObservations = new ArrayList<>();
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

    public static UserAccount getUserAccount() {
        return userAccount.get();
    }

    public static ObjectProperty<UserAccount> userAccountProperty() {
        return userAccount;
    }

    public static void setUserAccount(UserAccount userAccount) {
        StateLookup.userAccount.set(userAccount);
    }
}
