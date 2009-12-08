/*
 * @(#)App.java   2009.12.05 at 10:45:49 PST
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

import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.UIManager;
import org.bushe.swing.event.EventTopicSubscriber;
import org.mbari.util.SystemUtilities;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;
import javax.swing.SwingUtilities;
import org.bushe.swing.event.EventBus;

import vars.annotation.Observation;
import vars.shared.ui.GlobalLookup;
import vars.shared.ui.event.ExitTopicSubscriber;
import vars.shared.ui.event.FatalExceptionSubscriber;
import vars.shared.ui.event.NonFatalErrorSubscriber;
import vars.shared.ui.event.WarningSubscriber;

/**
 *
 * @author brian
 */
public class App {

    private final EventTopicSubscriber<Exception> fatalErrorSubscriber;
    private final EventTopicSubscriber nonFatalErrorSubscriber;
    private final EventTopicSubscriber exitSubscriber;
    private final EventTopicSubscriber<String> warningSubscriber;
    private AnnotationFrame annotationFrame;
    private final ToolBelt toolBelt;

    public App() {
    	final Injector injector = (Injector) Lookup.getGuiceInjectorDispatcher().getValueObject();
    	toolBelt = injector.getInstance(ToolBelt.class);
    	Lookup.getSelectedObservationsDispatcher().setValueObject(new Vector<Observation>());
        fatalErrorSubscriber = new FatalExceptionSubscriber(getAnnotationFrame());
        nonFatalErrorSubscriber = new NonFatalErrorSubscriber(getAnnotationFrame());
        warningSubscriber = new WarningSubscriber(getAnnotationFrame());
        exitSubscriber = new ExitTopicSubscriber();

        EventBus.subscribe(Lookup.TOPIC_FATAL_ERROR, fatalErrorSubscriber);
        EventBus.subscribe(Lookup.TOPIC_NONFATAL_ERROR, nonFatalErrorSubscriber);
        EventBus.subscribe(Lookup.TOPIC_WARNING, warningSubscriber);
        EventBus.subscribe(Lookup.TOPIC_EXIT, exitSubscriber);

        JFrame frame = getAnnotationFrame();
        frame.pack();
        frame.setVisible(true);
    }

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

        /*
         * Make it pretty on Macs
         */
        if (SystemUtilities.isMacOS()) {
            SystemUtilities.configureMacOSApplication("VARS Annotation");
        }

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
