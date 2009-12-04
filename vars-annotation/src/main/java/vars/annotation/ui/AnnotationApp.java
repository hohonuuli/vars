/*
 * @(#)AnnotationApp.java   2009.11.13 at 11:01:49 PST
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
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;
import org.mbari.swing.SplashFrame;
import org.mbari.util.SystemUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.shared.ui.GlobalLookup;
import vars.shared.ui.event.FatalExceptionSubscriber;
import vars.shared.ui.event.NonFatalErrorSubscriber;
import vars.shared.ui.event.WarningSubscriber;

/**
 * <p>This is the main class for the Annotation Application.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public class AnnotationApp {

    private static Logger log;
    private JFrame annotationAppFrame;
    private final EventTopicSubscriber<String> warningSubscriber = new WarningSubscriber(getAnnotationAppFrame());
    private final EventTopicSubscriber<Exception> fatalExceptionSubscriber = new FatalExceptionSubscriber(getAnnotationAppFrame());
    private final EventTopicSubscriber nonFatalErrorSubscriber = new NonFatalErrorSubscriber(getAnnotationAppFrame());

    /**
     * Constructor for the AnnotationApp object
     */
    public AnnotationApp() {
        initialize();
        Lookup.getApplicationDispatcher().setValueObject(this);
        EventBus.publish(Lookup.TOPIC_DATABASE_STATUS, Boolean.TRUE);
        EventBus.subscribe(Lookup.TOPIC_WARNING, warningSubscriber);
        EventBus.subscribe(Lookup.TOPIC_FATAL_ERROR, fatalExceptionSubscriber);
        EventBus.subscribe(Lookup.TOPIC_NONFATAL_ERROR, nonFatalErrorSubscriber);
    }

    private JFrame getAnnotationAppFrame() {
        if (annotationAppFrame == null) {
            final Injector injector = (Injector) Lookup.getGuiceInjectorDispatcher().getValueObject();
            annotationAppFrame = new AnnotationFrame(injector.getInstance(ToolBelt.class));
            annotationAppFrame.pack();

            // Center the frame on screen
            final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            final Dimension frameSize = annotationAppFrame.getSize();

            frameSize.height = (int) (screenSize.height * 0.8);
            frameSize.width = (int) (screenSize.width * 0.8);
            annotationAppFrame.setLocation((screenSize.width - frameSize.width) / 2,
                                           (screenSize.height - frameSize.height) / 2);
            annotationAppFrame.setSize(frameSize);
            Lookup.getApplicationFrameDispatcher().setValueObject(annotationAppFrame);
        }

        return annotationAppFrame;
    }

    /**
     * Do NOT initialize a log until the 'user.timezone' property has been
     * set or you will not be able to stores dates in the UTC timezone! This
     */
    private static Logger getLog() {
        if (log == null) {
            log = LoggerFactory.getLogger(AnnotationApp.class);
        }

        return log;
    }

    private void initialize() {
        final ImageIcon mbariLogo = new ImageIcon(
            getClass().getResource("/images/vars/annotation/annotation-splash.png"));
        final SplashFrame splashFrame = new SplashFrame(mbariLogo);

        splashFrame.setMessage(" Loading configuration...");
        splashFrame.setVisible(true);
        splashFrame.repaint();

        /*
         * Load the KB in a seperate thread so the UI remains responsive.
         */
        splashFrame.setMessage(" Loading knowledgebase...");
        getAnnotationAppFrame().setVisible(true);
        getAnnotationAppFrame().setIconImage(mbariLogo.getImage());
        JFrame.setDefaultLookAndFeelDecorated(true);
        splashFrame.repaint();
        splashFrame.dispose();
    }

    /**
     * The main program for the AnnotationApp class
     *
     * @param  argv             The command line arguments
     */
    public static void main(final String[] argv) {

        /*
         * Castor.properties has a setting that tells castor to save Dates
         * as GMT. IN order to minimize confusion in displays we set
         * the timezone of the application to GMT too. This way database
         * values and displayed values are always the same
         */
        System.setProperty("user.timezone", "UTC");
        GlobalLookup.getSettingsDirectory(); // Make sure log directory gets created

        /*
         * Make it pretty on Macs
         */
        if (SystemUtilities.isMacOS()) {
            SystemUtilities.configureMacOSApplication("VARS Annotation");
        }
        

        final Logger myLog = getLog();

        if (myLog.isInfoEnabled()) {
            final Date date = new Date();

            myLog.info("This application was launched at " + date.toString());
        }
        


        try {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch (final Exception e) {
                myLog.info("Unable to set look and feel", e);
            }

            javax.swing.SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    new AnnotationApp();
                }
            });
        }
        catch (final Throwable e) {
            myLog.error("Error occured on startup", e);
        }
    }
}
