package vars.knowledgebase.ui;

import com.google.common.base.Preconditions;
import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;
import org.mbari.swing.ProgressDialog;
import vars.knowledgebase.Concept;
import vars.shared.InjectorModule;
import vars.shared.ui.GlobalStateLookup;

import javax.swing.JTree;
import java.awt.Frame;

/**
 * @author Brian Schlining
 * @since 2016-03-30T10:22:00
 */
public class StateLookup extends GlobalStateLookup {

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

    /** HACK: Hard-wired JPA names. Change after switch to typesafe config */
    public static final Injector GUICE_INJECTOR = Guice.createInjector(new InjectorModule("vars-jpa-annotation", "vars-jpa-knowledgebase", "vars-jpa-misc"));

    private static final ObjectProperty<KnowledgebaseFrame> applicationFrame = new SimpleObjectProperty<>();
    private static final ObjectProperty<Concept> selectedConcept = new SimpleObjectProperty<>();
    private static final ObjectProperty<App> application = new SimpleObjectProperty<>();
    private static final EventTopicSubscriber<Concept> conceptSubscriber = (s, c) -> selectedConcept.set(c);
    private static JTree conceptTree;

    private static ProgressDialog progressDialog;

    static {
        EventBus.subscribe(TOPIC_SELECTED_CONCEPT, conceptSubscriber);
        EventBus.subscribe(TOPIC_APPROVE_HISTORY, LOGGING_SUBSCRIBER);
        EventBus.subscribe(TOPIC_APPROVE_HISTORIES, LOGGING_SUBSCRIBER);
        EventBus.subscribe(TOPIC_SELECTED_CONCEPT, LOGGING_SUBSCRIBER);
        EventBus.subscribe(TOPIC_UPDATE_OBSERVATIONS, LOGGING_SUBSCRIBER);
    }


    public static ProgressDialog getProgressDialog() {
        if (progressDialog == null) {
            Frame frame = applicationFrame.get();
            progressDialog = new ProgressDialog(frame);
            progressDialog.setLocationRelativeTo(frame);
        }

        return progressDialog;
    }


    public static KnowledgebaseFrame getApplicationFrame() {
        return applicationFrame.get();
    }

    public static ObjectProperty<KnowledgebaseFrame> applicationFrameProperty() {
        return applicationFrame;
    }

    public static void setApplicationFrame(KnowledgebaseFrame applicationFrame) {
        StateLookup.applicationFrame.set(applicationFrame);
    }

    public static Concept getSelectedConcept() {
        return selectedConcept.get();
    }

    public static ObjectProperty<Concept> selectedConceptProperty() {
        return selectedConcept;
    }

    public static void setSelectedConcept(Concept selectedConcept) {
        StateLookup.selectedConcept.set(selectedConcept);
    }

    public static JTree getConceptTree() {


        return conceptTree;
    }

    public static void setConceptTree(JTree conceptTree) {
        Preconditions.checkArgument(conceptTree != null);
        StateLookup.conceptTree = conceptTree;
    }

    public static App getApplication() {
        return application.get();
    }

    public static ObjectProperty<App> applicationProperty() {
        return application;
    }

    public static void setApplication(App application) {
        StateLookup.application.set(application);
    }
}
