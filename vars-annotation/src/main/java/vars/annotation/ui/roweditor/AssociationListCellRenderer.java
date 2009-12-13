/*
 * @(#)AssociationListCellRenderer.java   2009.11.18 at 04:22:39 PST
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



package vars.annotation.ui.roweditor;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import vars.LinkUtilities;
import vars.annotation.Association;

/**
 * <p>Allows a list of <code>Associations</code> to be displayed in a
 * a single cell of a JTable</p>
 *
 * <h2><u>UML</u></h2>
 * <pre>
 *                                   1
 *      [AssociationListCellRenderer]<--[TableCellRenderer4AssociationList]
 * </pre>
 *
 */
public class AssociationListCellRenderer extends DefaultListCellRenderer implements ListCellRenderer {

    String assoString;

    /**
     * Constructor for the AssociationListCellRenderer object
     */
    public AssociationListCellRenderer() {
        super();
    }

    /**
     *  Gets the listCellRendererComponent attribute of the AssociationListCellRenderer object
     *
     * @param  list Description of the Parameter
     * @param  value Description of the Parameter
     * @param  index Description of the Parameter
     * @param  isSelected Description of the Parameter
     * @param  cellHasFocus Description of the Parameter
     * @return  The listCellRendererComponent value
     */
    @Override
    public Component getListCellRendererComponent(final JList list, final Object value, final int index,
            final boolean isSelected, final boolean cellHasFocus) {
        final Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof String) {
            this.setText((String) value);
            assoString = (String) value;
        }
        else {
            Association association = null;
            association = (Association) value;

            if (association != null) {
                assoString = LinkUtilities.formatAsLongString(association);
                this.setText(assoString);
            }
            else {
                assoString = "--No Association--";
                this.setText(assoString);
            }
        }

        return component;
    }

    /**
     * @return  The preferredSize value
     */
    @Override
    public Dimension getPreferredSize() {
        Dimension dim = super.getPreferredSize();
        if (dim != null) {
            dim = new Dimension(dim.width + 4, dim.height);
        }

        return dim;
    }

    /**
     *
     * @return  The toolTipText value
     */
    @Override
    public String getToolTipText() {
        return assoString;
    }
}
