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

import com.google.inject.Guice;
import com.google.inject.Injector;
import foxtrot.Task;
import foxtrot.Worker;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;
import org.mbari.awt.WaitCursorEventQueue;
import org.mbari.swing.SplashFrame;
import org.mbari.util.Dispatcher;
import org.mbari.util.SystemUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.knowledgebase.Concept;
import vars.knowledgebase.KnowledgebaseModule;
import vars.knowledgebase.ui.actions.PopulateDatabaseAction;

/**
 *
 * @version    $Id: KnowledgebaseApp.java 295 2006-07-06 23:47:31Z hohonuuli $
 * @author     <a href="http://www.mbari.org">Monterey Bay Aquarium Research Institute</a>
 */
public class KnowledgebaseApp {

    private static Logger log;
    private KnowledgebaseFrame knowledgebaseFrame;
    private final ToolBelt toolBelt;
    private final EventTopicSubscriber approveHistorySubscriber;

        /**
     * To loosely couple our components, I'm using an event bus to
     * monitor for shutdown messages. We need to hang on to the reference
     * to the EventTopicSubscriber so that it doesn't get garbage collected.
     *
     * This subscriber handles shutdown.
     */
    private final EventTopicSubscriber exitSubscriber = new EventTopicSubscriber() {

        public void onEvent(String topic, Object data) {
            System.exit(0);
        }
    };


    /**
     * This subscriber displays and logs non-fatal errors
     */
    private final EventTopicSubscriber nonFatalErrorSubscriber = new EventTopicSubscriber() {

        public void onEvent(String topic, Object error) {

            String msg = "An error occurred. Refer to details for more information.";
            String details = null;
            Throwable data = null;
            if (error instanceof Throwable) {
                data = (Throwable) error;
                details = formatStackTraceForDialogs(data, true);
            }
            else {
                details = error.toString();
            }


            /*
             * Create an error pane to display the error stuff
             */
            JXErrorPane errorPane = new JXErrorPane();
            Icon errorIcon = new ImageIcon(getClass().getResource("/images/yellow-smile.jpg"));
            ErrorInfo errorInfo = new ErrorInfo("VARS - Something exceptional occured (and we don't like that)", msg, details, null, data, ErrorLevel.WARNING, null);
            errorPane.setIcon(errorIcon);
            errorPane.setErrorInfo(errorInfo);
            JXErrorPane.showDialog((JFrame) Lookup.getApplicationFrameDispatcher().getValueObject(), errorPane);
        }
    };

    /**
     * This subscriber should display a warning message on a fatal error. When
     * the OK button is clicked a notificaiton to the EXIT_TOPIC should be sent
     */
    private final EventTopicSubscriber fatalErrorSubscriber = new EventTopicSubscriber() {

        public void onEvent(String topic, Object error) {

            String msg = randomHaiku();
            String details = null;
            Throwable data = null;
            if (error instanceof Throwable) {
                data = (Throwable) error;
                details = formatStackTraceForDialogs(data, true);
            }
            else {
                details = error.toString();
            }

            /*
             * Create an error pane to display the error stuff
             */
            JXErrorPane errorPane = new JXErrorPane();
            Icon errorIcon = new ImageIcon(getClass().getResource("/images/red-frown_small.png"));
            ErrorInfo errorInfo = new ErrorInfo("VARS - Fatal Error", msg, details, null, data, ErrorLevel.FATAL, null);
            errorPane.setIcon(errorIcon);
            errorPane.setErrorInfo(errorInfo);
            JXErrorPane.showDialog((JFrame) Lookup.getApplicationFrameDispatcher().getValueObject(), errorPane);

        }



        String randomHaiku() {
            final List<String> haikus = new ArrayList<String>() {{
                add("Chaos reigns within.\nReflect, repent, and restart.\nOrder shall return.");
                add("Errors have occurred.\nWe won't tell you where or why.\nLazy programmers.");
                add("A crash reduces\nyour expensive computer\nto a simple stone.");
                add("There is a chasm\nof carbon and silicon\nthe software can't bridge");
                add("Yesterday it worked\nToday it is not working\nSoftware is like that");
                add("To have no errors\nWould be life without meaning\nNo struggle, no joy");
                add("Error messages\ncannot completely convey.\nWe now know shared loss.");
                add("The code was willing,\nIt considered your request,\nBut the chips were weak.");
                add("Wind catches lily\nScatt'ring petals to the wind:\nApplication dies");
                add("Three things are certain:\nDeath, taxes and lost data.\nGuess which has occurred.");
                add("Rather than a beep\nOr a rude error message,\nThese words: \"Restart now.\"");
                add("ABORTED effort:\nClose all that you have.\nYou ask way too much.");
                add("The knowledgebase crashed.\nI am the Blue Screen of Death.\nNo one hears your screams.");
                add("No-one can tell\nwhat God or Heaven will do\nIf you divide by zero.");
            }};

            return haikus.get((int) Math.floor(Math.random() * haikus.size()));

        }
    };

    /**
     * Constructs ...
     *
     */
    public KnowledgebaseApp() {
        super();

        Injector injector = Guice.createInjector(new KnowledgebaseModule());
        Lookup.getGuiceInjectorDispatcher().setValueObject(injector);
        toolBelt = injector.getInstance(ToolBelt.class);
        approveHistorySubscriber = new ApproveHistorySubscriber(toolBelt.getApproveHistoryTask());

        /*
         * Subscribe to all our favorite topics
         */
        EventBus.subscribe(Lookup.TOPIC_APPROVE_HISTORY, approveHistorySubscriber);
        EventBus.subscribe(Lookup.TOPIC_EXIT, exitSubscriber);
        EventBus.subscribe(Lookup.TOPIC_FATAL_ERROR, fatalErrorSubscriber);
        EventBus.subscribe(Lookup.TOPIC_NONFATAL_ERROR, nonFatalErrorSubscriber);

        initialize();

        

        /*
         * We put this application into a dispatcher so that other components
         * like dialogs, can grab it.
         */
        Lookup.getApplicationDispatcher().setValueObject(this);

        /*
         * Ensure that the concept is registered with all listeners when a user logs in.
         * This was originally done with dispatchers. We could also use EventBus.
         */

        Lookup.getUserAccountDispatcher().addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                Dispatcher dispatcher = Lookup.getSelectedConceptDispatcher();
                Concept selectedConcept = (Concept) dispatcher.getValueObject();
                dispatcher.setValueObject(null);
                dispatcher.setValueObject(selectedConcept);
            }
        });
    }

    public KnowledgebaseFrame getKnowledgebaseFrame() {
        if (knowledgebaseFrame == null) {
            knowledgebaseFrame = new KnowledgebaseFrame(toolBelt);

            /*
             * We store the frame here so that other components can easily
             * access it.
             */
            Lookup.getApplicationFrameDispatcher().setValueObject(knowledgebaseFrame);

        }

        return knowledgebaseFrame;
    }

    /**
     * Do NOT initialize a log until the 'user.timezone' property has been
     * set or you will not be able to stroe dates in the UTC timezone! This
     */
    private static Logger getLog() {
        if (log == null) {
            log = LoggerFactory.getLogger(KnowledgebaseApp.class);
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

        // TODO 20050415 brian: Need to load a configuration
        splashFrame.setMessage(" Loading configuration...");
        splashFrame.setVisible(true);
        splashFrame.repaint();

        /*
         * Load knowledgebase
         */
        splashFrame.setMessage(" Loading knowledgebase...");
        splashFrame.repaint();

        /*
         * Load the KB in a seperate thread so the UI remains responsive.
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
            splashFrame.setMessage(" Error: Failed to load the knowledgebase");
            getLog().error("Failed to load the knowledgebase", e);
            splashFrame.repaint();
            JOptionPane.showMessageDialog(splashFrame, "Unable to load knowledgebase", "VARS - Error",
                                          JOptionPane.ERROR_MESSAGE, null);
            System.exit(-1);
        }

        /*
         * Initialize GUI
         *
         * TODO 20050415 brian: Need to use the loaded configuration to
         * initiaize the GUI settings.
         */
        splashFrame.setMessage(" Initializing the GUI...");
        splashFrame.repaint();
        getKnowledgebaseFrame().pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getKnowledgebaseFrame().getSize();
        double newHeight = (screenSize.getHeight() - 150);
        frameSize.setSize(frameSize.getWidth(), (int) newHeight);


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

        getKnowledgebaseFrame().setSize(frameSize);
        getKnowledgebaseFrame().setIconImage(mbariLogo.getImage());

        try {
            Lookup.getSelectedConceptDispatcher().setValueObject(
                toolBelt.getKnowledgebaseDAOFactory().newConceptDAO().findRoot());
        }
        catch (Exception e) {
            log.error("Unable to locate root concept --- this means trouble!!", e);
            EventBus.publish(Lookup.TOPIC_FATAL_ERROR, e);
        }

        getKnowledgebaseFrame().setVisible(true);
        splashFrame.dispose();

        /*
         * Add a special eventQueue that toggles the cursor if the application is busy
         */
        Toolkit.getDefaultToolkit().getSystemEventQueue().push(new WaitCursorEventQueue(500));


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
                new KnowledgebaseApp();
            }

        });
    }

       /**
         * Defines a custom format for the stack trace as String.
         */
        String formatStackTraceForDialogs(Throwable throwable, boolean isCause) {

            //add the class name and any message passed to constructor
            final StringBuilder result = new StringBuilder();
            result.append("<h2>");
            if (isCause) {
                result.append("Caused by: ");
            }

            result.append(throwable.toString()).append("</h2>");
            final String newLine = "<br/>";

            //add each element of the stack trace
            for (StackTraceElement element : throwable.getStackTrace()) {
                result.append(element);
                result.append(newLine);
            }

            final Throwable cause = throwable.getCause();
            if (cause != null) {
                result.append(formatStackTraceForDialogs(cause, true));
            }

            return result.toString();
        }
}
