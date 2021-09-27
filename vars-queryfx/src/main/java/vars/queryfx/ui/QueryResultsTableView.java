package vars.queryfx.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import mbarix4j.net.URLUtilities;
import mbarix4j.util.Tuple2;
import vars.query.results.QueryResults;
import vars.shared.javafx.application.ImageFX;
import vars.shared.javafx.stage.ImageStage;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
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
        Tuple2<List<String>, List<String[]>> rowOrientedData = queryResults.toRowOrientedData();
        List<String> columnNames = rowOrientedData.getA();
        List<String[]> data = rowOrientedData.getB();

        // --- Build TableView
        TableView<String[]> tableView = getTableView(columnNames);
        tableView.getItems().addAll(data);


        return tableView;

    }


    private static TableView<String[]> getTableView(List<String> columnNames) {
        TableView<String[]> tableView = new TableView<>();

        // Generate a table column for each name
        for (int i = 0; i < columnNames.size(); i++) {
            TableColumn<String[], String> column = new TableColumn<>(columnNames.get(i));
            final int j = i;
            column.setCellValueFactory(param ->
                    new SimpleStringProperty(param.getValue()[j]));
            tableView.getColumns().add(i, column);
        }

        String uuid = UUID.randomUUID().toString(); // Key to associate a ImageFX view with a table
        URL url = QueryResultsTableView.class.getResource("/vars/queryfx/images/404-page-not-found.jpg");
        ImageStageExt imageStageExt = null;
        try {
            ImageStage imageStage = ImageFX.namedWindow(uuid, url.toExternalForm()).get(3, TimeUnit.SECONDS);
            imageStageExt = new ImageStageExt(imageStage);
        }
        catch (Exception e) {
            // TODO handle creation exception? Will this ever actually happen?
        }

        final FileChooser fileChooser = new FileChooser();


        // Add a listener to display an image if present and row is double clicked
        final ImageStageExt ext = imageStageExt;

        Function<String[], Void> showImageFn = rowItem -> {
            List<String> urls = Arrays.stream(rowItem)
                    .filter(s -> s.startsWith("http") || s.startsWith("file"))
                    .collect(Collectors.toList());
            if (!urls.isEmpty() && ext != null) {
                final String imageLocation = urls.get(0);
                final Image image = new Image(imageLocation);
                ext.saveButton.setOnAction(v -> {
                    try {
                        URL imageUrl = new URL(imageLocation);
                        fileChooser.setInitialFileName(URLUtilities.toFilename(imageUrl));
                        File selectedFile = fileChooser.showSaveDialog(ext.imageStage);
                        if (selectedFile != null) {
                            URLUtilities.copy(new URL(imageLocation), selectedFile);
                        }
                    }
                    catch (Exception e2) {
                        // TODO throw exception onto eventbus
                    }

                });
                ext.imageStage.setImage(image);
                ext.imageStage.show();
            }
            return null;
        };

        tableView.setRowFactory(tv -> {
            TableRow<String[]> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2) {
                    showImageFn.apply(row.getItem());
                }
            });

            return row;
        });

        tableView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String[] item = tableView.getSelectionModel().getSelectedItem();
                if (item != null) {
                    showImageFn.apply(item);
                }
            }
        });

        return  tableView;
    }


    private static class ImageStageExt {

        final Button saveButton;
        final ImageStage imageStage;

        public ImageStageExt(ImageStage imageStage) {
            this.imageStage = imageStage;
            BorderPane root = imageStage.getRoot();
            root.setStyle("-fx-background-color: black");
            // TODO style button
            saveButton = new Button("Save");
            ToolBar toolBar = new ToolBar(saveButton);
            toolBar.setStyle("-fx-background-color: black");
            root.setTop(toolBar);

        }

    }

}
