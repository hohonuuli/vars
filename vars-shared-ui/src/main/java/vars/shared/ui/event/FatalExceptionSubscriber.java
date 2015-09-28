/*
 * @(#)FatalExceptionSubscriber.java   2009.12.03 at 08:58:36 PST
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



package vars.shared.ui.event;

import java.awt.Frame;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.google.common.collect.Lists;
import org.bushe.swing.event.EventTopicSubscriber;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;

/**
 *
 * @author brian
 */
public class FatalExceptionSubscriber implements EventTopicSubscriber<Exception> {

    /**  */
    private final Frame parentFrame;

    /**
     * Constructs ...
     *
     * @param parent
     */
    public FatalExceptionSubscriber(Frame parent) {
        this.parentFrame = parent;
    }

    /**
     * Defines a custom format for the stack trace as String.
     */
    String formatStackTraceForDialogs(Throwable throwable, boolean isCause) {

        //add the class name and any message passed to constructor
        final StringBuilder result = new StringBuilder();

        result.append("<h3>");

        if (isCause) {
            result.append("Caused by: ");
        }

        result.append(throwable.toString()).append("</h3>");

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

    /**
     *
     * @param topic
     * @param data
     */
    public void onEvent(String topic, Exception data) {

        String msg = randomHaiku();
        String details = formatStackTraceForDialogs(data, false);

        /*
         * Create an error pane to display the error stuff
         */
        JXErrorPane errorPane = new JXErrorPane();
        Icon errorIcon = randomImage();
        ErrorInfo errorInfo = new ErrorInfo("VARS - Fatal Error", msg, details, null, data, ErrorLevel.FATAL, null);

        errorPane.setIcon(errorIcon);
        errorPane.setErrorInfo(errorInfo);
        errorPane.setErrorReporter(new EmailErrorReporter(errorPane));
        JXErrorPane.showDialog(null, errorPane);

    }

    String randomHaiku() {
        final List<String> haikus = new ArrayList<String>() {

            {
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
                add("Some bugs have names\nOthers inscrutable numbers\nYours has not even that.");
                add("Riddle for student\nWhat error is so fatal\nIt has no message");
                add("Technical support\nWould be a flowing source of\nSweet commiseration.");
                add("Something you entered\ntranscended parameters\nSo much is unknown.");
                add("Your data was big\nIt might be very useful\nBut now it is gone.");
                add("In a wired world\n distractions consume ones thoughts\n... mindfulness returns");
            }
        };

        return haikus.get((int) Math.floor(Math.random() * haikus.size()));

    }

    ImageIcon randomImage() {
        final List<String> images = new ArrayList<String>() {
            {
                add("/vars/images/red-frown_small.png");
                add("/vars/images/fatal/202.jpg");
                add("/vars/images/fatal/2010-01-04-null-pointer-exception.png");
                add("/vars/images/fatal/1204185247268.jpg");
                add("/vars/images/fatal/actualbug.jpg");
                add("/vars/images/fatal/bug.png");
                add("/vars/images/fatal/catchAllTheErrors-615x461.png");
                add("/vars/images/fatal/epic-fail4.jpg");
                add("/vars/images/fatal/error_cartoon.gif");
                add("/vars/images/fatal/fatal-error-cartoon.jpg");
                add("/vars/images/fatal/Flag_of_Edward_England.svg.png");
                add("/vars/images/fatal/Funny-Animals-EPIC-FAIL-Indeed.jpg");
                add("/vars/images/fatal/its_okay_i_wrote_an_exception.jpg");
                add("/vars/images/fatal/xkcd2.png");
            }

        };
        String image = images.get((int) Math.floor(Math.random() * images.size()));
        return new ImageIcon(getClass().getResource(image));
    }


}
