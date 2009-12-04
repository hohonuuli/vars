/*
 * @(#)StatusLabelForVcr.java   2009.12.03 at 03:45:46 PST
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
import org.mbari.util.IObserver;
import org.mbari.vcr.IVCR;
import org.mbari.vcr.IVCRState;
import org.mbari.vcr.ui.VCRSelectionDialog;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.VideoService;

/**
 * <p>Indicates connection state of the VCR. Clicking on this label will bring up
 * a dialog allowing the user to connect to the VCR.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: StatusLabelForVcr.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class StatusLabelForVcr extends StatusLabel {

    private static final String NO_CONNECTION = "VCR: Not connected";
    private final StatusMonitor statusMonitor = new StatusMonitor();

    /**
     * Constructor
     */
    public StatusLabelForVcr() {
        super();
        setText(NO_CONNECTION);


        /*
         * When the user clicks this label a dialog should pop up allowing them
         * to open the VCR.
         */
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(final MouseEvent me) {
                SwingUtils.flashJComponent(StatusLabelForVcr.this, 2);

                final Point mousePosition = me.getPoint();

                SwingUtilities.convertPointToScreen(mousePosition, StatusLabelForVcr.this);

                final int x = mousePosition.x;
                final int y = mousePosition.y - videoDialog.getHeight();

                videoDialog.setLocation(x, y);
                videoDialog.setVisible(true);
            }

            Frame frame = (Frame) Lookup.getApplicationFrameDispatcher().getValueObject();
            private final JDialog videoDialog = new VCRSelectionDialog(frame);

        });

        /*
         * Need to do this in order have the label display the correct VCR if
         * one has already been put in the VcrDispatcher. Not that SUN's
         * propertyChangeSupport swallows notifications if the new obj and the
         * old obj are equal, so we have to set it to null and then back to
         * it's value to trigger a notification.
         */
        final Dispatcher dispatcher = Lookup.getVideoServiceDispatcher();
        final VideoService videoService = (VideoService) dispatcher.getValueObject();
        final IVCR vcr = (videoService == null) ? null : videoService.getVCR();

        dispatcher.addPropertyChangeListener(new VcrListener());
        setVcr(vcr);

    }

    /**
     *
     * @param evt
     */
    public void propertyChange(PropertyChangeEvent evt) {

        // TODO Auto-generated method stub

    }

    private void setVcr(final IVCR vcr) {
        String label = NO_CONNECTION;

        if (vcr != null) {
            label = "VCR: " + vcr.getConnectionName();
            setOk(vcr.getVcrState().isConnected());
            vcr.getVcrState().addObserver(statusMonitor);
        }

        setText(label);
    }

    /**
     *  Monitors the VCR status. When the VCR is connected it toggles the
     * OK state of the label.
     */
    private class StatusMonitor implements IObserver {

        /**
         * Method description
         *
         *
         * @param obj
         * @param changeCode
         */
        public void update(final Object obj, final Object changeCode) {
            final IVCRState vcrState = (IVCRState) obj;

            setOk(vcrState.isConnected());
        }
    }


    private class VcrListener implements PropertyChangeListener {

        /**
         * Method description
         *
         *
         * @param evt
         */
        public void propertyChange(final PropertyChangeEvent evt) {
            final VideoService newVideoService = (VideoService) evt.getNewValue();
            final VideoService oldVideoService = (VideoService) evt.getOldValue();
            final IVCR newVcr = (newVideoService == null) ? null : newVideoService.getVCR();
            final IVCR oldVcr = (oldVideoService == null) ? null : oldVideoService.getVCR();

            if (log.isDebugEnabled()) {
                final String label = (newVcr == null) ? NO_CONNECTION : "VCR: " + newVcr.getConnectionName();

                log.debug("Updating label: OLD = " + getText() + ", NEW = " + label);
            }

            if (oldVcr != null) {
                oldVcr.getVcrState().removeObserver(statusMonitor);
            }

            setVcr(newVcr);
        }
    }
}
