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
Created on Oct 22, 2003
 */
package vars.annotation.ui.table;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.swingx.table.TableColumnExt;
import vars.annotation.Observation;

/**
 * <p>Class used by ObservationTable model to render columns.</p> <h2><u>License</u></h2> <p><font size="-1" color="#336699"><a href="http://www.mbari.org"> The Monterey Bay Aquarium Research Institute (MBARI)</a> provides this documentation and code &quot;as is&quot;, with no warranty, express or implied, of its quality or consistency. It is provided without support and without obligation on the part of MBARI to assist in its use, correction, modification, or enhancement. This information should not be published or distributed to third parties without specific written permission from MBARI.</font></p> <p><font size="-1" color="#336699">Copyright 2004 MBARI. MBARI Proprietary Information. All rights reserved.</font></p>
 * @author   <a href="http://www.mbari.org">MBARI</a>
 * @version   $Id: ValueColumn.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public abstract class ValueColumn extends TableColumnExt {


    /**
     * Constructor for the ValueColumn object
     */
    public ValueColumn(String id) {
        super();
        setIdentifier(id);
    }

    /**
     * @param  modelIndex
     */
    public ValueColumn(String id, final int modelIndex) {
        super(modelIndex);
        setIdentifier(id);
    }

    /**
     * @param  modelIndex
     * @param  width
     */
    public ValueColumn(String id, final int modelIndex, final int width) {
        super(modelIndex, width);
        setIdentifier(id);
    }

    /**
     * @param  modelIndex
     * @param  width
     * @param  cellRenderer
     * @param  cellEditor
     */
    public ValueColumn(String id, final int modelIndex, final int width, final TableCellRenderer cellRenderer,
                       final TableCellEditor cellEditor) {
        super(modelIndex, width, cellRenderer, cellEditor);
        setIdentifier(id);
    }

    /**
     *     Gets the columnClass attribute of the ValueColumn object
     *     @return   The columnClass value
     *     @uml.property  name="columnClass"
     */
    public abstract Class getColumnClass();

    /**
     *  Gets the value attribute of the ValueColumn object
     *
     * @param  observation Description of the Parameter
     * @return  The value value
     */
    public abstract Object getValue(Observation observation);

    /**
     *  Gets the cellEditable attribute of the ValueColumn object
     *
     * @return  The cellEditable value
     */
    public boolean isCellEditable() {
        return false;
    }

    /**
     *  Gets the sortable attribute of the ValueColumn object
     *
     * @return  The sortable value
     */
    public boolean isSortable() {
        return false;
    }

    /**
     *  Sets the value attribute of the ValueColumn object
     *
     * @param  observation The new value value
     * @param  value The new value value
     */
    public void setValue(final Observation observation, final Object value) {

        // DO Nothing
    }
}
