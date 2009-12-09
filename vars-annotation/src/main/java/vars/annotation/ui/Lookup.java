/*
 * @(#)Lookup.java   2009.12.09 at 09:08:38 PST
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



package vars.annotation.ui;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.prefs.Preferences;
import org.bushe.swing.event.EventBus;
import org.mbari.util.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.UserAccount;
import vars.annotation.CameraDirections;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.annotation.ui.table.ObservationTable;
import vars.annotation.ui.video.ImageCaptureService;
import vars.annotation.ui.video.VideoControlService;
import vars.jpa.VarsUserPreferencesFactory;
import vars.old.annotation.ui.AnnotationApp;
import vars.old.annotation.ui.AnnotationFrame;
import vars.shared.InjectorModule;
import vars.shared.ui.GlobalLookup;

/**
 *
 * @author brian
 */
public class Lookup extends GlobalLookup {

    protected static final Object KEY_DISPATCHER_CAMERA_DIRECTION = "vars.annotation.ui.Lookup-CameraDirection";
    protected static final Object KEY_DISPATCHER_GUICE_INJECTOR = "vars.annotation.ui.Lookup-Injector";
    protected static final Object KEY_DISPATCHER_IMAGECAPTURESERVICE = "vars.annotation.ui.Lookup-ImageCaptureService";
    protected static final Object KEY_DISPATCHER_OBSERVATION_TABLE = "vars.annotation.ui.Lookup-ObservationTable";
    protected static final Object KEY_DISPATCHER_PREFERENCES = "vars.annotation.ui.Lookup-Preferences";
    protected static final Object KEY_DISPATCHER_VIDEOSERVICE = "vars.annotation.ui.Lookup-VideoService";

    /**  */
    public static final String RESOURCE_BUNDLE = "annotation-app";

    /**
     * Subscribers to this topic will receive Boolean objects.
     * True = status is OK,
     * false = database problems
     */
    public static final String TOPIC_DATABASE_STATUS = "vars.annotation.ui.Lookup-DatabaseStatus";

    /**
     * Specifies Observations that are being deleted
     */
    public static final String TOPIC_DELETE_OBSERVATIONS = "vars.annotation.ui.Lookup-DeleteObservations";

    /**
     * Specifies Observations that are being updated
     */
    public static final String TOPIC_MERGE_OBSERVATIONS = "vars.annotation.ui.Lookup-MergeObservations";

    /**
     * Specifies Observations that are being deleted
     */
    public static final String TOPIC_PERSIST_OBSERVATIONS = "vars.annotation.ui.Lookup-PersistObservations";

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

    /**  */
    public static Logger log = LoggerFactory.getLogger(Lookup.class);
    protected static final Object KEY_DISPATCHER_VIDEOARCHIVE = VideoArchive.class;
    protected static final Object KEY_DISPATCHER_SELECTED_OBSERVATIONS = Observation.class;
    protected static final Object KEY_DISPATCHER_APPLICATION_FRAME = AnnotationFrame.class;
    protected static final Object KEY_DISPATCHER_APPLICATION = AnnotationApp.class;

    static {
        getApplicationDispatcher().addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if ((evt.getNewValue() != null) && !(evt.getNewValue() instanceof AnnotationApp)) {
                    throw new IllegalArgumentException("SUPPLIED: " + evt.getNewValue().getClass().getName() +
                                                       ", EXPECTED: " + AnnotationApp.class.getName());
                }
            }

        });

        getApplicationFrameDispatcher().addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if ((evt.getNewValue() != null) && !(evt.getNewValue() instanceof AnnotationFrame)) {
                    throw new IllegalArgumentException("SUPPLIED: " + evt.getNewValue().getClass().getName() +
                                                       ", EXPECTED: " + AnnotationFrame.class.getName());
                }
            }

        });

        getCameraDirectionDispatcher().addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if ((evt.getNewValue() != null) && !(evt.getNewValue() instanceof CameraDirections)) {
                    throw new IllegalArgumentException("SUPPLIED: " + evt.getNewValue().getClass().getName() +
                                                       ", EXPECTED: " + CameraDirections.class.getName());
                }
            }
        });

        getGuiceInjectorDispatcher().addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if ((evt.getNewValue() != null) && !(evt.getNewValue() instanceof Injector)) {
                    throw new IllegalArgumentException("SUPPLIED: " + evt.getNewValue().getClass().getName() +
                                                       ", EXPECTED: " + Injector.class.getName());
                }
            }

        });

        getPreferencesDispatcher().addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if ((evt.getNewValue() != null) && !(evt.getNewValue() instanceof Preferences)) {
                    throw new IllegalArgumentException("SUPPLIED: " + evt.getNewValue().getClass().getName() +
                                                       ", EXPECTED: " + Preferences.class.getName());
                }
            }

        });


        getVideoArchiveDispatcher().addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if ((evt.getNewValue() != null) && !(evt.getNewValue() instanceof VideoArchive)) {
                    throw new IllegalArgumentException("SUPPLIED: " + evt.getNewValue().getClass().getName() +
                                                       ", EXPECTED: " + VideoArchive.class.getName());
                }
                else {
                    log.info("Using " + evt.getNewValue());
                }
            }

        });


        getVideoControlServiceDispatcher().addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if ((evt.getNewValue() != null) && !(evt.getNewValue() instanceof VideoControlService)) {
                    throw new IllegalArgumentException("SUPPLIED: " + evt.getNewValue().getClass().getName() +
                                                       ", EXPECTED: " + VideoControlService.class.getName());
                }
                else {
                    log.info("Using " + evt.getNewValue());
                    VideoControlService oldService = (VideoControlService) evt.getOldValue();
                    if (oldService != null) {
                        oldService.disconnect();
                    }
                }
            }
        });

        getImageCaptureServiceDispatcher().addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if ((evt.getNewValue() != null) && !(evt.getNewValue() instanceof ImageCaptureService)) {
                    throw new IllegalArgumentException("SUPPLIED: " + evt.getNewValue().getClass().getName() +
                                                       ", EXPECTED: " + ImageCaptureService.class.getName());
                }
                else {
                    log.info("Using " + evt.getNewValue());
                }
            }
        });

        getObservationTableDispatcher().addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if ((evt.getNewValue() != null) && !(evt.getNewValue() instanceof ObservationTable)) {
                    throw new IllegalArgumentException("SUPPLIED: " + evt.getNewValue().getClass().getName() +
                                                       ", EXPECTED: " + ObservationTable.class.getName());
                }
            }
        });

        /*
         * When a UserAccount is sent to this topic on event bus make sure the preferences
         * get updated in the preferences dispatcher
         */
        getUserAccountDispatcher().addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                UserAccount userAccount = (UserAccount) evt.getNewValue();
                if (userAccount == null) {
                    getPreferencesDispatcher().setValueObject(null);
                }
                else {
                    Injector injector = (Injector) getGuiceInjectorDispatcher().getValueObject();
                    VarsUserPreferencesFactory factory = injector.getInstance(VarsUserPreferencesFactory.class);
                    Preferences prefs = factory.userRoot(userAccount.getUserName());
                    getPreferencesDispatcher().setValueObject(prefs);
                }
            }
        });

        EventBus.subscribe(TOPIC_REFRESH, LOGGING_SUBSCRIBER);
        EventBus.subscribe(TOPIC_DATABASE_STATUS, LOGGING_SUBSCRIBER);

    }

    /**
     * @return
     */
    public static Dispatcher getApplicationDispatcher() {
        return Dispatcher.getDispatcher(KEY_DISPATCHER_APPLICATION);
    }

    /**
     * @return
     */
    public static Dispatcher getApplicationFrameDispatcher() {
        return Dispatcher.getDispatcher(KEY_DISPATCHER_APPLICATION_FRAME);
    }

    /**
     *
     * @return A Dispatcher referencing a {@link CameraDirections} enumeration
     */
    public static Dispatcher getCameraDirectionDispatcher() {
        return Dispatcher.getDispatcher(KEY_DISPATCHER_CAMERA_DIRECTION);
    }

    /**
     * @return
     */
    public static Dispatcher getGuiceInjectorDispatcher() {
        final Dispatcher dispatcher = Dispatcher.getDispatcher(KEY_DISPATCHER_GUICE_INJECTOR);
        Injector injector = (Injector) dispatcher.getValueObject();
        if (injector == null) {
            injector = Guice.createInjector(new InjectorModule(RESOURCE_BUNDLE));
            dispatcher.setValueObject(injector);
        }

        return Dispatcher.getDispatcher(KEY_DISPATCHER_GUICE_INJECTOR);
    }

    /**
     * @return
     */
    public static Dispatcher getImageCaptureServiceDispatcher() {
        return Dispatcher.getDispatcher(KEY_DISPATCHER_IMAGECAPTURESERVICE);
    }

    /**
     * Stores a reference to the {@link ConceptTree} so that other componenets
     * can reference it as needed.
     * @return
     */
    public static Dispatcher getObservationTableDispatcher() {
        return Dispatcher.getDispatcher(KEY_DISPATCHER_OBSERVATION_TABLE);
    }

    /**
     *
     * @return A {@link Dispatcher} that contains a {@link Preferences} object
     * for the current UserAccount. This may be null
     */
    public static Dispatcher getPreferencesDispatcher() {
        return Dispatcher.getDispatcher(KEY_DISPATCHER_PREFERENCES);
    }

    /**
     *
     * @return A Dispatcher that contains a Collection&lt;Observaton&gt; that
     *  have been selected in the Observation table. This should NEVER be null.
     *  if no rows are selected an empty list should be returned.
     */
    public static Dispatcher getSelectedObservationsDispatcher() {
        return Dispatcher.getDispatcher(KEY_DISPATCHER_SELECTED_OBSERVATIONS);
    }

    /**
     * @return
     */
    public static Dispatcher getVideoArchiveDispatcher() {
        return Dispatcher.getDispatcher(KEY_DISPATCHER_VIDEOARCHIVE);
    }

    /**
     * @return
     */
    public static Dispatcher getVideoControlServiceDispatcher() {
        return Dispatcher.getDispatcher(KEY_DISPATCHER_VIDEOSERVICE);
    }
}
