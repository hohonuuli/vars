/*
 * @(#)StatusLabelForVcr.java   2010.05.06 at 02:46:24 PDT
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
import java.beans.PropertyChangeListener;
import javax.swing.SwingUtilities;
import org.mbari.swing.SwingUtils;
import org.mbari.util.Dispatcher;
import org.mbari.util.IObserver;
import org.mbari.vcr.IVCR;
import org.mbari.vcr.IVCRState;
import vars.UserAccount;
import vars.shared.ui.video.VideoControlService;
import vars.annotation.ui.video.VideoControlServiceDialog;
import vars.shared.preferences.PreferencesService;

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


        Frame frame = (Frame) Lookup.getApplicationFrameDispatcher().getValueObject();
        VideoControlServiceDialog videoDialog = new VideoControlServiceDialog(frame);

        /*
         * When the user clicks this label a dialog should pop up allowing them
         * to open the VCR.
         */
        addMouseListener(new MyMouseListener(videoDialog));

        /*
         * Need to do this in order have the label display the correct VCR if
         * one has already been put in the VcrDispatcher. Not that SUN's
         * propertyChangeSupport swallows notifications if the new obj and the
         * old obj are equal, so we have to set it to null and then back to
         * it's value to trigger a notification.
         */
        final Dispatcher dispatcher = Lookup.getVideoControlServiceDispatcher();
        final VideoControlService videoService = (VideoControlService) dispatcher.getValueObject();
        final IVCR vcr = (videoService == null) ? null : videoService;

        dispatcher.addPropertyChangeListener(new VcrListener());
        setVcr(vcr);

        Lookup.getUserAccountDispatcher().addPropertyChangeListener(new UserAccountListener(videoDialog));

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

    private class MyMouseListener extends MouseAdapter {

        private final VideoControlServiceDialog dialog;

        /**
         * Constructs ...
         *
         * @param dialog
         */
        public MyMouseListener(VideoControlServiceDialog dialog) {
            this.dialog = dialog;
        }

        /**
         *
         * @param me
         */
        @Override
        public void mouseClicked(final MouseEvent me) {
            SwingUtils.flashJComponent(StatusLabelForVcr.this, 2);

            final Point mousePosition = me.getPoint();

            SwingUtilities.convertPointToScreen(mousePosition, StatusLabelForVcr.this);

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


    /**
     * Class that listens for changing UserAccounts and updated the UDP
     * Panel fields accordingly. Also persists values when OK button is pressed.
     */
    private class UserAccountListener implements PropertyChangeListener {

        private final VideoControlServiceDialog dialog;
        private final PreferencesService preferencesService;

        /**
         * Constructs ...
         *
         * @param dialog
         */
        public UserAccountListener(VideoControlServiceDialog dialog) {
            this.dialog = dialog;
            preferencesService = new PreferencesService(Lookup.getPreferencesFactory());

            dialog.getOkayButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();
                    String host = UserAccountListener.this.dialog.getUdpPanel().getHostTextField().getText();
                    String port = UserAccountListener.this.dialog.getUdpPanel().getPortTextField().getText();
                    host = (host == null) ? preferencesService.getHostname() : host;
                    port = (port == null) ? "9000" : port;
                    preferencesService.persistVcrUrl(userAccount.getUserName(), preferencesService.getHostname(), host,
                                                     port);

                }

            });
        }

        /**
         *
         * @param evt
         */
        public void propertyChange(PropertyChangeEvent evt) {
            UserAccount userAccount = (UserAccount) evt.getNewValue();
            String port = null;
            String hostname = null;
            if (userAccount != null) {
                port = preferencesService.findVcrPort(userAccount.getUserName(), preferencesService.getHostname());
                hostname = preferencesService.findVcrHostname(userAccount.getUserName(),
                        preferencesService.getHostname());
                dialog.setUDPConnectionParameters(hostname, port);

                String lastVcr = preferencesService.findLastVideoConnectionId(preferencesService.getHostname());
                dialog.setLastConnectionParameters(lastVcr);

            }
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
            final VideoControlService newVideoService = (VideoControlService) evt.getNewValue();
            final VideoControlService oldVideoService = (VideoControlService) evt.getOldValue();
            final IVCR newVcr = (newVideoService == null) ? null : newVideoService;
            final IVCR oldVcr = (oldVideoService == null) ? null : oldVideoService;

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
