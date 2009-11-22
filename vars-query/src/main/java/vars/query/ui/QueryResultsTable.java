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


package vars.query.ui;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mbari.sql.QueryResults;
import org.mbari.swing.ImageFrame;
import org.mbari.swing.table.TableSorter;


/**
 *
 * @author Brian Schlining
 */
public class QueryResultsTable extends JTable {


    private static final Logger log = LoggerFactory.getLogger(QueryResultsTable.class);


    private MouseListener urlMouseListener;


    /**
     * Constructs ...
     *
     *
     * @param queryResults
     */
    public QueryResultsTable(QueryResults queryResults) {
        super();
        setAutoCreateColumnsFromModel(true);
        setAutoResizeMode(AUTO_RESIZE_OFF);

        /*
         * Add the ability to sort by columns
         */
        TableSorter tableSorter = new TableSorter(
            new QueryResultsTableModel(queryResults));
        setModel(tableSorter);
        tableSorter.setTableHeader(getTableHeader());
        setColumnWidths();
        addMouseListener(getUrlMouseListener());
    }


    /**
	 * Creates a mouselistener that listens for double clicks. If a double click occurs in a cell starting with 'http', it attempts to display it in an <code>ImageFrame</code.
	 * @return  A MouseListener that displays image URLs when double clicked.
	 */
    private MouseListener getUrlMouseListener() {
        if (urlMouseListener == null) {
            urlMouseListener = new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {

                    /*
                     * Ignore single-clicks. We only want to deal with
                     * double clicks.
                     */
                    if (e.getClickCount() < 2) {
                        return;
                    }

                    /*
                     * Get the user selected row and column indexes.
                     */
                    Point clickPoint = e.getPoint();
                    int col = QueryResultsTable.this.columnAtPoint(clickPoint);
                    int row = QueryResultsTable.this.rowAtPoint(clickPoint);

                    /*
                     * If the value at the point clicked starts with 'http'
                     * we're assuming that it's an image and we'll attempt to
                     * display it in an imageFrame.
                     */
                    Object value = getValueAt(row, col);
                    if (value != null) {
                        String stringValue = value.toString();
                        if (stringValue.toLowerCase().startsWith("http")) {
                            URL url = null;
                            try {
                                url = new URL(stringValue);
                            } catch (MalformedURLException e1) {
                                log.warn(
                                        "The URL, " + url +
                                        ", is not valid. Unable to display it");
                            }

                            getImageFrame().setImageUrl(url);
                            getImageFrame().setVisible(true);
                        }
                    }
                }
                ImageFrame getImageFrame() {
                    if (imageFrame == null) {
                        imageFrame = new ImageFrame();
                        imageFrame.setDefaultCloseOperation(
                                JFrame.HIDE_ON_CLOSE);
                    }

                    return imageFrame;
                }
                ImageFrame imageFrame;
            };
        }

        return urlMouseListener;
    }


    /**
     * Process all the values in the tables to set the column width so that all
     * data is displayed without the user having to resize the table cells.
     *
     */
    private void setColumnWidths() {
        int columnCount = getColumnCount();
        int rowCount = getRowCount();
        for (int col = 0; col < columnCount; col++) {
            int width = getColumnName(col).length();
            for (int row = 0; row < rowCount; row++) {
                Object value = getValueAt(row, col);
                if (value != null) {
                    width = Math.max(width, value.toString().length());
                }
            }

            /*
             * TODO 20050429 brian: Should set width using Font metrics but for now
             * I'm using the magic number of '7' which seems to work pretty well
             */
            width = Math.min(width * 7, 400);
            TableColumn tableColumn = getColumnModel().getColumn(col);
            tableColumn.setMinWidth(width);
            tableColumn.setPreferredWidth(width);
        }
    }


    /**
     * <p>Generates a non-editable TableModel based on the contents of a queryResult.</p>
     *
     */
    class QueryResultsTableModel extends DefaultTableModel {


        /**
         *
         *
         * @param queryResults
         */
        QueryResultsTableModel(QueryResults queryResults) {
            super(queryResults.getDataArray(),
                    (String[]) queryResults.getColumnNames().toArray(new String[queryResults.getColumnNames().size()]));
        }

        /*
         *  (non-Javadoc)
         * @see javax.swing.table.TableModel#isCellEditable(int, int)
         */

        /**
         * <p><!-- Method description --></p>
         *
         *
         * @param row
         * @param column
         *
         * @return
         */
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }
}
