/*
 * @(#)VideoControlServiceDialog.java   2010.01.12 at 10:34:08 PST
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



package vars.annotation.ui.video;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import vars.annotation.ui.Lookup;
import vars.shared.ui.dialogs.StandardDialog;

/**
 *
 *
 * @version        Enter version here..., 2009.12.09 at 10:16:52 PST
 * @author         Brian Schlining [brian@mbari.org]
 */
public class VideoControlServiceDialog extends StandardDialog {

    private JPanel aPanel;
    private JComboBox comboBox;
    private JLabel lblSelectTimecodeSource;
    private JPanel panel;
    private JPanel rs422Panel;
    private JPanel udpPanel;

    private enum Sources { RS422, UDP; }

    /**
     * Constructs ...
     *
     * @param parent
     */
    public VideoControlServiceDialog(Frame parent) {
        super(parent);
        initialize();
        pack();
    }

    private JPanel getAPanel() {
        if (aPanel == null) {
            aPanel = new JPanel();
            aPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            aPanel.setLayout(new BoxLayout(aPanel, BoxLayout.X_AXIS));
            aPanel.add(getLblSelectTimecodeSource());
            aPanel.add(getComboBox());
        }

        return aPanel;
    }

    /**
     * @return
     */
    public JComboBox getComboBox() {
        if (comboBox == null) {
            comboBox = new JComboBox();

            /*
             * FIXME: I commented out the RS422 source for now. When I've
             * fixed the native deployment of RXTX (refer to JNA's
             * Native and Platform classes
             */
            for (Sources source : Sources.values()) {
                comboBox.addItem(source);
            }
            //comboBox.addItem(Sources.UDP);


            comboBox.addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    Sources source = (Sources) e.getItem();
                    switch (source) {
                    case RS422:
                        getUdpPanel().setEnabled(false);
                        getRs422Panel().setEnabled(true);
                        break;

                    default:
                        getUdpPanel().setEnabled(true);
                        getRs422Panel().setEnabled(false);
                        break;
                    }

                }
            });

            comboBox.setSelectedItem(Sources.RS422);
            getUdpPanel().setEnabled(false);
            getRs422Panel().setEnabled(true);


        }

        return comboBox;
    }

    private JLabel getLblSelectTimecodeSource() {
        if (lblSelectTimecodeSource == null) {
            lblSelectTimecodeSource = new JLabel("Select Timecode Source: ");
        }

        return lblSelectTimecodeSource;
    }

    private JPanel getPanel() {
        if (panel == null) {
            panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.add(getAPanel());
            panel.add(getRs422Panel());
            panel.add(getUdpPanel());
        }

        return panel;
    }

    private JPanel getRs422Panel() {
        if (rs422Panel == null) {
            rs422Panel = new RS422ConnectionPanel();
        }

        return rs422Panel;
    }

    private JPanel getUdpPanel() {
        if (udpPanel == null) {
            udpPanel = new UDPConnectionPanel();
        }

        return udpPanel;
    }

    private void initialize() {
        setPreferredSize(new Dimension(450, 302));
        getContentPane().add(getPanel(), BorderLayout.CENTER);
        getCancelButton().addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                dispose();
            }

        });

        getOkayButton().addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Sources source = (Sources) getComboBox().getSelectedItem();
                VideoControlService videoControlService = null;
                ConnectionParameters connectionParameters = null;
                Object[] args = null;
                switch (source) {
                case RS422:
                    videoControlService = new RSS422VideoControlService();
                    connectionParameters = (ConnectionParameters) getRs422Panel();
                    break;

                default:
                    videoControlService = new UDPVideoControlService();
                    connectionParameters = (ConnectionParameters) getUdpPanel();
                    break;
                }

                dispose();

                videoControlService.connect(connectionParameters.getConnectionParameters());
                Lookup.getVideoControlServiceDispatcher().setValueObject(videoControlService);

            }

        });

        //pack();
    }
}
