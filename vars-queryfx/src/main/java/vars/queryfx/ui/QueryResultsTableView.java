package vars.queryfx.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import vars.queryfx.ui.db.results.QueryResults;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author Brian Schlining
 * @since 2015-07-31T14:21:00
 */
public class QueryResultsTableView {

    // http://blog.ngopal.com.np/2011/10/19/dyanmic-tableview-data-from-database/


    public static TableView<String[]> newTableView(QueryResults queryResults) {

        // A TableView renders ROWS. We have to turn the data into
        // row-oreinted constructs. Here I turn each row into a
        // String array.

        // --- Turn Query results to row oriented data.
        List<String[]> data = new ArrayList<>();
        int rows = queryResults.getRows();
        int cols = queryResults.getColumns();
        for (int r = 0; r < rows; r++) {
            data.add(new String[cols]);
        }

        List<String> columnNames = new ArrayList<>(queryResults.getColumnNames());
        for (int c = 0; c < cols; c++) {
            List d = queryResults.getValues(columnNames.get(c));
            for (int r = 0; r < rows; r++) {
                Object val = d.get(r);
                String s = val == null ? "" : val.toString();
                data.get(r)[c] = s;
            }
        }

        TableView<String[]> tableView = new TableView<>();

        // --- Generate a table column for each name

        for (int i = 0; i < columnNames.size(); i++) {
            TableColumn<String[], String> column = new TableColumn<>(columnNames.get(i));
            final int j = i;
            column.setCellValueFactory(param ->
                    new SimpleStringProperty(param.getValue()[j]));
            tableView.getColumns().add(i, column);
        }


        tableView.getItems().addAll(data);


        return tableView;

    }

}
