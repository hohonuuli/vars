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
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;
import org.mbari.swing.ProgressDialog;
import org.mbari.util.Dispatcher;
import vars.knowledgebase.Concept;
import vars.knowledgebase.KnowledgebaseModule;
import vars.shared.ui.GlobalLookup;
import vars.shared.ui.kbtree.ConceptTree;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Sep 29, 2009
 * Time: 11:39:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class Lookup extends GlobalLookup {

    protected static final Object KEY_DISPATCHER_APPLICATION_FRAME = KnowledgebaseFrame.class;
    protected static final Object KEY_DISPATCHER_APPLICATION = KnowledgebaseApp.class;
    protected static final Object KEY_DISPATCHER_CONCEPT_TREE = ConceptTree.class;
    protected static final Object KEY_DISPATCHER_SELECTED_CONCEPT = Concept.class;
    public static final String RESOURCE_BUNDLE = "knowlegebase-app";
    public static final Object KEY_DISPATCHER_GUICE_INJECTOR = Injector.class;
    public static final String TOPIC_DELETE_MEDIA = Lookup.class.getName() + "-DeleteMedia";
    public static final String TOPIC_DELETE_CONCEPT = Lookup.class.getName() + "-DeleteConcept";
    public static final String TOPIC_DELETE_CONCEPT_NAME = Lookup.class.getName() +"-DeleteConceptName";
    public static final String TOPIC_DELETE_HISTORY = Lookup.class.getName() + "-DeleteHistorie";
    public static final String TOPIC_DELETE_LINK_REALIZATION = Lookup.class.getName() + "-DeleteLinkRealization";
    public static final String TOPIC_DELETE_LINK_TEMPLATE = Lookup.class.getName() + "-DeleteLinkTemplate";
    public static final String TOPIC_SELECTED_CONCEPT = Lookup.class.getName() + "-SelectedConcept";

    /**
     * Refresh the knowledgebase (purge caceh) and open node to the conceptname provided.
     * Calls this as EventBus.publish(TOPIC_REFRESH_KNOWLEGEBASE, String conceptName)
     */
    public static final String TOPIC_REFRESH_KNOWLEGEBASE = Lookup.class.getName() + "-RefreshKnowledgebase";

    static {
        getApplicationDispatcher().addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (!(evt.getNewValue() instanceof KnowledgebaseApp)) {
                    throw new IllegalArgumentException("SUPPLIED: " + evt.getNewValue().getClass().getName() +
                            ", EXPECTED: " + KnowledgebaseApp.class.getName());
                }
            }
        });

        getApplicationFrameDispatcher().addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (!(evt.getNewValue() instanceof KnowledgebaseFrame)) {
                    throw new IllegalArgumentException("SUPPLIED: " + evt.getNewValue().getClass().getName() +
                            ", EXPECTED: " + KnowledgebaseFrame.class.getName());
                }
            }
        });

        getGuiceInjectorDispatcher().addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (!(evt.getNewValue() instanceof Injector)) {
                    throw new IllegalArgumentException("SUPPLIED: " + evt.getNewValue().getClass().getName() +
                            ", EXPECTED: " + Injector.class.getName());
                }
            }
        });

        getSelectedConceptDispatcher().addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if (!(evt.getNewValue() instanceof Concept)) {
                    throw new IllegalArgumentException("SUPPLIED: " + evt.getNewValue().getClass().getName() +
                            ", EXPECTED: " + Concept.class.getName());
                }
            }
        });

        getConceptTreeDispatcher().addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if (!(evt.getNewValue() instanceof ConceptTree)) {
                    throw new IllegalArgumentException("SUPPLIED: " + evt.getNewValue().getClass().getName() +
                            ", EXPECTED: " + ConceptTree.class.getName());
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
            injector = Guice.createInjector(new KnowledgebaseModule());
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
