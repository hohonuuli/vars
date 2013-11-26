/*
 * @(#)RS422ConnectionPanel.java   2009.12.09 at 11:36:37 PST
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

import gnu.io.CommPortIdentifier;
//import purejavacomm.CommPortIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.TitledBorder;

/**
 *
 *
 * @version        Enter version here..., 2009.12.09 at 11:36:37 PST
 * @author         Brian Schlining [brian@mbari.org]
 */
public class RS422ConnectionPanel extends JPanel implements ConnectionParameters {

    private JComboBox comboBox;
    private JLabel lblPort;
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Create the panel.
     */
    public RS422ConnectionPanel() {
        initialize();
    }

    protected JComboBox getComboBox() {
        if (comboBox == null) {
            comboBox = new JComboBox();
            // RXTX can only be initialize on the JDK/OS it was compiled for. Here we'll catch
            // problems when it can't be loaded so that VARS will run gracefully degraded.
            try {
                RS422VideoControlService videoControlService = new RS422VideoControlService();
                Set<CommPortIdentifier> cpis = videoControlService.findAvailableCommPorts();
                for (CommPortIdentifier commPortIdentifier : cpis) {
                    comboBox.addItem(commPortIdentifier.getName());
                }
            }
            catch (Error e) {
                log.error("Unable to initialize Serial port communications.", e);
                comboBox.setEnabled(false);
            }
        }

        return comboBox;
    }

    /**
     * @return
     */
    public Object[] getConnectionParameters() {
        return new Object[] { comboBox.getSelectedItem(), Double.valueOf(29.97) };
    }

    private JLabel getLblPort() {
        if (lblPort == null) {
            lblPort = new JLabel("Port: ");
        }

        return lblPort;
    }

    private void initialize() {
        setBorder(new TitledBorder(null, "RS422", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(getLblPort())
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(getComboBox(), 0, 404, Short.MAX_VALUE)
                        .addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                .addComponent(getLblPort())
                                .addComponent(getComboBox(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(280, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        getComboBox().setEnabled(enabled);
    }


}
