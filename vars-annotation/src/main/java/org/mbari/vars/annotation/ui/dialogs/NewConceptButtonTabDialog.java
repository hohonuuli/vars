/*
 * Copyright 2005 MBARI
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1
 * (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.mbari.vars.annotation.ui.dialogs;

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

    /**
     *
     */
    private static final long serialVersionUID = 2882944076955928089L;

    // The button to canel

    /**
     *     @uml.property  name="cancelButton"
     *     @uml.associationEnd  multiplicity="(1 1)"
     */
    JButton cancelButton = new JButton();

    // The button to create a new tab

    /**
     *     @uml.property  name="createButton"
     *     @uml.associationEnd  multiplicity="(1 1)"
     */
    JButton createButton = new JButton();

    /**
     *     @uml.property  name="gridBagLayout"
     */
    GridBagLayout gridBagLayout = new GridBagLayout();

    // A panel for the buttons

    /**
     *     @uml.property  name="newCancelPanel"
     *     @uml.associationEnd  multiplicity="(1 1)"
     */
    JPanel newCancelPanel = new JPanel();

    // JPanel for dialog box

    /**
     *     @uml.property  name="newTabPanel"
     *     @uml.associationEnd  multiplicity="(1 1)"
     */
    JPanel newTabPanel = new JPanel();

    /**
     *     @uml.property  name="returnValue"
     */
    String returnValue = null;

    /**
     *     @uml.property  name="tabName"
     *     @uml.associationEnd  multiplicity="(1 1)"
     */
    JTextField tabName = new JTextField();

    // The text field to name the preferences

    /**
     *     @uml.property  name="tabNameLabel"
     *     @uml.associationEnd  multiplicity="(1 1)"
     */
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

    /**
     * cancel this dialog
     *
     * @param  e Description of the Parameter
     */
    private void cancelAction(final ActionEvent e) {

        // close the popup window
        dispose();
    }

    /**
     *     Gets the returnValue attribute of the NewConceptButtonTabDialog object
     *     @return   The returnValue value
     *     @uml.property  name="returnValue"
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
