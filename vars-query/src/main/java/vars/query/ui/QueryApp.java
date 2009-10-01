/*
 * @(#)QueryApp.java   2009.09.24 at 09:56:26 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.query.ui;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import foxtrot.Task;
import foxtrot.Worker;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.util.Date;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.SwingUtilities;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;
import org.mbari.awt.WaitCursorEventQueue;
import org.mbari.swing.SplashFrame;
import org.mbari.util.Dispatcher;
import org.mbari.util.SystemUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.query.QueryModule;
import vars.shared.ui.FatalErrorSubscriber;
import vars.shared.ui.NonFatalErrorSubscriber;

/**
 * <p><!-- Insert Description --></p>
 *
 * <p> Once instantiated a reference to the query app can be retrieved as follows:
 * <pre>
 * QueryApp queryApp = (QueryApp) Lookup.getApplicationDispatcher().getValueObject();
 * </pre>
 * </p>
 *
 * @author Brian Schlining
 * @version $Id: QueryApp.java 466 2007-01-20 00:59:58Z hohonuuli $
 */
public class QueryApp {

    /**
     * Key used to retrieve the current QueryApp instance from a Dispatcher object.
     * Use as:
     * <pre>
     * QueryApp queryApp = (QueryApp) Dispatcher.getDispatcher(QueryApp.DISPATCHER_KEY_QUERYAPP).getValueObject();
     * </pre>
     */
    public static final String KEY_DISPATCHER_QUERYAPP = "QueryApp";

    /**
     * 378720000 = 1982-01-01
     */
    public static final Date MIN_RECORDED_DATE = new Date(378720000L * 1000L);
    private static Logger log;

    /**
         * @uml.property  name="actionMap"
         * @uml.associationEnd
         */
    private ActionMap actionMap;
    private final EventTopicSubscriber<Exception> fatalErrorSubscriber;

    /**
         * @uml.property  name="inputMap"
         * @uml.associationEnd
         */
    private InputMap inputMap;
    private final KnowledgebaseDAOFactory knowledgebaseDAOFactory;
    private final EventTopicSubscriber nonFatalErrorSubscriber;

    /**
         * @uml.property  name="queryFrame"
         * @uml.associationEnd
         */
    private QueryFrame queryFrame;

    /**
     * Constructs ...
     *
     *
     * @param knowledgebaseDAOFactory
     */
    @Inject
    public QueryApp(KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
        super();
        this.knowledgebaseDAOFactory = knowledgebaseDAOFactory;

        /*
         * We put this application into a dispatcher so that other components
         * like dialogs, can grab it.
         */
        Lookup.getApplicationDispatcher().setValueObject(this);


        initialize();
        fatalErrorSubscriber = new FatalErrorSubscriber(getQueryFrame());
        nonFatalErrorSubscriber = new NonFatalErrorSubscriber(getQueryFrame());

        /*
         * Subscribe to all our favorite topics
         */
        EventBus.subscribe(Lookup.TOPIC_FATAL_ERROR, fatalErrorSubscriber);
        EventBus.subscribe(Lookup.TOPIC_NONFATAL_ERROR, nonFatalErrorSubscriber);

    }

    /**
         * @return  Returns the actionMap.
         * @uml.property  name="actionMap"
         */
    public ActionMap getActionMap() {
        if (actionMap == null) {
            actionMap = new ActionMap();
        }

        return actionMap;
    }

    static Injector getGuiceInjector() {

        Dispatcher dispatcher = Lookup.getGuiceInjectorDispatcher();
        Injector injector = (Injector) dispatcher.getValueObject();
        if (injector == null) {
            injector = Guice.createInjector(new QueryModule());
            dispatcher.setValueObject(injector);
        }

        return injector;

    }

    /**
         * @return  Returns the inputMap.
         * @uml.property  name="inputMap"
         */
    public InputMap getInputMap() {
        if (inputMap == null) {
            inputMap = new InputMap();
        }

        return inputMap;
    }

    /**
     * Do NOT initialize a log until the 'user.timezone' property has been
     * set or you will not be able to store dates in the UTC timezone! This
     */
    private static Logger getLog() {
        if (log == null) {
            log = LoggerFactory.getLogger(QueryApp.class);
        }

        return log;
    }

    /**
         * <p><!-- Method description --></p>
         * @return
         * @uml.property  name="queryFrame"
         */
    public QueryFrame getQueryFrame() {
        if (queryFrame == null) {

            // Let's let Guice autowire the dependencies
            queryFrame = getGuiceInjector().getInstance(QueryFrame.class);
            Lookup.getApplicationFrameDispatcher().setValueObject(queryFrame);
        }

        return queryFrame;
    }

    /**
     * <p><!-- Method description --></p>
     *
     */
    private void initialize() {
        ImageIcon mbariLogo = new ImageIcon(getClass().getResource("/images/vars/query/query-splash.png"));
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
                    try {
                        knowledgebaseDAOFactory.newConceptDAO().findRoot();
                    }
                    catch (Exception e) {
                        EventBus.publish(Lookup.TOPIC_FATAL_ERROR, e);
                    }

                    return null;
                }
            });
        }
        catch (Exception e) {
            getLog().error("Failed to load the knowledgebase", e);
            splashFrame.setMessage(" Error: Failed to load the knowledgebase");
            splashFrame.repaint();

            // Give folks a moment to read the error message.
            try {
                Thread.sleep(4000);
            }
            catch (InterruptedException e1) {

                // Do nothing. we're exiting anyway.
            }

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
        getQueryFrame().pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getQueryFrame().getSize();
        double newHeight = (screenSize.getHeight() - 150);
        frameSize.setSize(frameSize.getWidth(), (int) newHeight);
        getQueryFrame().setSize(frameSize);
        getQueryFrame().setVisible(true);
        getQueryFrame().setIconImage(mbariLogo.getImage());
        splashFrame.dispose();

        /*
         * Add a special eventQueue that toggles the cursor if the application is busy
         */
        Toolkit.getDefaultToolkit().getSystemEventQueue().push(new WaitCursorEventQueue(500));
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param args
     */
    public static void main(String[] args) {

        /*
         * We want ALl dates to be stored as UTC in the database. The most
         * fool-proof way to do that is simply to set the JVM timezone to UTC on
         * start.
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

        final Logger mainLog = getLog();
        if (mainLog.isInfoEnabled()) {
            final Date date = new Date();
            mainLog.info("This application was launched at " + date.toString());
        }

        /*
         * Make it pretty on Macs
         */
        if (SystemUtilities.isMacOS()) {
            SystemUtilities.configureMacOSApplication("VARS Query");
        }

        /*
         * Java 8 on windows has a bug in the Window L&F that causes
         * redraw issues. Until this bug is fixed the workaround is to use
         * Metal L&F on Windows.
         */

//        if (!SystemUtilities.isWindowsOS()) {
//            try {
//                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//            }
//            catch (Exception e) {
//                mainLog.info("Unable to set look and feel", e);
//            }
//        }


        final Injector injector = getGuiceInjector();

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                try {
                    final QueryApp queryApp = injector.getInstance(QueryApp.class);
                }
                catch (Exception e) {
                    FatalErrorSubscriber subscriber = new FatalErrorSubscriber(null);
                    subscriber.onEvent(FatalErrorSubscriber.TOPIC_FATAL_ERROR, e);
                }
            }

        });
    }
}
