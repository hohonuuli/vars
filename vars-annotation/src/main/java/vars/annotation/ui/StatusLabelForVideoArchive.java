/*
 * @(#)StatusLabelForVideoArchive.java   2009.12.10 at 09:33:48 PST
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

import java.awt.Frame;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import org.mbari.swing.SwingUtils;
import org.mbari.util.Dispatcher;
import vars.annotation.VideoArchive;
import vars.old.annotation.ui.StatusLabel;

/**
 * <p>Indicates which {@link VideoArchive} the annotator is editing. Clicking on the
 * label opens a dialog allowing the user to change the {@link VideoArchive} being
 * edited.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public class StatusLabelForVideoArchive extends StatusLabel {

    private final JDialog dialog;

    /**
     * Constructor for the StatusLabelForVideoArchive object
     *
     * @param persistenceController
     */
    public StatusLabelForVideoArchive(ToolBelt toolBelt) {
        super();
        Frame frame = (Frame) Lookup.getApplicationFrameDispatcher().getValueObject();
        this.dialog = new vars.annotation.ui.dialogs.OpenVideoArchiveDialog(frame, toolBelt);

        /*
         * Listen for changes in the VideoArchive being annotated. When it changes update
         * the label text
         */
        Dispatcher dispatcher = Lookup.getVideoArchiveDispatcher();
        dispatcher.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                update((VideoArchive) evt.getNewValue());
            }

        });
        update((VideoArchive) dispatcher.getValueObject());


        /*
         * When the user clicks this label a dialog should pop up allowing them
         * to open the VCR.
         */
        addMouseListener(new MouseAdapter() {

            Frame frame = (Frame) Lookup.getApplicationFrameDispatcher().getValueObject();

            @Override
            public void mouseClicked(final MouseEvent me) {
                SwingUtils.flashJComponent(StatusLabelForVideoArchive.this, 2);

                final Point mousePosition = me.getPoint();

                SwingUtilities.convertPointToScreen(mousePosition, StatusLabelForVideoArchive.this);

                final int x = mousePosition.x;
                final int y = mousePosition.y - dialog.getHeight();

                dialog.setLocation(x, y);
                dialog.setVisible(true);

            }


        });

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
