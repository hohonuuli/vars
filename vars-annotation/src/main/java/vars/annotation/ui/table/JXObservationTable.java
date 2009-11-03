package vars.annotation.ui.table;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.HashSet;

import javax.swing.JViewport;
import javax.swing.table.TableCellRenderer;

import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SuppressWarnings("serial")
public class JXObservationTable extends JXTable implements ObservationTable {

    private final Logger log = LoggerFactory.getLogger(JXObservationTable.class);

    public JXObservationTable() {
        super();
        final TableColumnModel tableColumnModel = new JXObservationTableColumnModel();
        final TableModel model = new JXObservationTableModel(tableColumnModel);
        setModel(model);
        setColumnModel(tableColumnModel);
        super.setHighlighters(new Highlighter[]{ HighlighterFactory.createAlternateStriping() });
        setRowHeightEnabled(true);
    }

    /* (non-Javadoc)
     * @see org.mbari.vars.annotation.ui.IObservationTable#populateWithObservations(vars.annotation.IVideoArchive)
     */
    @SuppressWarnings("unchecked")
    public void populateWithObservations(final IVideoArchive videoArchive) {

        // Get the TableModel
        final IObservationTableModel model = (IObservationTableModel) getModel();

        // Remove the current contents
        model.clear();

        // Repopulate it with the contents of the new VideoArchive
        if (videoArchive != null) {
            final IVideoArchive va = videoArchive;

            /*
             * Use copies of collections to avoid synchronization issues
             */
            final Collection<IVideoFrame> vfs = new HashSet<IVideoFrame>(va.getVideoFrames());
            for (IVideoFrame videoFrame : vfs) {
                final Collection<IObservation> observations = new HashSet<IObservation>(videoFrame.getObservations());
                for (IObservation observation : observations) {
                    model.addObservation(observation);
                }
            }
        }

    }

    /* (non-Javadoc)
     * @see org.mbari.vars.annotation.ui.IObservationTable#addObservation(vars.annotation.IObservation)
     */
    public void addObservation(final IObservation observation) {
        ((IObservationTableModel) getModel()).addObservation(observation);

        if (log.isDebugEnabled()) {
            log.debug("Adding " + observation + " to the table model");
        }
    }

    /* (non-Javadoc)
     * @see org.mbari.vars.annotation.ui.IObservationTable#removeObservation(vars.annotation.IObservation)
     */
    public void removeObservation(final IObservation observation) {
        ((IObservationTableModel) getModel()).removeObservation(observation);
    }

    /* (non-Javadoc)
     * @see org.mbari.vars.annotation.ui.IObservationTable#setSelectedObservation(vars.annotation.IObservation)
     */
    public void setSelectedObservation(final IObservation obs) {
        if (log.isDebugEnabled()) {
            log.debug("Setting the observation selected in the table to " + obs);
        }

        final int row  = convertRowIndexToView(((JXObservationTableModel) getModel()).getObservationRow(obs));
        getSelectionModel().setSelectionInterval(row, row);
        scrollCellToVisible(row, 0);
    }
    
    /* (non-Javadoc)
     * @see org.mbari.vars.annotation.ui.IObservationTable#getObservationAt(int)
     */
    public IObservation getObservationAt(final int row) {
        final IObservationTableModel model = (IObservationTableModel) getModel();

        return model.getObservationAt(convertRowIndexToModel(row));
    }
    
    /* (non-Javadoc)
     * @see org.mbari.vars.annotation.ui.IObservationTable#redrawAll()
     */
    public void redrawAll() {
        ((IObservationTableModel) getModel()).redrawAll();
    }

    /* (non-Javadoc)
     * @see org.mbari.vars.annotation.ui.IObservationTable#redrawRow(int)
     */
    public void redrawRow(final int row) {
        ((IObservationTableModel) getModel()).redrawRow(row);
    }

    /* (non-Javadoc)
     * @see org.mbari.vars.annotation.ui.IObservationTable#getPreferredRowHeight(int, int)
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

    @Override
    public TableCellRenderer getDefaultRenderer(Class<?> columnClass) {
        TableCellRenderer renderer = super.getDefaultRenderer(columnClass);
        if (renderer == null) {
            renderer = super.getDefaultRenderer(Object.class);
        }
        return renderer;
    }

    /**
     * Scrool to the corect row and column
     * @param rowIndex
     * @param vColIndex
     */
    public void scrollToVisible(final int rowIndex, final int vColIndex) {
        if (!(getParent() instanceof JViewport)) {
            return;
        }

        final JViewport viewport = (JViewport) getParent();

        // This rectangle is relative to the table where the
        // northwest corner of cell (0,0) is always (0,0).
        final Rectangle rect = getCellRect(rowIndex, vColIndex, true);

        // The location of the viewport relative to the table
        final Point pt = viewport.getViewPosition();

        // Translate the cell location so that it is relative
        // to the view, assuming the northwest corner of the
        // view is (0,0)
        rect.setLocation(rect.x - pt.x, rect.y - pt.y);

        // Scroll the area into view
        viewport.scrollRectToVisible(rect);
    }

}
