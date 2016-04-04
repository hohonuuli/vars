/*
 * @(#)Lookup.java   2009.09.30 at 04:56:10 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.knowledgebase.ui;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JTree;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;
import org.mbari.swing.ProgressDialog;
import org.mbari.util.Dispatcher;
import vars.knowledgebase.Concept;
import vars.shared.InjectorModule;
import vars.shared.ui.GlobalLookup;

/**
 * Lookup contains globally available resources for the Knowledgbebase application.
 * These include:
 * <ul>
 *  <li>TOPIC - Topic objects used to publish or subscribe to eventbus messages</li>
 *  <li>KEY - Keys used to lookup a Dispatcher object</li>
 *  <li>static 'get' methods - Used for retrieving shared ui resources such as
 *      {@link Dispatcher} objects</li>
 * </ul>
 *
 * No buisness logic exists in the lookup other than enforcement of object types
 * that can be stored in a dispatcher.
 *
 * The usage pattern between EventBus messages (used for notification) and {@link Dispatcher}
 * objects (which can store values as well as register {@link PropertyChangeListener}s is:
 * Objects published to {@link EventBus} will be set in the appropriate {@link Dispatcher}.
 * However, objects set in a Dispatcher will not send a message via EventBus.
 * @deprecated
 */
public class Lookup extends GlobalLookup {

    protected static final Object KEY_DISPATCHER_APPLICATION_FRAME = KnowledgebaseFrame.class;
    protected static final Object KEY_DISPATCHER_APPLICATION = App.class;
    protected static final Object KEY_DISPATCHER_CONCEPT_TREE = JTree.class;
    protected static final Object KEY_DISPATCHER_SELECTED_CONCEPT = Concept.class;
    public static final String RESOURCE_BUNDLE = "knowledgebase-app";
    public static final Object KEY_DISPATCHER_GUICE_INJECTOR = Injector.class;
    
    
    /** The data object should be a {@link Concept} */
    public static final String TOPIC_SELECTED_CONCEPT = "vars.knowledgebase.ui.Lookup-SelectedConcept";


    /** The data object should be a  History  */
    public static final String TOPIC_APPROVE_HISTORY = "vars.knowledgebase.ui.Lookup-ApproveHistory";

    /** The data object should be a Collection<History> */
    public static final String TOPIC_APPROVE_HISTORIES = "vars.knowledgebase.ui.Lookup-ApproveHistories";

    /** The data object should be a Concept */
    public static final String TOPIC_UPDATE_OBSERVATIONS = "vars.knowledgebase.ui.Lookup-UpateObservations";

    /**
     * Refresh the knowledgebase (purge caceh) and open node to the conceptname provided.
     * Calls this as EventBus.publish(TOPIC_REFRESH_KNOWLEGEBASE, String conceptName)
     */
    public static final String TOPIC_REFRESH_KNOWLEGEBASE = "vars.knowledgebase.ui.Lookup-RefreshKnowledgebase";

    static {
        getApplicationDispatcher().addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getNewValue() != null && !(evt.getNewValue() instanceof App)) {
                    throw new IllegalArgumentException("SUPPLIED: " + evt.getNewValue().getClass().getName() +
                            ", EXPECTED: " + App.class.getName());
                }
            }
        });

        getApplicationFrameDispatcher().addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getNewValue() != null && !(evt.getNewValue() instanceof KnowledgebaseFrame)) {
                    throw new IllegalArgumentException("SUPPLIED: " + evt.getNewValue().getClass().getName() +
                            ", EXPECTED: " + KnowledgebaseFrame.class.getName());
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

        getSelectedConceptDispatcher().addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getNewValue() != null && !(evt.getNewValue() instanceof Concept)) {
                    throw new IllegalArgumentException("SUPPLIED: " + evt.getNewValue().getClass().getName() +
                            ", EXPECTED: " + Concept.class.getName());
                }
            }
        });

        getConceptTreeDispatcher().addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if (!(evt.getNewValue() instanceof JTree)) {
                    throw new IllegalArgumentException("SUPPLIED: " + evt.getNewValue().getClass().getName() +
                            ", EXPECTED: " + JTree.class.getName());
                }
            }
        });


        /*
         * When a Concept is sent to this topic on event bus make sure it gets
         * relayed to the correct Dispatcher
         */
        EventBus.subscribe(TOPIC_SELECTED_CONCEPT, new EventTopicSubscriber<Concept>() {
            public void onEvent(String topic, Concept data) {
                getSelectedConceptDispatcher().setValueObject(data);
            }
        });


        EventBus.subscribe(TOPIC_APPROVE_HISTORY, LOGGING_SUBSCRIBER);
        EventBus.subscribe(TOPIC_APPROVE_HISTORIES, LOGGING_SUBSCRIBER);
        EventBus.subscribe(TOPIC_SELECTED_CONCEPT, LOGGING_SUBSCRIBER);
        EventBus.subscribe(TOPIC_UPDATE_OBSERVATIONS, LOGGING_SUBSCRIBER);

    }

    private static ProgressDialog progressDialog;

    public static Dispatcher getApplicationDispatcher() {
        return Dispatcher.getDispatcher(KEY_DISPATCHER_APPLICATION);
    }

    public static Dispatcher getApplicationFrameDispatcher() {
        return Dispatcher.getDispatcher(KEY_DISPATCHER_APPLICATION_FRAME);
    }

    public static Dispatcher getGuiceInjectorDispatcher() {
        final Dispatcher dispatcher = Dispatcher.getDispatcher(KEY_DISPATCHER_GUICE_INJECTOR);
        Injector injector = (Injector) dispatcher.getValueObject();
        if (injector == null) {
            // HACK: Hard-wired JPA names. Change after switch to typesafe config
            injector = Guice.createInjector(new InjectorModule("vars-jpa-annotation", "vars-jpa-knowledgebase", "vars-jpa-misc"));
            dispatcher.setValueObject(injector);
        }

        return Dispatcher.getDispatcher(KEY_DISPATCHER_GUICE_INJECTOR);
    }

    public static ProgressDialog getProgressDialog() {
        if (progressDialog == null) {
            Frame frame = (Frame) getApplicationFrameDispatcher().getValueObject();
            progressDialog = new ProgressDialog(frame);
            progressDialog.setLocationRelativeTo(frame);
        }

        return progressDialog;
    }

    /**
     * You can set this directly but the recommended method for watching for
     * changes to the selected concept is to register an {@link EventTopicSubscriber}
     * to the TOPIC_SELECTED_CONCEPT topic. THe same for setting the selected
     * concept, publish to the {@link EventBus}
     * 
     * @return A {@link Dispatcher} that contains a reference to the currently
     *      selected concept.
     */
    public static Dispatcher getSelectedConceptDispatcher() {
        return Dispatcher.getDispatcher(KEY_DISPATCHER_SELECTED_CONCEPT);
    }

    /**
     * Stores a reference to the {@link ConceptTree} so that other componenets
     * can reference it as needed.
     */
    public static Dispatcher getConceptTreeDispatcher() {
        return Dispatcher.getDispatcher(KEY_DISPATCHER_CONCEPT_TREE);
    }

 

}
