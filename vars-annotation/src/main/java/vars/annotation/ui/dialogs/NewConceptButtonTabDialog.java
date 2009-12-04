/*
 * @(#)NewConceptButtonTabDialog.java   2009.12.03 at 03:53:04 PST
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



package vars.annotation.ui.dialogs;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * <p>This class creates a new tab on the concept button tab pane for user's to put
 * concept buttons on.</p>
 *
 * @author  : $Author: hohonuuli $
 * @version  : $Revision: 332 $
 */
public class NewConceptButtonTabDialog extends JDialog {

    JButton cancelButton = new JButton();

    /** The button to create a new tab */
    JButton createButton = new JButton();
    GridBagLayout gridBagLayout = new GridBagLayout();
    JPanel newCancelPanel = new JPanel();
    JPanel newTabPanel = new JPanel();
    String returnValue = null;
    JTextField tabName = new JTextField();
    JLabel tabNameLabel = new JLabel();

    /**
     *  dialog constructor -- show the login dialog
     *
     * @param  title title for this window
     */
    public NewConceptButtonTabDialog(final String title) {
        super();
        this.setModal(true);
        this.setTitle(title);

        try {
            jbInit();
            pack();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }

        this.setVisible(true);
    }

    private void cancelAction(final ActionEvent e) {

        // close the popup window
        dispose();
    }

    /**
     *     Gets the returnValue attribute of the NewConceptButtonTabDialog object
     *     @return   The returnValue value
     */
    public String getReturnValue() {
        return this.returnValue;
    }

    private void jbInit() throws Exception {
        tabNameLabel.setText("Name for New Tab:");
        tabName.setText("");
        tabName.setMinimumSize(new Dimension(150, 21));
        tabName.setPreferredSize(new Dimension(150, 21));
        tabName.setMaximumSize(new Dimension(150, 21));
        createButton.setText("Create New Tab");
        createButton.setFont(new java.awt.Font("Dialog", 1, 12));
        createButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                setTabName();
            }
        });
        cancelButton.setText("Cancel");
        cancelButton.setFont(new java.awt.Font("Dialog", 1, 12));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                cancelAction(e);
            }
        });
        newCancelPanel.setLayout(gridBagLayout);
        newCancelPanel.add(tabNameLabel);
        newCancelPanel.add(tabName);
        newCancelPanel.add(createButton);
        newCancelPanel.add(cancelButton);
        this.getContentPane().add(newCancelPanel, null);

        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final Dimension frameSize = this.getSize();

        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }

        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }

        this.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    }

    /**
     *  Sets the tabName attribute of the NewConceptButtonTabDialog object
     */
    public void setTabName() {
        try {
            if (tabName.getText().compareTo("") == 0) {
                JOptionPane.showMessageDialog(this, "Name of tab cannot be empty");
            }
            else {
                this.returnValue = tabName.getText();
                this.setVisible(false);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
