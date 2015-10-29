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

import com.google.common.collect.Lists;
import com.google.inject.Injector;
import foxtrot.Job;
import foxtrot.Worker;

import java.util.Collection;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;
import javax.swing.*;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;
import org.bushe.swing.event.EventTopicSubscriber;
import org.mbari.swing.SplashFrame;
import org.mbari.util.SystemUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.AnnotationPersistenceService;
import vars.annotation.Observation;
import vars.annotation.ui.eventbus.ExitTopicSubscriber;
import vars.annotation.ui.eventbus.VideoArchiveChangedEvent;
import vars.knowledgebase.Concept;
import vars.shared.ui.GlobalLookup;
import vars.shared.ui.event.FatalExceptionSubscriber;
import vars.shared.ui.event.LoggingEventSubscriber;
import vars.shared.ui.event.NonFatalErrorSubscriber;
import vars.shared.ui.event.WarningSubscriber;
import vars.avplayer.FakeImageCaptureServiceImpl;
import vars.shared.util.ActiveAppBeacon;
import vars.shared.util.ActiveAppPinger;

/**
 *
 * @author brian
 */
public class App {

    private AnnotationFrame annotationFrame;
    private ToolBelt toolBelt;
    
    /**
     * The App gets garbage collected shortly after startup. To
     * hang on to the EventTopicSubscribers we store them in a static list. This
     * is very important, otherwise they get gc'd too.
     */
    private static final List<EventTopicSubscriber> GC_PREVENTION = new Vector<EventTopicSubscriber>();
    private static final List<EventSubscriber> GC_PREVENTION_EVENTS = new Vector<EventSubscriber>();

    /**
     * For VCR's, we only want one instance of VARS running as the first instance owns the
     * serial ports and the framecapture card.
     */
    public static final Collection<Integer> BEACON_PORTS = Lists.newArrayList(4002, 4121, 5097, 6238, 6609,
            7407, 8169, 9069, 9669, 16569);
    public static final String BEACON_MESSAGE = "VARS Annotation";
    private static ActiveAppBeacon activeAppBeacon;

    /**
     * Constructs ...
     */
    public App() {

        final ImageIcon mbariLogo =
                new ImageIcon(getClass().getResource("/annotation-splash.png"));
        final SplashFrame splashFrame = new SplashFrame(mbariLogo);
        splashFrame.setVisible(true);
        splashFrame.setMessage(" Starting application beacon ...");
        activeAppBeacon = new ActiveAppBeacon(BEACON_PORTS, BEACON_MESSAGE);

        splashFrame.setMessage(" Initializing configuration ...");


        final Injector injector = (Injector) Lookup.getGuiceInjectorDispatcher().getValueObject();
        try {
            toolBelt = injector.getInstance(ToolBelt.class);
        }
        catch (Exception e) {
            Exception ex = new RuntimeException("Initialization failed. Perhaps VARS can't connect to the database", e);
            (new FatalExceptionSubscriber(null)).onEvent(Lookup.TOPIC_FATAL_ERROR, ex);
        }

        // HACK - For failed initialization... e.g. Database isn't running.
        // Hate putting a system exit call in constructor. But it works.
        if (toolBelt == null) {
            System.exit(-1);
        }

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
        splashFrame.setMessage(" Pre-loading knowledgebase ... be patient");
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
        Lookup.getImageCaptureServiceDispatcher().setValueObject(injector.getInstance(ImageCaptureService.class));

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

        /*
         * Check that VARS is not already running
         */
        if (ActiveAppPinger.pingAll(BEACON_PORTS, BEACON_MESSAGE)) {
            JOptionPane.showMessageDialog(null, "An instance of the VARS Annotation application is already running. Exiting ...");
        }
        else {
            try {
                SwingUtilities.invokeLater(() -> new App());
            }
            catch (Throwable e) {
                LoggerFactory.getLogger(App.class).warn("An error occurred on startup", e);
            }
        }

    }
}
