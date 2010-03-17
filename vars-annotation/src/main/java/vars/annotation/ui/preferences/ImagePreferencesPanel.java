/*
 * @(#)ImagePreferencesPanel.java   2010.03.17 at 11:17:31 PDT
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



package vars.annotation.ui.preferences;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.UserAccount;
import vars.annotation.ui.Lookup;
import vars.shared.preferences.PreferenceUpdater;

/**
 *
 *
 * @version        Enter version here..., 2010.03.17 at 11:17:31 PDT
 * @author         Brian Schlining [brian@mbari.org]
 */
public class ImagePreferencesPanel extends JPanel implements PreferenceUpdater {

    private final Logger log = LoggerFactory.getLogger(ImagePreferencesPanel.class);
    private JButton browseButton;
    private final ImagePreferencesPanelController controller;
    private JComboBox imageFormatComboBox;
    private JLabel imageFormatLabel;
    private JTextField imageTargetMappingTextField;
    private JTextField imageTargetTextField;
    private JLabel imageWebAccessLabel;
    private JLabel saveImagesToLabel;
    private UserAccount userAccount;

    /**
     * Create the panel
     */
    public ImagePreferencesPanel(PreferencesFactory preferencesFactory) {
        super();
        this.controller = new ImagePreferencesPanelController(this, preferencesFactory);

        try {
            initialize();
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * @return
     */
    protected JButton getBrowseButton() {
        if (browseButton == null) {
            browseButton = new JButton();
            browseButton.setText("Browse");
            browseButton.addActionListener(new BrowseAction());
        }

        return browseButton;
    }

    protected ImagePreferencesPanelController getController() {
        return controller;
    }

    /**
     * @return
     */
    protected JComboBox getImageFormatComboBox() {
        if (imageFormatComboBox == null) {
            imageFormatComboBox = new JComboBox();
            imageFormatComboBox.setEnabled(false);
        }

        return imageFormatComboBox;
    }

    /**
     * @return
     */
    protected JLabel getImageFormatLabel() {
        if (imageFormatLabel == null) {
            imageFormatLabel = new JLabel();
            imageFormatLabel.setText("Image Format:");
        }

        return imageFormatLabel;
    }

    /**
     * @return
     */
    protected JTextField getImageTargetMappingTextField() {
        if (imageTargetMappingTextField == null) {
            imageTargetMappingTextField = new JTextField();
        }

        return imageTargetMappingTextField;
    }

    /**
     * @return
     */
    protected JTextField getImageTargetTextField() {
        if (imageTargetTextField == null) {
            imageTargetTextField = new JTextField();
        }

        return imageTargetTextField;
    }

    /**
     * @return
     */
    protected JLabel getImageWebAccessLabel() {
        if (imageWebAccessLabel == null) {
            imageWebAccessLabel = new JLabel();
            imageWebAccessLabel.setText("Image Web Access:");
        }

        return imageWebAccessLabel;
    }

    /**
     * @return
     */
    protected JLabel getSaveImagesToLabel() {
        if (saveImagesToLabel == null) {
            saveImagesToLabel = new JLabel();
            saveImagesToLabel.setText("Save Images to:");
        }

        return saveImagesToLabel;
    }

    /**
     * @return
     */
    public UserAccount getUserAccount() {
        return userAccount;
    }

    private void initialize() throws Exception {
            final GroupLayout groupLayout = new GroupLayout((JComponent) this);
            groupLayout.setHorizontalGroup(
                    groupLayout.createParallelGroup(GroupLayout.LEADING)
                            .add(groupLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .add(groupLayout.createParallelGroup(GroupLayout.LEADING)
                                            .add(getImageWebAccessLabel())
                                            .add(getSaveImagesToLabel())
                                            .add(getImageFormatLabel()))
                                    .addPreferredGap(LayoutStyle.RELATED)
                                    .add(groupLayout.createParallelGroup(GroupLayout.LEADING)
                                            .add(groupLayout.createSequentialGroup()
                                                    .add(getImageTargetTextField(), GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                                                    .addPreferredGap(LayoutStyle.RELATED)
                                                    .add(getBrowseButton()))
                                            .add(getImageTargetMappingTextField(), GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
                                            .add(getImageFormatComboBox(), 0, 347, Short.MAX_VALUE))
                                    .addContainerGap())
            );
            groupLayout.setVerticalGroup(
                    groupLayout.createParallelGroup(GroupLayout.LEADING)
                            .add(groupLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .add(groupLayout.createParallelGroup(GroupLayout.BASELINE)
                                            .add(getSaveImagesToLabel())
                                            .add(getBrowseButton())
                                            .add(getImageTargetTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.RELATED)
                                    .add(groupLayout.createParallelGroup(GroupLayout.BASELINE)
                                            .add(getImageWebAccessLabel())
                                            .add(getImageTargetMappingTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.RELATED)
                                    .add(groupLayout.createParallelGroup(GroupLayout.BASELINE)
                                            .add(getImageFormatLabel())
                                            .add(getImageFormatComboBox(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addContainerGap(292, Short.MAX_VALUE))
            );
            setLayout(groupLayout);
    }

    /**
     */
    public void persistPreferences() {
        controller.persistPreferences();
    }

    /**
     *
     * @param userAccount
     */
    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
        // Pass it on to the controller to do the updating of UI fields
        controller.setUserAccount(userAccount);
    }

    /**
     */
    public void updatePreferences() {
        controller.persistPreferences();
    }

    /**
     * Action for browsing for a directory to save images into.
     */
    private class BrowseAction implements ActionListener {

        final JFileChooser chooser;

        /**
         * Constructs ...
         */
        public BrowseAction() {
            chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }

        /**
         *
         * @param e
         */
        public void actionPerformed(ActionEvent e) {
            int response = chooser.showOpenDialog(ImagePreferencesPanel.this);
            if (response == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                getImageTargetTextField().setText(file.getAbsolutePath());
            }
        }
    }
}
