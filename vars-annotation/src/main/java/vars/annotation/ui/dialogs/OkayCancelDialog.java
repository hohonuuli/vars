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


package vars.annotation.ui.dialogs;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.ButtonBarFactory;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.mbari.swing.JFancyButton;

/**
 * <p>Base class used for other dialogs in the vars appplications.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: OkayCancelDialog.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class OkayCancelDialog extends JDialog {

 
    private static final long serialVersionUID = 4684462712730934969L;

    private JButton cancelButton = null;

    private final CloseWindowListener closeWindowListener = new CloseWindowListener();

    private Container internalContentPane = null;

    private JButton okayButton = null;

    /**
     * Constructor for the OkayCancelDialog object
     *
     * @exception  HeadlessException Description of the Exception
     */
    public OkayCancelDialog() throws HeadlessException {
        this((Frame) null, false);
    }

    /**
     * Constructor for the OkayCancelDialog object
     *
     * @param  owner Description of the Parameter
     * @exception  HeadlessException Description of the Exception
     */
    public OkayCancelDialog(final Dialog owner) throws HeadlessException {
        this(owner, false);
    }

    /**
     * Constructor for the OkayCancelDialog object
     *
     * @param  owner Description of the Parameter
     * @exception  HeadlessException Description of the Exception
     */
    public OkayCancelDialog(final Frame owner) throws HeadlessException {
        this(owner, false);
    }

    /**
     * Constructor for the OkayCancelDialog object
     *
     * @param  owner Description of the Parameter
     * @param  modal Description of the Parameter
     * @exception  HeadlessException Description of the Exception
     */
    public OkayCancelDialog(final Dialog owner, final boolean modal) throws HeadlessException {
        this(owner, null, modal);
    }

    /**
     * Constructor for the OkayCancelDialog object
     *
     * @param  owner Description of the Parameter
     * @param  title Description of the Parameter
     * @exception  HeadlessException Description of the Exception
     */
    public OkayCancelDialog(final Dialog owner, final String title) throws HeadlessException {
        this(owner, title, false);
    }

    /**
     * Constructor for the OkayCancelDialog object
     *
     * @param  owner Description of the Parameter
     * @param  modal Description of the Parameter
     * @exception  HeadlessException Description of the Exception
     */
    public OkayCancelDialog(final Frame owner, final boolean modal) throws HeadlessException {
        this(owner, null, modal);
    }

    /**
     * Constructor for the OkayCancelDialog object
     *
     * @param  owner Description of the Parameter
     * @param  title Description of the Parameter
     * @exception  HeadlessException Description of the Exception
     */
    public OkayCancelDialog(final Frame owner, final String title) throws HeadlessException {
        this(owner, title, false);
    }

    /**
     * Constructor for the OkayCancelDialog object
     *
     * @param  owner Description of the Parameter
     * @param  title Description of the Parameter
     * @param  modal Description of the Parameter
     * @exception  HeadlessException Description of the Exception
     */
    public OkayCancelDialog(final Dialog owner, final String title, final boolean modal) throws HeadlessException {
        super(owner, title, modal);
        initialize();
    }

    /**
     * Constructor for the OkayCancelDialog object
     *
     * @param  owner Description of the Parameter
     * @param  title Description of the Parameter
     * @param  modal Description of the Parameter
     * @exception  HeadlessException Description of the Exception
     */
    public OkayCancelDialog(final Frame owner, final String title, final boolean modal) throws HeadlessException {
        super(owner, title, modal);
        initialize();
    }

    /**
     * Constructor for the OkayCancelDialog object
     *
     * @param  owner Description of the Parameter
     * @param  title Description of the Parameter
     * @param  modal Description of the Parameter
     * @param  gc Description of the Parameter
     * @exception  HeadlessException Description of the Exception
     */
    public OkayCancelDialog(final Dialog owner, final String title, final boolean modal, final GraphicsConfiguration gc)
            throws HeadlessException {
        super(owner, title, modal, gc);
        initialize();
    }

    /**
     * Constructor for the OkayCancelDialog object
     *
     * @param  owner Description of the Parameter
     * @param  title Description of the Parameter
     * @param  modal Description of the Parameter
     * @param  gc Description of the Parameter
     */
    public OkayCancelDialog(final Frame owner, final String title, final boolean modal,
                            final GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        initialize();
    }

    /**
     *     @return  the cancelButton
     *     @uml.property  name="cancelButton"
     */
    final JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JFancyButton("Cancel");
            cancelButton.setIcon(new ImageIcon(getClass().getResource("/images/vars/knowledgebase/delete2.png")));
            cancelButton.addActionListener(closeWindowListener);
        }

        return cancelButton;
    }

    /**
     *  Gets the contentPane attribute of the OkayCancelDialog object
     *
     * @return  The contentPane value
     */
//    @Override
//    public Container getContentPane() {
//        if (internalContentPane == null) {
//            internalContentPane = new JPanel();
//        }
//
//        return internalContentPane;
//    }

    /**
     *     @return  the okayButton
     */
    public JButton getOkayButton() {
        if (okayButton == null) {
            okayButton = new JFancyButton("Okay");
            okayButton.setIcon(new ImageIcon(getClass().getResource("/images/vars/knowledgebase/check2.png")));
            okayButton.addActionListener(closeWindowListener);

            // this property is being removed when the dialog is closed.
            // Then, if the same instantiation is shown again, the okaybutton
            // is no longer the default button of the root pane.
            // Options are to either override dialogs setvisible and add
            // a call to setDefaultButton there, or to add a listener here
            // to do the work.
            // I'm going with option two for now... achase 20040512
            getRootPane().setDefaultButton(okayButton);
            addHierarchyListener(new HierarchyListener() {

                public void hierarchyChanged(final HierarchyEvent e) {

                    // pardon me if this isn't the most efficient way to check
                    // for SHOWING_CHANGED. My bitwise math skills have never been
                    // very good. achase 20040512
                    if (HierarchyEvent.SHOWING_CHANGED == (HierarchyEvent.SHOWING_CHANGED & e.getChangeFlags())) {
                        OkayCancelDialog.this.getRootPane().setDefaultButton(okayButton);
                    }
                }

            });
        }

        return okayButton;
    }

    private final void initialize() {
       // final Container contentPane = super.getContentPane();
        setLayout(new BorderLayout());
        final JPanel buttonPanel = ButtonBarFactory.buildOKCancelBar(getCancelButton(), getOkayButton());
        buttonPanel.setBorder(Borders.DIALOG_BORDER);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(buttonPanel, BorderLayout.SOUTH);
        //add(getContentPane(), BorderLayout.CENTER);
    }

    /**
     * Close the dialog when the user presses the "cancel" button. Default
     * value is true.
     *
     * @param  closeWindowOnCancel
     */
    protected void setCloseDialogOnCancel(final boolean closeWindowOnCancel) {
        getCancelButton().removeActionListener(closeWindowListener);

        if (closeWindowOnCancel) {
            getCancelButton().addActionListener(closeWindowListener);
        }
    }

    /**
     * Close the dialog when the user presses the "okay" button. Default
     * value is true.
     *
     * @param  closeWindowOnOkay
     */
    protected void setCloseDialogOnOkay(final boolean closeWindowOnOkay) {
        getOkayButton().removeActionListener(closeWindowListener);

        if (closeWindowOnOkay) {
            getOkayButton().addActionListener(closeWindowListener);
        }
    }

    /**
     *  Sets the contentPane attribute of the OkayCancelDialog object
     *
     * @param  contentPane The new contentPane value
     */
//    @Override
//    public void setContentPane(final Container contentPane) {
//        super.getContentPane().remove(getContentPane());
//        super.getContentPane().add(contentPane, BorderLayout.CENTER);
//        internalContentPane = contentPane;
//    }

    /**
     *  The main program for the OkayCancelDialog class
     *
     * @param  args The command line arguments
     */
    public static void main(final String[] args) {
        final JFrame frame = new JFrame("Test Okay Cancel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final OkayCancelDialog dialog = new OkayCancelDialog();
        dialog.setBounds(600, 500, 350, 200);
        dialog.getContentPane().add(new JLabel("Hello world"));
        dialog.getContentPane().add(new JButton("Foo bar"));
        dialog.getContentPane().add(new JButton("fubar"));
        final JButton showDialog = new JButton("Show Dialog");
        showDialog.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                dialog.setVisible(true);
            }
        });
        frame.getContentPane().add(showDialog);
        frame.setBounds(500, 500, 50, 50);
        dialog.initialize();
        frame.setVisible(true);
    }

    private class CloseWindowListener implements ActionListener {

        /**
         *  Description of the Method
         *
         * @param  e Description of the Parameter
         */
        public void actionPerformed(final ActionEvent e) {
            OkayCancelDialog.this.dispose();
        }
    }
}
