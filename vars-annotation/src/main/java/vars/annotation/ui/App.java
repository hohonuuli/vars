/*
 * @(#)App.java   2009.12.12 at 09:26:17 PST
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

import com.google.inject.Injector;
import foxtrot.Job;
import foxtrot.Worker;
import java.awt.Toolkit;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;
import org.bushe.swing.event.EventTopicSubscriber;
import org.mbari.awt.WaitCursorEventQueue;
import org.mbari.swing.SplashFrame;
import org.mbari.util.SystemUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.AnnotationPersistenceService;
import vars.annotation.Observation;
import vars.annotation.ui.buttons.simple.QuickConceptButton;
import vars.annotation.ui.eventbus.ExitTopicSubscriber;
import vars.annotation.ui.eventbus.VideoArchiveChangedEvent;
import vars.knowledgebase.Concept;
import vars.shared.ui.GlobalLookup;
import vars.shared.ui.event.FatalExceptionSubscriber;
import vars.shared.ui.event.LoggingEventSubscriber;
import vars.shared.ui.event.NonFatalErrorSubscriber;
import vars.shared.ui.event.WarningSubscriber;
import vars.shared.ui.video.FakeImageCaptureServiceImpl;

/**
 *
 * @author brian
 */
public class App {

    private AnnotationFrame annotationFrame;
    private final ToolBelt toolBelt;
    
    /**
     * The App gets garbage collected shortly after startup. To
     * hang on to the EventTopicSubscribers we store them in a static list. This
     * is very important, otherwise they get gc'd too.
     */
    private static final List<EventTopicSubscriber> GC_PREVENTION = new Vector<EventTopicSubscriber>();
    private static final List<EventSubscriber> GC_PREVENTION_EVENTS = new Vector<EventSubscriber>();

    /**
     * Constructs ...
     */
    public App() {

        final ImageIcon mbariLogo =
            new ImageIcon(getClass().getResource("/annotation-splash.png"));
        final SplashFrame splashFrame = new SplashFrame(mbariLogo);
        splashFrame.setMessage(" Initializing configuration ...");
        splashFrame.setVisible(true);

        final Injector injector = (Injector) Lookup.getGuiceInjectorDispatcher().getValueObject();
        toolBelt = injector.getInstance(ToolBelt.class);


        /*
         *  Verify that the database connection is working. If it's not, show
         *  a dialog. Without this check database errors fail silently and to
         *  the user it looks like the application just won't start at all.
         */
        splashFrame.setMessage(" Loading authentication policies ...");
        try {
            toolBelt.getMiscDAOFactory().newUserAccountDAO().findAll();
        }
        catch (Exception e) {
            (new FatalExceptionSubscriber(null)).onEvent(Lookup.TOPIC_FATAL_ERROR, e);
        }
        Lookup.getApplicationDispatcher().setValueObject(this);

        /*
         * Preload the knowledgebase in the Foxtrot worker thread!!
         */
        splashFrame.setMessage(" Preloading knowledgebase ... be patient");
        Worker.post(new Job() {

            @Override
            public Object run() {
                AnnotationPersistenceService service = toolBelt.getAnnotationPersistenceService();
                Concept root = service.findRootConcept();
                service.findDescendantNamesFor(root);
                return null;
            }
        });

        splashFrame.setMessage("Assembling the user interface ...");
        Lookup.getSelectedObservationsDispatcher().setValueObject(new Vector<Observation>());

        // Connect to the ImageCaptureService
        //Lookup.getImageCaptureServiceDispatcher().setValueObject(injector.getInstance(ImageCaptureService.class));
        Lookup.getImageCaptureServiceDispatcher().setValueObject(new FakeImageCaptureServiceImpl());
        
        // Configure EventBus
        EventTopicSubscriber fatalErrorSubscriber = new FatalExceptionSubscriber(getAnnotationFrame());
        EventTopicSubscriber nonFatalErrorSubscriber = new NonFatalErrorSubscriber(getAnnotationFrame());
        EventTopicSubscriber warningSubscriber = new WarningSubscriber(getAnnotationFrame());       
        EventTopicSubscriber exitSubscriber = new ExitTopicSubscriber();
        EventSubscriber loggingSubscriber = new LoggingEventSubscriber();
        
        GC_PREVENTION.add(fatalErrorSubscriber);
        GC_PREVENTION.add(nonFatalErrorSubscriber);
        GC_PREVENTION.add(warningSubscriber);
        GC_PREVENTION.add(exitSubscriber);
        GC_PREVENTION_EVENTS.add(loggingSubscriber);

        EventBus.subscribe(Lookup.TOPIC_FATAL_ERROR, fatalErrorSubscriber);
        EventBus.subscribe(Lookup.TOPIC_NONFATAL_ERROR, nonFatalErrorSubscriber);
        EventBus.subscribe(Lookup.TOPIC_WARNING, warningSubscriber);
        EventBus.subscribe(Lookup.TOPIC_EXIT, exitSubscriber);
        EventBus.subscribe(VideoArchiveChangedEvent.class, loggingSubscriber);

        /*
         * Add a special eventQueue that toggles the cursor if the application is busy
         */
        //Toolkit.getDefaultToolkit().getSystemEventQueue().push(new WaitCursorEventQueue(500));
        JFrame frame = getAnnotationFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Lookup.getApplicationFrameDispatcher().setValueObject(frame);
        frame.pack();
        splashFrame.dispose();
        frame.setVisible(true);
    }

    /**
     * @return
     */
    public AnnotationFrame getAnnotationFrame() {
        if (annotationFrame == null) {
            annotationFrame = new AnnotationFrame(toolBelt);
        }

        return annotationFrame;
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {



        /**
         * We like to do all database transaction in the UTC timezone
         */
        System.setProperty("user.timezone", "UTC");
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        /*
         * Make it pretty on Macs
         */
        if (SystemUtilities.isMacOS()) {
            SystemUtilities.configureMacOSApplication("VARS Annotation");
        }

        /**
         * Log uncaught Exceptions
         */
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread thread, Throwable e) {
                Logger log = LoggerFactory.getLogger(thread.getClass());
                log.error("Exception in thread [" + thread.getName() + "]", e);
            }
        });

        /*
         * Create an application settings directory if needed
         */
        GlobalLookup.getSettingsDirectory();

        try {

            // Set System L&F
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            LoggerFactory.getLogger(App.class).warn("Failed to set system look and feel", e);
        }

        try {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    new App();
                }

            });
        }
        catch (Throwable e) {
            LoggerFactory.getLogger(App.class).warn("An error occurred on startup", e);
        }
    }
}
