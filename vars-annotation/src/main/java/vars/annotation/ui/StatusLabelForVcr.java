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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.SwingUtilities;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.mbari.swing.SwingUtils;
import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoState;
import vars.UserAccount;
import vars.avplayer.VideoController;
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
//    private final StatusMonitor statusMonitor = new StatusMonitor();

    /**
     * Constructor
     */
    public StatusLabelForVcr() {
        super();
        setText(NO_CONNECTION);


        Frame frame = StateLookup.getAnnotationFrame();
        //VideoControlServiceDialog videoDialog = new VideoControlServiceDialog(frame);

        /*
         * When the user clicks this label a dialog should pop up allowing them
         * to open the VCR.
         */
        //addMouseListener(new MyMouseListener(videoDialog));

        /*
         * Need to do this in order have the label display the correct VCR if
         * one has already been put in the VcrDispatcher. Not that SUN's
         * propertyChangeSupport swallows notifications if the new obj and the
         * old obj are equal, so we have to set it to null and then back to
         * it's value to trigger a notification.
         */
        // TODO replace with VideoController code
        StateLookup.videoControllerProperty().addListener((obs, oldVal, newVal) -> {
            setVideoController(newVal);
        });

        //StateLookup.userAccountProperty().addListener(new UserAccountChangeListener(videoDialog));

    }

    /**
     *
     * @param evt
     */
    public void propertyChange(PropertyChangeEvent evt) {
        // Do nothing
    }

    private void setVideoController(VideoController<? extends VideoState, ? extends VideoError> videoController) {
        String label = NO_CONNECTION;
        if (videoController != null) {
            label = "VCR: " + videoController.getConnectionID();
        }
        setText(label);
    }


    private class MyMouseListener extends MouseAdapter {

//        private final VideoControlServiceDialog dialog;
//
//        /**
//         * Constructs ...
//         *
//         * @param dialog
//         */
//        public MyMouseListener(VideoControlServiceDialog dialog) {
//            this.dialog = dialog;
//        }
//
//        /**
//         *
//         * @param me
//         */
//        @Override
//        public void mouseClicked(final MouseEvent me) {
//            SwingUtils.flashJComponent(StatusLabelForVcr.this, 2);
//
//            final Point mousePosition = me.getPoint();
//
//            SwingUtilities.convertPointToScreen(mousePosition, StatusLabelForVcr.this);
//
//            int x = mousePosition.x;
//            if (x < 1) {
//                x = 1;
//            }
//
//            int y = mousePosition.y - dialog.getHeight();
//            if (y < 1) {
//                y = 1;
//            }
//
//            dialog.setLocation(x, y);
//            dialog.setVisible(true);
//        }
    }


    /**
     *  Monitors the VCR status. When the VCR is connected it toggles the
     * OK state of the label.
     */
//    private class StatusMonitor implements IObserver {
//
//        /**
//         * Method description
//         *
//         *
//         * @param obj
//         * @param changeCode
//         */
//        public void update(final Object obj, final Object changeCode) {
//            final IVCRState vcrState = (IVCRState) obj;
//
//            setOk(vcrState.isConnected());
//        }
//    }


    /**
     * Class that listens for changing UserAccounts and updated the UDP
     * Panel fields accordingly. Also persists values when OK button is pressed.
     */
//    private class UserAccountChangeListener implements ChangeListener<UserAccount> {
//
//        private final VideoControlServiceDialog dialog;
//        private final PreferencesService preferencesService = new PreferencesService(StateLookup.PREFERENCES_FACTORY);;
//
//        public UserAccountChangeListener(VideoControlServiceDialog dialog) {
//            this.dialog = dialog;
//
//            dialog.getOkayButton().addActionListener( e -> {
//                UserAccount userAccount = StateLookup.getUserAccount();
//                String host = UserAccountChangeListener.this.dialog.getUdpPanel().getHostTextField().getText();
//                String port = UserAccountChangeListener.this.dialog.getUdpPanel().getPortTextField().getText();
//                host = (host == null) ? preferencesService.getHostname() : host;
//                port = (port == null) ? "9000" : port;
//                preferencesService.persistVcrUrl(userAccount.getUserName(),
//                        preferencesService.getHostname(),
//                        host,
//                        port);
//            });
//        }
//
//        @Override
//        public void changed(ObservableValue<? extends UserAccount> observable, UserAccount oldValue, UserAccount userAccount) {
//            String port;
//            String hostname;
//            if (userAccount != null) {
//                port = preferencesService.findVcrPort(userAccount.getUserName(), preferencesService.getHostname());
//                hostname = preferencesService.findVcrHostname(userAccount.getUserName(),
//                        preferencesService.getHostname());
//                dialog.setUDPConnectionParameters(hostname, port);
//
//                String lastVcr = preferencesService.findLastVideoConnectionId(preferencesService.getHostname());
//                dialog.setLastConnectionParameters(lastVcr);
//
//            }
//        }
//    }


}
