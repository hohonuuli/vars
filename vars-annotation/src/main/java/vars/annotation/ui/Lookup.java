/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.prefs.Preferences;
import org.bushe.swing.event.EventBus;
import org.mbari.util.Dispatcher;
import org.mbari.vars.annotation.ui.AnnotationApp;
import org.mbari.vars.annotation.ui.AnnotationFrame;
import org.mbari.vars.annotation.ui.MiscTabsPanel;
import vars.UserAccount;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.jpa.VarsUserPreferencesFactory;
import vars.shared.InjectorModule;
import vars.shared.ui.GlobalLookup;

/**
 *
 * @author brian
 */
public class Lookup extends GlobalLookup {

    protected static final Object KEY_DISPATCHER_OBSERVATION_TABLE = "vars.annotation.ui.Lookup-ObservationTable";
    protected static final Object KEY_DISPATCHER_VIDEO_ARCHIVE = VideoArchive.class;
    protected static final Object KEY_DISPATCHER_VIDEOARCHIVE = VideoArchive.class;
    protected static final Object KEY_DISPATCHER_APPLICATION_FRAME = AnnotationFrame.class;
    protected static final Object KEY_DISPATCHER_APPLICATION = AnnotationApp.class;
    protected static final Object KEY_DISPATCHER_SELECTED_OBSERVATIONS = Observation.class;
    protected static final Object KEY_DISPATCHER_PREFERENCES = "vars.annotation.ui.Lookup-Preferences";
    public static final Object KEY_DISPATCHER_GUICE_INJECTOR = "vars.annotation.ui.Lookup-Injector";

    /** Subscribers to this topic will recieve Boolean objects */
    public static final String TOPIC_DATABASE_STATUS = "vars.annotation.ui.Lookup-DatabaseStatus";

    /**
     * Refresh the persisted objects (akak clear 2nd level cache).
     */
    public static final String TOPIC_REFRESH = "vars.annotation.ui.Lookup-Refresh";

    /**
     * Change the videoarchive being annotated. The data object will be an
     * instance of {@link VideoArchive}
     */
    public static final String TOPIC_SELECTED_VIDEOARCHIVE = "vars.annotation.ui.Lookup-VideoArchive";

    /**
     * Specifies the Observations that are selected in the Observation table. the
     * data object will be a Collection&lt;Observation&gt;
     */
    public static final String TOPIC_SELECTED_OBSERVATIONS = "vars-annotation.ui.Lookup-SelectedObservations";

    /**
     * Message is sent when a concept should be selected in the concept tree. The
     * data object is a {@link String} representing the concept name to select. The
     * subscriber for the tree is in {@link MiscTabsPanel}
     */
    public static final String TOPIC_SELECT_CONCEPT = "vars-annotation.ui.Lookup-SelectedConcept";
    public static final String RESOURCE_BUNDLE = "annotation-app";


    static {
        getApplicationDispatcher().addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getNewValue() != null && !(evt.getNewValue() instanceof AnnotationApp)) {
                    throw new IllegalArgumentException("SUPPLIED: " + evt.getNewValue().getClass().getName() +
                            ", EXPECTED: " + AnnotationApp.class.getName());
                }
            }
        });

        getApplicationFrameDispatcher().addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getNewValue() != null && !(evt.getNewValue() instanceof AnnotationFrame)) {
                    throw new IllegalArgumentException("SUPPLIED: " + evt.getNewValue().getClass().getName() +
                            ", EXPECTED: " + AnnotationFrame.class.getName());
                }
            }
        });

        getGuiceInjectorDispatcher().addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getNewValue() != null && !(evt.getNewValue() instanceof Injector)) {
                    throw new IllegalArgumentException("SUPPLIED: " + evt.getNewValue().getClass().getName() +
                            ", EXPECTED: " + Injector.class.getName());
                }
            }
        });

        getPreferencesDispatcher().addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getNewValue() != null && !(evt.getNewValue() instanceof Preferences)) {
                    throw new IllegalArgumentException("SUPPLIED: " + evt.getNewValue().getClass().getName() +
                            ", EXPECTED: " + Preferences.class.getName());
                }
            }
        });


        getVideoArchiveDispatcher().addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getNewValue() != null && !(evt.getNewValue() instanceof VideoArchive)) {
                    throw new IllegalArgumentException("SUPPLIED: " + evt.getNewValue().getClass().getName() +
                            ", EXPECTED: " + VideoArchive.class.getName());
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

        EventBus.subscribe(TOPIC_SELECTED_VIDEOARCHIVE, LOGGING_SUBSCRIBER);
        EventBus.subscribe(TOPIC_REFRESH, LOGGING_SUBSCRIBER);
        EventBus.subscribe(TOPIC_DATABASE_STATUS, LOGGING_SUBSCRIBER);

    }

    public static Dispatcher getVideoArchiveDispatcher() {
        return Dispatcher.getDispatcher(KEY_DISPATCHER_VIDEO_ARCHIVE);
    }

    /**
     * Stores a reference to the {@link ConceptTree} so that other componenets
     * can reference it as needed.
     */
    public static Dispatcher getObservationTableDispatcher() {
        return Dispatcher.getDispatcher(KEY_DISPATCHER_OBSERVATION_TABLE);
    }

    public static Dispatcher getGuiceInjectorDispatcher() {
        final Dispatcher dispatcher = Dispatcher.getDispatcher(KEY_DISPATCHER_GUICE_INJECTOR);
        Injector injector = (Injector) dispatcher.getValueObject();
        if (injector == null) {
            injector = Guice.createInjector(new InjectorModule(RESOURCE_BUNDLE));
            dispatcher.setValueObject(injector);
        }

        return Dispatcher.getDispatcher(KEY_DISPATCHER_GUICE_INJECTOR);
    }

    public static Dispatcher getApplicationDispatcher() {
        return Dispatcher.getDispatcher(KEY_DISPATCHER_APPLICATION);
    }

    public static Dispatcher getApplicationFrameDispatcher() {
        return Dispatcher.getDispatcher(KEY_DISPATCHER_APPLICATION_FRAME);
    }

    /**
     *
     * @return A {@link Dispatcher} that contains a {@link Preferences} object
     * for the current UserAccount. This may be null
     */
    public static Dispatcher getPreferencesDispatcher() {
        return  Dispatcher.getDispatcher(KEY_DISPATCHER_PREFERENCES);
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

}
