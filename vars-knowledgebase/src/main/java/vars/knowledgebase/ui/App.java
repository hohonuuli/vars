/*
 * @(#)KnowledgebaseApp.java   2009.10.27 at 11:06:16 PDT
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

import com.google.inject.Injector;
import foxtrot.Task;
import foxtrot.Worker;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Date;
import java.util.TimeZone;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;
import org.mbari.swing.SplashFrame;
import org.mbari.util.Dispatcher;
import org.mbari.util.SystemUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ui.actions.PopulateDatabaseAction;
import vars.shared.ui.event.ExitTopicSubscriber;
import vars.shared.ui.event.FatalExceptionSubscriber;
import vars.shared.ui.event.NonFatalErrorSubscriber;
import vars.shared.ui.event.WarningSubscriber;

/**
 *
 * @author     <a href="http://www.mbari.org">Monterey Bay Aquarium Research Institute</a>
 */
public class App {

    private static Logger log;
    private KnowledgebaseFrame knowledgebaseFrame;
    private final ToolBelt toolBelt;
    private final EventTopicSubscriber approveHistorySubscriber;
    private final EventTopicSubscriber approveHistoriesSubscriber;

    /**
     * To loosely couple our components, I'm using an event bus to
     * monitor for shutdown messages. We need to hang on to the reference
     * to the EventTopicSubscriber so that it doesn't get garbage collected.
     *
     * This subscriber handles shutdown.
     */
    private final EventTopicSubscriber exitSubscriber = new ExitTopicSubscriber();


    /**
     * This subscriber displays and logs non-fatal errors
     */
    private final EventTopicSubscriber nonFatalErrorSubscriber;

    /**
     * This subscriber should display a warning message on a fatal error. When
     * the OK button is clicked a notification to the EXIT_TOPIC should be sent
     */
    private final EventTopicSubscriber fatalErrorSubscriber;

    /**
     * This subscriber displays a warning dialog containing any messages sent to it.
     */
    private final EventTopicSubscriber warningSubscriber;

    /**
     * Constructs ...
     *
     */
    public App() {
        super();

        Injector injector = StateLookup.GUICE_INJECTOR;
        toolBelt = injector.getInstance(ToolBelt.class);
        approveHistorySubscriber = new ApproveHistorySubscriber(toolBelt.getApproveHistoryTask());
        approveHistoriesSubscriber = new ApproveHistoriesSubscriber(toolBelt.getApproveHistoryTask());
        // Temporary subscribers to show any error messages that occur during startup
        EventTopicSubscriber tempFatalErrorSubscriber  = new FatalExceptionSubscriber(null);
        EventTopicSubscriber tempNonFatalErrorSubscriber = new NonFatalErrorSubscriber(null);
        
        /*
         * Subscribe to all our favorite topics
         */
        EventBus.subscribe(StateLookup.TOPIC_APPROVE_HISTORY, approveHistorySubscriber);
        EventBus.subscribe(StateLookup.TOPIC_APPROVE_HISTORIES, approveHistoriesSubscriber);
        EventBus.subscribe(StateLookup.TOPIC_EXIT, exitSubscriber);
        EventBus.subscribe(StateLookup.TOPIC_FATAL_ERROR, tempFatalErrorSubscriber);
        EventBus.subscribe(StateLookup.TOPIC_NONFATAL_ERROR, tempNonFatalErrorSubscriber);
        
        initialize();

        // After the app is intialiazed, remove the temporary error subscribers
        // and add the permanant ones
        EventBus.unsubscribe(StateLookup.TOPIC_FATAL_ERROR, tempFatalErrorSubscriber);
        EventBus.unsubscribe(StateLookup.TOPIC_NONFATAL_ERROR, tempNonFatalErrorSubscriber);
        fatalErrorSubscriber  = new FatalExceptionSubscriber(getKnowledgebaseFrame());
        nonFatalErrorSubscriber = new NonFatalErrorSubscriber(getKnowledgebaseFrame());
        warningSubscriber = new WarningSubscriber(getKnowledgebaseFrame());
        EventBus.subscribe(StateLookup.TOPIC_FATAL_ERROR, fatalErrorSubscriber);
        EventBus.subscribe(StateLookup.TOPIC_NONFATAL_ERROR, nonFatalErrorSubscriber);
        EventBus.subscribe(StateLookup.TOPIC_WARNING, warningSubscriber);


        /*
         * We put this application into a dispatcher so that other components
         * like dialogs, can grab it.
         */
        StateLookup.setApplication(this);

        /*
         * Ensure that the concept is registered with all listeners when a user logs in.
         */
        StateLookup.userAccountProperty().addListener((obs, oldVal, newVal) -> {
            Concept selectedConcept = StateLookup.getSelectedConcept();
            StateLookup.setSelectedConcept(null);
            StateLookup.setSelectedConcept(selectedConcept);
        });

    }

    public KnowledgebaseFrame getKnowledgebaseFrame() {
        if (knowledgebaseFrame == null) {
            knowledgebaseFrame = new KnowledgebaseFrame(toolBelt);

            /*
             * We store the frame here so that other components can easily
             * access it.
             */
            StateLookup.setApplicationFrame(knowledgebaseFrame);
            StateLookup.setSelectedFrame(knowledgebaseFrame);

        }

        return knowledgebaseFrame;
    }

    /**
     * Do NOT initialize a log until the 'user.timezone' property has been
     * set or you will not be able to store dates in the UTC timezone! This
     */
    private static Logger getLog() {
        if (log == null) {
            log = LoggerFactory.getLogger(App.class);
        }

        return log;
    }

    /**
     * <p>UI initialization</p>
     *
     */
    private void initialize() {
        ImageIcon mbariLogo = new ImageIcon(
            getClass().getResource("/images/vars/knowledgebase/knowledgebase-splash.png"));
        SplashFrame splashFrame = new SplashFrame(mbariLogo);

        /*
         * Load knowledgebase
         */
        splashFrame.setVisible(true);
        splashFrame.setMessage(" Loading knowledgebase...");
        splashFrame.repaint();

        /*
         * Load the KB in a separate thread so the UI remains responsive.
         */
        try {
            Worker.post(new Task() {

                public Object run() throws Exception {
                    toolBelt.getKnowledgebaseDAOFactory().newConceptDAO().findRoot();
                    return null;
                }

            });
        }
        catch (Exception e) {
            splashFrame.setMessage(" WARNING: Failed to load the knowledgebase");
            getLog().warn("Failed to load the knowledgebase", e);
            splashFrame.repaint();
            JOptionPane.showMessageDialog(splashFrame, "Unable to load knowledgebase", "VARS - Error",
                                          JOptionPane.ERROR_MESSAGE, null);
            System.exit(-1);
        }


        /*
         * Make sure that the knowledgebase exists. If it's empty then give the
         * user the oppurtunity to create a root object
         */
        try {
            (new PopulateDatabaseAction(toolBelt)).doAction();
        }
        catch (Exception e) {
            splashFrame.setMessage(" Error: Failed to load the knowledgebase");
            getLog().error("Failed to load the knowledgebase", e);
            splashFrame.repaint();
            JOptionPane.showMessageDialog(splashFrame, "Unable to load knowledgebase", "VARS - Error",
                                          JOptionPane.ERROR_MESSAGE, null);
            System.exit(-1);
        }

        /*
         * Initialize GUI
         */
        splashFrame.setMessage(" Initializing the GUI...");
        splashFrame.repaint();
        getKnowledgebaseFrame().pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getKnowledgebaseFrame().getSize();
        double newHeight = (screenSize.getHeight() - 150);
        frameSize.setSize(frameSize.getWidth(), (int) newHeight);

        getKnowledgebaseFrame().setSize(frameSize);
        getKnowledgebaseFrame().setIconImage(mbariLogo.getImage());

        try {
            ConceptDAO dao = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
            dao.startTransaction();
            Concept rootConcept = dao.findRoot();
            dao.endTransaction();
            dao.close();
            StateLookup.setSelectedConcept(rootConcept);
        }
        catch (Exception e) {
            log.error("Unable to locate root concept --- this means trouble!!", e);
            EventBus.publish(StateLookup.TOPIC_FATAL_ERROR, e);
        }

        getKnowledgebaseFrame().setVisible(true);
        splashFrame.dispose();

        /*
         * Add a special eventQueue that toggles the cursor if the application is busy
         * NOTE: This causes problems on JDK8
         */
        //Toolkit.getDefaultToolkit().getSystemEventQueue().push(new WaitCursorEventQueue(500));


    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        /*
         * Castor.properties has a setting that tells castor to save Dates
         * as GMT. IN order to minimize confusion in displays we set
         * the timezone of the application to GMT too. This way database
         * values and displayed values are always the same
         */
        System.setProperty("user.timezone", "UTC");
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

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
         * Create an application settings directory if needed and create the log directory
         */
        String home = System.getProperty("user.home");

        File settingsDirectory = new File(home, ".vars");
        if (!settingsDirectory.exists()) {
            settingsDirectory.mkdir();
        }

        File logDirectory = new File(settingsDirectory, "logs");
        if (!logDirectory.exists()) {
            logDirectory.mkdir();
        }

        final Logger myLog = getLog();
        if (myLog.isInfoEnabled()) {
            final Date date = new Date();
            myLog.info("This application was launched at " + date.toString());
        }

        /*
         * Make it pretty on Macs
         */
        if (SystemUtilities.isMacOS()) {
            SystemUtilities.configureMacOSApplication("VARS Knowledgebase");
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            myLog.info("Unable to set look and feel", e);
        }



        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                new App();
            }

        });
    }

}
