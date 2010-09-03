/*
 * @(#)JXObservationTable.java   2009.11.12 at 10:11:19 PST
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



package vars.annotation.ui.table;

import java.awt.Component;
import java.util.Collection;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTableHeader;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.Observation;


/**  
 */
@SuppressWarnings("serial")
public class JXObservationTable extends JXTable implements ObservationTable {

    private final Logger log = LoggerFactory.getLogger(JXObservationTable.class);
    /**
     * Constructs ...
     */
    public JXObservationTable() {
        super();
        final TableColumnModel tableColumnModel = new JXObservationTableColumnModel();
        final TableModel model = new JXObservationTableModel(tableColumnModel);
        final JTableHeader aTableHeader = new JXTableHeader(tableColumnModel);

        setModel(model);
        setColumnModel(tableColumnModel);
        setAutoCreateColumnsFromModel(true);
        setSortable(true);
        setAutoscrolls(true);
        aTableHeader.setTable(this);
        setTableHeader(aTableHeader);

        // SwingX Highlighters do not play well with custom sell renderers. Turned this off for now
        //super.setHighlighters(HighlighterFactory.createSimpleStriping());
        setRowHeightEnabled(true);
    }

    /**
     *
     * @param observation
     */
    public void addObservation(final Observation observation) {
        ((ObservationTableModel) getModel()).addObservation(observation);

        if (log.isDebugEnabled()) {
            log.debug("Adding " + observation + " to the table model");
        }
    }

    /**
     * Essentially, this replaces the observation with the matchin primary key in the model
     * with the one you provided.
     */
    public void updateObservation(Observation observation) {
    	((ObservationTableModel) getModel()).updateObservation(observation);
    }
    /**
     *
     * @param columnClass
     * @return
     */
    @Override
    public TableCellRenderer getDefaultRenderer(Class<?> columnClass) {
        TableCellRenderer renderer = super.getDefaultRenderer(columnClass);

        if (renderer == null) {
            renderer = super.getDefaultRenderer(Object.class);
        }

        return renderer;
    }

    /**
     *
     * @param row
     * @return
     */
    public Observation getObservationAt(final int row) {
        final ObservationTableModel model = (ObservationTableModel) getModel();

        return model.getObservationAt(convertRowIndexToModel(row));
    }

    /**
     *
     * @param rowIndex
     * @param margin
     * @return
     */
    public int getPreferredRowHeight(final int rowIndex, final int margin) {

        // Get the current default height for all rows
        int height = getRowHeight();

        // Determine highest cell in the row
        for (int c = 0; c < getColumnCount(); c++) {
            final TableCellRenderer renderer = getCellRenderer(rowIndex, c);
            final Component comp = prepareRenderer(renderer, rowIndex, c);
            final int h = comp.getPreferredSize().height + 2 * margin;

            height = Math.max(height, h);
        }

        return height;
    }


    /**
     */
    public void redrawAll() {
        ((ObservationTableModel) getModel()).redrawAll();
    }

    /**
     *
     * @param row
     */
    public void redrawRow(final int row) {
        ((ObservationTableModel) getModel()).redrawRow(row);
    }

    /**
     *
     * @param observation
     */
    public void removeObservation(final Observation observation) {
        ((ObservationTableModel) getModel()).removeObservation(observation);
    }

    /**
     * Scroll to the correct row and column
     * @param rowIndex
     * @param vColIndex
     */
    public void scrollToVisible(final int rowIndex, final int vColIndex) {
        scrollCellToVisible(rowIndex, vColIndex);
    }

    /**
     * Selects the row containing the observation
     * @param obs
     */
    public void setSelectedObservation(final Observation obs) {
        if (log.isDebugEnabled()) {
            log.debug("Setting the observation selected in the table to " + obs);
        }

        final int row = convertRowIndexToView(((JXObservationTableModel) getModel()).getObservationRow(obs));

        getSelectionModel().setSelectionInterval(row, row);
        scrollCellToVisible(row, 0);
    }

    /**
     * Selects the rows containing the provided observation
     * @param observations
     */
    public void setSelectedObservations(final Collection<Observation> observations) {
        ListSelectionModel mySelectionModel = getSelectionModel();
        mySelectionModel.clearSelection();

        JXObservationTableModel model = (JXObservationTableModel) getModel();
        getSelectionModel().clearSelection();
        for (Observation observation : observations) {
            try {
                final int row = convertRowIndexToView(model.getObservationRow(observation));
                mySelectionModel.addSelectionInterval(row, row);
            }
            catch (IndexOutOfBoundsException e) {
                // This occurs if an observation is no longer in the table.
                // We can safely ignore the exception
            }
        }

        int[] i = getSelectedRows();
        if (i != null && i.length > 0) {
            scrollCellToVisible(i[0], 0);
        }
    }

    public JTable getJTable() {
        return this;
    }
}
