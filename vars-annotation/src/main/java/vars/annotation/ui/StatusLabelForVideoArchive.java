/*
 * @(#)StatusLabelForVideoArchive.java   2009.11.17 at 09:35:07 PST
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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JDialog;
import org.mbari.swing.SwingUtils;
import org.mbari.util.Dispatcher;

import vars.annotation.AnnotationDAOFactory;
import vars.annotation.VideoArchive;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.actions.ShowOpenVideoArchiveDialogAction;

/**
 * <p>Indicates which {@link VideoArchive} the annotator is editing. Clicking on the
 * label opens a dialog allowing the user to change the {@link VideoArchive} being
 * edited.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public class StatusLabelForVideoArchive extends StatusLabel {

    private ShowOpenVideoArchiveDialogAction action;
    private final AnnotationDAOFactory annotationDAOFactory;

    /**
     * Constructor for the StatusLabelForVideoArchive object
     */
    public StatusLabelForVideoArchive(AnnotationDAOFactory annotationDAOFactory) {
        super();
        this.annotationDAOFactory = annotationDAOFactory;
        Dispatcher dispatcher = Lookup.getVideoArchiveDispatcher();
        dispatcher.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                update((VideoArchive) evt.getNewValue());
            }

        });
        update((VideoArchive) dispatcher.getValueObject());

        /*
         * On click show a dialog allowing a user to open a VideoArchive
         */
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(final MouseEvent me) {
                SwingUtils.flashJComponent(StatusLabelForVideoArchive.this, 2);
                final JDialog dialog = getAction().getDialog();

                /*
                 * Centers the dialog on screen
                 */
                dialog.setLocationRelativeTo(null);
                getAction().doAction();
            }

        });
    }

    private ShowOpenVideoArchiveDialogAction getAction() {
        if (action == null) {
            action = new ShowOpenVideoArchiveDialogAction(annotationDAOFactory);
        }

        return action;
    }

    /**
     *
     * @param evt
     */
    public void propertyChange(PropertyChangeEvent evt) {
        update((VideoArchive) evt.getNewValue());
    }

    /**
     * Sets the videoArchive registered with the label. In general, you don't need
     * to call this. This status label registers with
     * PredefinedDispatcher.VIDEOARCHIVE and listens for when the videoArchive
     * is set there.
     *
     * @param videoArchive Sets the videoArchive to be registered with the label
     */
    public void update(final VideoArchive videoArchive) {
        boolean ok = false;
        String text = "NONE";
        String toolTip = text;
        if (videoArchive != null) {
            text = videoArchive.getName() + "";
            toolTip = text;

            if ((text.length() > 20) &&
                    (text.toLowerCase().startsWith("http:") || text.toLowerCase().startsWith("file:"))) {
                String[] parts = text.split("/");
                if (parts.length > 0) {
                    text = ".../" + parts[parts.length - 1];
                }

            }

            ok = true;
        }

        setText("Video: " + text);
        setToolTipText(toolTip);
        setOk(ok);
    }
}
