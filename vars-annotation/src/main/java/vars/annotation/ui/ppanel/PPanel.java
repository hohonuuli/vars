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
Created on May 18, 2004
 */
package vars.annotation.ui.ppanel;

import java.util.Collection;
import javax.swing.JButton;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.swing.PropertyPanel;
import vars.annotation.Observation;
import vars.annotation.ui.StateLookup;

/**
 * <p>A panel that displays name value pairs. It also toggles the state of
 * the edit button (if an edit action has been set). The button is turned on
 * if a rows is selected in the table. </p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 * @version $Id: PPanel.java 314 2006-07-10 02:38:46Z hohonuuli $
 */
public class PPanel extends PropertyPanel {

    /**
     *
     */
    private static final long serialVersionUID = 106735865477960699L;

    /**
     *
     */
    public PPanel() {
        super();
    }

    /**
     * @param name
     * @param value
     */
    public PPanel(final Object name, final Object value) {
        super(name, value);
    }

    /**
     * @param action
     */
    @Override
    public void setEditAction(final ActionAdapter action) {
        super.setEditAction(action);
        final JButton btn = getEditButton();

        /*
         * Listen for the selected observations. If it's size is not 1 then
         * disable the panel
         */
        StateLookup.selectedObservationsProperty()
                .addListener((obs, oldVal, newVal) -> btn.setEnabled(newVal != null && newVal.size() == 1));


        /*
         * Need to check the state of the current observation in
         * order to properly enable a button on startup.
         */
        final Collection<Observation> obj = StateLookup.getSelectedObservations();
        btn.setEnabled(obj != null && obj.size() == 1);

    }
}
