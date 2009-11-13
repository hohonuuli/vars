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


/*
Created on Apr 7, 2004
 */
package org.mbari.vars.annotation.ui.actions;

import javax.swing.JDialog;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.vars.annotation.locale.OpenVideoArchiveSetUsingParamsDialogFactory;

/**
 * <p>Action that opens a <code>OpenVideoArchiveSetUsingParamsDialog</code></p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 * @version $Id: ShowOpenVideoArchiveDialogAction.java 314 2006-07-10 02:38:46Z hohonuuli $
 */
public class ShowOpenVideoArchiveDialogAction extends ActionAdapter {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final static JDialog dialog = OpenVideoArchiveSetUsingParamsDialogFactory.getDialog();

    /**
     * Constructor
     */
    public ShowOpenVideoArchiveDialogAction() {
        super("Open Archive");

        // TODO Auto-generated constructor stub
    }

    /**
     *  Intiates the action
     */
    public void doAction() {
        dialog.setVisible(true);
    }

    /**
     *  Gets the dialog.
     *
     * @return  An <code>OpenVideoArchiveSetUsingParamsDialog</code>
     */
    public JDialog getDialog() {
        return dialog;
    }
}
