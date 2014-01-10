/*
 * @(#)QueryApp.java   2009.12.03 at 10:01:14 PST
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



package vars.query.ui;

import com.google.inject.Inject;
import com.google.inject.Injector;
import foxtrot.Task;
import foxtrot.Worker;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;
import org.mbari.awt.WaitCursorEventQueue;
import org.mbari.swing.SplashFrame;
import org.mbari.util.SystemUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.shared.ui.GlobalLookup;
import vars.shared.ui.event.FatalExceptionSubscriber;
import vars.shared.ui.event.NonFatalErrorSubscriber;
import vars.shared.ui.event.WarningSubscriber;

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
public class App {


    /**
     * 378720000 = 1982-01-01
     */
    public static final Date MIN_RECORDED_DATE = new Date(378720000L * 1000L);
    private static Logger log;
    private ActionMap actionMap;
    private InputMap inputMap;
    private final KnowledgebaseDAOFactory knowledgebaseDAOFactory;
    private QueryFrame queryFrame;

    /**
     * The App gets garbage collected shortly after startup. To
     * hang on to the EventTopicSubscribers we store them in a static list. This
     * is very important, otherwise they get gc'd too.
     */
    private static final List<EventTopicSubscriber> GC_PREVENTION = new Vector<EventTopicSubscriber>();

    /**
     * Constructs ...
     *
     *
     * @param knowledgebaseDAOFactory
     */
    @Inject
    public App(KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
        super();
        this.knowledgebaseDAOFactory = knowledgebaseDAOFactory;

        /*
         * We put this application into a dispatcher so that other components
         * like dialogs, can grab it.
         */
        Lookup.getApplicationDispatcher().setValueObject(this);

        initialize();

        /*
         * Subscribe to all our favorite topics
         */
        EventTopicSubscriber fatalErrorSubscriber = new FatalExceptionSubscriber(getQueryFrame());
        EventTopicSubscriber nonFatalErrorSubscriber = new NonFatalErrorSubscriber(getQueryFrame());
        EventTopicSubscriber warningSubscriber = new WarningSubscriber(getQueryFrame());
        EventBus.subscribe(Lookup.TOPIC_FATAL_ERROR, fatalErrorSubscriber);
        EventBus.subscribe(Lookup.TOPIC_NONFATAL_ERROR, nonFatalErrorSubscriber);
        EventBus.subscribe(Lookup.TOPIC_WARNING, warningSubscriber);
        GC_PREVENTION.add(fatalErrorSubscriber);
        GC_PREVENTION.add(nonFatalErrorSubscriber);
        GC_PREVENTION.add(warningSubscriber);

    }

    /**
     * @return  Returns the actionMap.
     */
    public ActionMap getActionMap() {
        if (actionMap == null) {
            actionMap = new ActionMap();
        }

        return actionMap;
    }


    /**
     * @return  Returns the inputMap.
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
            log = LoggerFactory.getLogger(App.class);
        }

        return log;
    }

    /**
     * @return
     */
    public QueryFrame getQueryFrame() {
        if (queryFrame == null) {

            // Let's let Guice autowire the dependencies
        	Injector injector = (Injector) Lookup.getGuiceInjectorDispatcher().getValueObject();
            queryFrame = injector.getInstance(QueryFrame.class);
            queryFrame.setSize(300, 200);
            Lookup.getApplicationFrameDispatcher().setValueObject(queryFrame);
        }

        return queryFrame;
    }

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
         * Load the KB in a separate thread so the UI remains responsive.
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
        frameSize.setSize(650, (int) newHeight);
        getQueryFrame().setSize(frameSize);
        getQueryFrame().setVisible(true);
        getQueryFrame().setIconImage(mbariLogo.getImage());
        splashFrame.dispose();

        /*
         * Add a special eventQueue that toggles the cursor if the application is busy
         * NOTE: This causes problems on JDK8
         */
          //Toolkit.getDefaultToolkit().getSystemEventQueue().push(new WaitCursorEventQueue(500));
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
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        GlobalLookup.getSettingsDirectory(); // Not used

        /**
         * Log uncaught Exceptions
         */
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread thread, Throwable e) {
                Logger log = LoggerFactory.getLogger(thread.getClass());
                log.error("Exception in thread [" + thread.getName() + "]", e);
            }
        });


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
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (final Exception e) {
            mainLog.info("Unable to set look and feel", e);
        }

        final Injector injector = (Injector) Lookup.getGuiceInjectorDispatcher().getValueObject();

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                try {
                    final App queryApp = injector.getInstance(App.class);
                }
                catch (Exception e) {
                    FatalExceptionSubscriber subscriber = new FatalExceptionSubscriber(null);
                    subscriber.onEvent(Lookup.TOPIC_FATAL_ERROR, e);
                }
            }

        });
    }
}
