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

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * <p>Demostrates the ConnectionDialog</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: ConnectionDialogDemo.java 332 2006-08-01 18:38:46Z hohonuuli $
 * @see ConnectionDialog
 */
public class ConnectionDialogDemo extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = -565727729634621398L;

    /**
     *     @uml.property  name="dialog"
     *     @uml.associationEnd  multiplicity="(1 1)"
     */
    private final ConnectionDialog dialog = new ConnectionDialog();

    /**
     * @exception  HeadlessException Description of the Exception
     */
    public ConnectionDialogDemo() throws HeadlessException {
        super("ConnectionDialogDemo");
    }

    /**
     *  Gets the dialog attribute of the ConnectionDialogDemo object
     *
     * @return  The dialog value
     */
    public JDialog getDialog() {
        return dialog;
    }

    /**
     *  The main program for the ConnectionDialogDemo class
     *
     * @param  args The command line arguments
     */
    public static void main(final String[] args) {
        final ConnectionDialogDemo f = new ConnectionDialogDemo();
        f.setSize(100, 100);
        final JButton b = new JButton("Push me");
        b.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                final JDialog d = f.getDialog();
                d.setVisible(true);
            }
        });
        f.getContentPane().add(b);
        f.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        f.pack();
        f.setVisible(true);
    }
}
