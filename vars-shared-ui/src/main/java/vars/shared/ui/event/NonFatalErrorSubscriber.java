/*
 * @(#)NonFatalErrorSubscriber.java   2009.12.03 at 08:58:38 PST
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
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.bushe.swing.event.EventTopicSubscriber;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;

/**
 *
 * @author brian
 */
public class NonFatalErrorSubscriber implements EventTopicSubscriber {

    /**  */
    private final Frame parentFrame;

    /**
     * Constructs ...
     *
     * @param parent
     */
    public NonFatalErrorSubscriber(Frame parent) {
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
     * @param error
     */
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
        Icon errorIcon = new ImageIcon(getClass().getResource("/vars/images/yellow-smile.jpg"));
        ErrorInfo errorInfo = new ErrorInfo("VARS - Something exceptional occured (and we don't like that)", msg,
            details, null, data, ErrorLevel.WARNING, null);

        errorPane.setIcon(errorIcon);
        errorPane.setErrorInfo(errorInfo);
        JXErrorPane.showDialog(parentFrame, errorPane);
    }
}


;
