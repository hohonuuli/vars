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
import java.util.Set;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.TitledBorder;
import org.mbari.comm.CommUtil;

/**
 *
 *
 * @version        Enter version here..., 2009.12.09 at 11:36:37 PST
 * @author         Brian Schlining [brian@mbari.org]
 */
public class RS422ConnectionPanel extends JPanel implements ConnectionParameters {

    private JComboBox comboBox;
    private JLabel lblPort;

    /**
     * Create the panel.
     */
    public RS422ConnectionPanel() {
        initialize();
    }

    private JComboBox getComboBox() {
        if (comboBox == null) {
            comboBox = new JComboBox();
            RSS422VideoControlService videoControlService = new RSS422VideoControlService();
            Set<CommPortIdentifier> cpis = videoControlService.findAvailableCommPorts();
            for (CommPortIdentifier commPortIdentifier : cpis) {
                comboBox.addItem(commPortIdentifier.getName());
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
