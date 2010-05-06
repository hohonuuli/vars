/*
 * @(#)UDPConnectionPanel.java   2010.05.06 at 02:24:41 PDT
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

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.TitledBorder;

/**
 *
 *
 * @version        Enter version here..., 2010.05.06 at 02:24:41 PDT
 * @author         Brian Schlining [brian@mbari.org]
 */
public class UDPConnectionPanel extends JPanel implements ConnectionParameters {

    private JTextField hostTextField;
    private JLabel lblHost;
    private JLabel lblPort;
    private JTextField portTextField;

    /**
     * Constructs ...
     */
    public UDPConnectionPanel() {
        initialize();
    }

    /**
     * @return
     */
    public Object[] getConnectionParameters() {
        return new Object[] { getHostTextField().getText(), Integer.valueOf(getPortTextField().getText()),
                              Double.valueOf(29.97) };
    }

    /**
     * @return
     */
    public JTextField getHostTextField() {
        if (hostTextField == null) {
            hostTextField = new JTextField();
            hostTextField.setColumns(10);
        }

        return hostTextField;
    }

    private JLabel getLblHost() {
        if (lblHost == null) {
            lblHost = new JLabel("Host: ");
        }

        return lblHost;
    }

    private JLabel getLblPort() {
        if (lblPort == null) {
            lblPort = new JLabel("Port: ");
        }

        return lblPort;
    }

    /**
     * @return
     */
    public JTextField getPortTextField() {
        if (portTextField == null) {
            portTextField = new JTextField();
            portTextField.setColumns(10);
        }

        return portTextField;
    }

    private void initialize() {
            setBorder(new TitledBorder(null, "UDP", TitledBorder.LEADING, TitledBorder.TOP, null, null));

            GroupLayout groupLayout = new GroupLayout(this);
            groupLayout.setHorizontalGroup(
                    groupLayout.createParallelGroup(Alignment.LEADING)
                            .addGroup(groupLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                            .addComponent(getLblHost())
                                            .addComponent(getLblPort()))
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                            .addComponent(getPortTextField(), GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE)
                                            .addComponent(getHostTextField(), GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE))
                                    .addContainerGap())
            );
            groupLayout.setVerticalGroup(
                    groupLayout.createParallelGroup(Alignment.LEADING)
                            .addGroup(groupLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                            .addComponent(getLblHost())
                                            .addComponent(getHostTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                            .addComponent(getLblPort())
                                            .addComponent(getPortTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addContainerGap(248, Short.MAX_VALUE))
            );
            setLayout(groupLayout);
    }

    /*
     * Sets the connection parameters in the text fields
     */

    /**
     *
     * @param hostname
     * @param portnumber
     */
    public void setConnectionParameters(String hostname, String portnumber) {
        getHostTextField().setText(hostname);
        getPortTextField().setText(portnumber);
    }

    /**
     *
     * @param enabled
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        getHostTextField().setEnabled(enabled);
        getPortTextField().setEnabled(enabled);
    }
}
