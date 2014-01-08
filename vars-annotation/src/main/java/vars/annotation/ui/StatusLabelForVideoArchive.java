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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.SwingUtilities;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.mbari.swing.SwingUtils;
import org.mbari.util.Dispatcher;
import org.mbari.util.Tuple2;
import vars.annotation.VideoArchive;
import vars.annotation.ui.eventbus.VideoArchiveChangedEvent;
import vars.annotation.ui.videofile.VideoPlayerController;
import vars.annotation.ui.videofile.VideoPlayerDialogUI;
import vars.annotation.ui.videofile.quicktime.QTOpenVideoArchiveDialog;
import vars.annotation.ui.eventbus.VideoArchiveSelectedEvent;

/**
 * <p>Indicates which {@link VideoArchive} the annotator is editing. Clicking on the
 * label opens a dialog allowing the user to change the {@link VideoArchive} being
 * edited.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public class StatusLabelForVideoArchive extends StatusLabel {

    private final VideoPlayerDialogUI dialog;

    /**
     * Constructor for the StatusLabelForVideoArchive object
     *
     * @param toolBelt Friendly god object
     */
    public StatusLabelForVideoArchive(ToolBelt toolBelt) {
        super();
        Frame frame = (Frame) Lookup.getApplicationFrameDispatcher().getValueObject();
        final Dispatcher videoArchiveDispatcher = Lookup.getVideoArchiveDispatcher();


        dialog = new QTOpenVideoArchiveDialog(frame, toolBelt);
        dialog.onOkay((Void) -> {
            dialog.setVisible(false);
            //VideoArchive videoArchive = dialog.openVideoArchive();
            Tuple2<VideoArchive, VideoPlayerController> t = dialog.openVideoArchive();
            VideoArchive videoArchive = t.getA();
            VideoPlayerController videoPlayerController = t.getB();
            VideoArchiveSelectedEvent event = new VideoArchiveSelectedEvent(this, videoArchive);
            Lookup.getImageCaptureServiceDispatcher().setValueObject(videoPlayerController.getImageCaptureService());
            Lookup.getVideoControlServiceDispatcher().setValueObject(videoPlayerController.getVideoControlService());
            EventBus.publish(event);
        });

        AnnotationProcessor.process(this); // Register with EventBus

        update((VideoArchive) videoArchiveDispatcher.getValueObject());

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

                int x = mousePosition.x;
                if (x < 1) {
                    x = 1;
                }
                int y = mousePosition.y - dialog.getHeight();
                if (y < 1) {
                    y = 1;
                }


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
     * EventBus Listener method
     * @param event
     */
    @EventSubscriber(eventClass = VideoArchiveChangedEvent.class)
    public void respondTo(VideoArchiveChangedEvent event) {
        update(event.get());
    }

    @EventSubscriber(eventClass = VideoArchiveSelectedEvent.class)
    public void respondTo(VideoArchiveSelectedEvent event) {
        update(event.get());
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
