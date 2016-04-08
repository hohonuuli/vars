package vars.queryfx.ui;

import com.google.common.collect.Lists;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import vars.query.results.QueryResults;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Brian Schlining
 * @since 2015-07-31T15:48:00
 */
public class QueryResultsTableViewDemo extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        Map<String, List<Object>> data = new HashMap<>();
        data.put("foo", Lists.newArrayList(1, 2, 3, 4, 5, 6, 7));
        data.put("bar", Lists.newArrayList("A", "B", "C", "D", "E", "F", "G"));
        QueryResults queryResults = new QueryResults(data);

        TableView tableView = QueryResultsTableView.newTableView(queryResults);
        BorderPane borderPane = new BorderPane(tableView);

        Scene scene = new Scene(borderPane);

        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
