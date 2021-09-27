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


//$Header: /cvsroot/vars/vars/src/main/java/org/mbari/vars/annotation/ui/table/TableCellRenderer4AssociationList.java,v 1.1 2005/10/27 16:20:10 hohonuuli Exp $
package vars.annotation.ui.table;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListModel;
import javax.swing.ToolTipManager;
import mbarix4j.swing.ListListModel;
import mbarix4j.swing.table.ListTableCellRenderer;
import vars.LinkComparator;
import vars.annotation.Association;

/**
 * <p>Renderer used to display a list of Associations in the GUI. Requires
 * that the table be an instance of JTableEx. This class sets the row height of
 * the table row, then passes the rendering duties on to AssociationListCellRenderer</p>
 *
 * <h2><u>UML</u></h2>
 * <pre>
 *  [AssociationListCellRenderer]
 *          ^1
 *          |
 *          |
 *  [TableCellRenderer4AssociationList]---[JTableEx]
 * </pre>
 *
 * <h2><u>License</u></h2>
 * <p><font size="-1" color="#336699"><a href="http://www.mbari.org">
 * The Monterey Bay Aquarium Research Institute (MBARI)</a> provides this
 * documentation and code &quot;as is&quot;, with no warranty, express or
 * implied, of its quality or consistency. It is provided without support and
 * without obligation on the part of MBARI to assist in its use, correction,
 * modification, or enhancement. This information should not be published or
 * distributed to third parties without specific written permission from
 * MBARI.</font></p>
 *
 * <p><font size="-1" color="#336699">Copyright 2003 MBARI.
 * MBARI Proprietary Information. All rights reserved.</font></p>
 *
 * @author  <a href="mailto:brian@mbari.org">Brian Schlining</a>
 * @version  $Id: TableCellRenderer4AssociationList.java 314 2006-07-10 02:38:46Z hohonuuli $
 * @stereotype  role
 */
public class TableCellRenderer4AssociationList extends ListTableCellRenderer {

    /**
     *
     */
    private static final long serialVersionUID = -4188934869288929845L;

    /**
     * @directed
     */

    /*
     *  # AssociationListCellRenderer lnkAssociationListCellRenderer;
     */

    /**
     * Constructor for the TableCellRenderer4AssociationList object
     */
    public TableCellRenderer4AssociationList() {
        super();
        setPrototypeCellValue("0123456789012345678901234567890");

        // Create a custom List Cell Renderer that calls remoteToString
        // instead of defaulting to toString for Associations
        final AssociationListCellRenderer assocListCellRenderer = new AssociationListCellRenderer();
        assocListCellRenderer.setOpaque(false);
        this.setCellRenderer(assocListCellRenderer);
        ToolTipManager.sharedInstance().registerComponent(this);
    }

    // implements javax.swing.table.TableCellRenderer

    /**
     * Returns the table cell renderer for AssociationLists. Requires that the
     * JTable be an instance of JTableEx so that th row height for an individual row can be changed.
     *
     * @param  table the <code>JTable</code>
     * @param  value the value to assign to the cell at <code>[row, column]</code> An AssociationList
     * @param  isSelected true if cell is selected
     * @param  row the row of the cell to render
     * @param  column the column of the cell to render
     * @param  hasFocus Description of the Parameter
     * @return  the default table cell renderer
     */
    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
            final boolean hasFocus, final int row, final int column) {
        final Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                                        column);

        // Turn of row height adjustment while the cell is being edited.
        final ListModel listModel = getModel();

        // Calculate the row height
        final int fixedCellHeight = getFixedCellHeight();
        int numItemsInList = listModel.getSize();
        if (numItemsInList == 0) {
            numItemsInList = 1;
        }

        final int cellHeight = fixedCellHeight * numItemsInList;
        final Dimension preferredSize = component.getPreferredSize();
        if (cellHeight != table.getRowHeight(row)) {
            preferredSize.setSize(preferredSize.getWidth(), cellHeight);

            // component.setSize(preferredSize);
            table.setRowHeight(row, cellHeight);

            // System.out.println("Resizing row " + row + " height to " + cellHeight);
        }

        return component;
    }

    /**
     * Sets the string for the cell being rendered to <code>value</code>.
     * Overrides the super class to avoid Problems with shared renderers.
     *
     * @param  value the <code>List</code> for this cell; if value is <code>null</code> it sets the value to an empty string
     * @see  JLabel#setText
     */
    @Override
    protected void setValue(final Object value) {
        // Sort them in the view
        final List<Association> associations = (List<Association>) value;
        Collections.sort(associations, new LinkComparator());
        final ListModel listModel = new ListListModel(associations);
        this.setModel(listModel);
    }
}
