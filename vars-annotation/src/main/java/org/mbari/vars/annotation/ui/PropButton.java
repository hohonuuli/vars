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
Created on Apr 30, 2004
 *
TODO To change the template for this generated file go to Window -
Preferences - Java - Code Generation - Code and Comments
 */
package org.mbari.vars.annotation.ui;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.mbari.swing.JFancyButton;
import org.mbari.vars.annotation.ui.dispatchers.ObservationTableDispatcher;
import org.mbari.vars.annotation.ui.dispatchers.PersonDispatcher;
import org.mbari.vars.annotation.ui.table.ObservationTable;

/**
 * <p>This button toggles its enabled state based on if Person is logged in
 * and if the OBservationTable has selected rows. Person  != null and
 * ObservationTable has rows selected = enabled; otherwise not enabled.</p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 * @version $Id: PropButton.java 314 2006-07-10 02:38:46Z hohonuuli $
 */
public class PropButton extends JFancyButton {

    /**
     *
     */
    private static final long serialVersionUID = -7442195802758439190L;

    /**
     * Constructs ...
     *
     */
    public PropButton() {
        super();

        // Enable the button if a user is logged in and one ro more rows are
        // selected in the table.
        ObservationTableDispatcher.getInstance().getObservationTable().getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {

            public void valueChanged(final ListSelectionEvent e) {
                final String p = PersonDispatcher.getInstance().getPerson();
                if (p != null) {
                    final ObservationTable t = ObservationTableDispatcher.getInstance().getObservationTable();
                    if (t.getSelectedRows().length > 0) {
                        setEnabled(true);
                    }
                    else {
                        setEnabled(false);
                    }
                }
                else {
                    setEnabled(false);
                }
            }

        });
    }
}
