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


package org.mbari.vars.annotation.ui.actions;

import javax.swing.JDialog;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.vars.annotation.ui.dialogs.AddSamplePropDialog;

/**
 * <p>Displays a dialog that prompts the user to input parameters needed to
 * set the samples properties.</p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 * @version $Id: AddSamplePropWithDialogAction.java 314 2006-07-10 02:38:46Z hohonuuli $
 */
public class AddSamplePropWithDialogAction extends ActionAdapter {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final JDialog dialog = new AddSamplePropDialog();

    /**
     *
     */
    public AddSamplePropWithDialogAction() {
        super();
    }

    /**
     * <p><!-- Method description --></p>
     *
     */
    public void doAction() {
        dialog.setVisible(true);
    }
}
