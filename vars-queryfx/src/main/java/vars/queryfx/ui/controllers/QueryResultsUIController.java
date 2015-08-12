package vars.queryfx.ui.controllers;

import com.guigarage.sdk.util.MaterialDesignButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.mbari.util.Tuple2;
import vars.queryfx.RXEventBus;
import vars.queryfx.messages.NewQueryResultsMsg;
import vars.queryfx.messages.NonFatalExceptionMsg;
import vars.queryfx.messages.SaveAsKMLMsg;
import vars.queryfx.messages.SaveAsTextMsg;
import vars.queryfx.ui.QueryResultsTableView;
import vars.queryfx.ui.db.SQLStatementGenerator;
import vars.queryfx.ui.db.results.QueryResults;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Created by brian on 8/5/15.
 */
public class QueryResultsUIController {

    private final RXEventBus eventBus;

    public QueryResultsUIController(RXEventBus eventBus) {
        this.eventBus = eventBus;

        eventBus.toObserverable()
                .filter(msg -> msg instanceof NewQueryResultsMsg)
                .map(msg -> (NewQueryResultsMsg) msg)
                .subscribe(msg -> showQueryResults(msg.getStage(), msg.getQueryResults(), msg.getSql()));
    }

    public CompletableFuture<Stage> newQueryStage() {
        CompletableFuture<Stage> stageF = new CompletableFuture<>();
        Platform.runLater(() -> {

            Stage stage = new Stage();
            Instant toc = Instant.now();
            Label label0 = new Label("Executing query ... ");
            Label label1 = new Label("    started at " + toc.toString());
            Label label2 = new Label("    ...");
            VBox hBox = new VBox(label0, label1, label2);
            Scene scene = new Scene(hBox);

            Timeline timeline = new Timeline(new KeyFrame(javafx.util.Duration.millis(100), e -> {
                Instant tic = Instant.now();
                Duration d = Duration.between(toc, tic);
                String s = String.format("%5.0f", d.toMillis() / 1000D);
                label2.setText("    Elapsed time: " + s + " seconds");
            }));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();

            stage.setScene(scene);
            stage.show();
            stageF.complete(stage);
        });
        return stageF;
    }

    public void showQueryResults(Stage stage, QueryResults queryResults, Optional<String> sql) {

        Platform.runLater(() -> {

            TabPane tabPane = new TabPane();
            BorderPane borderPane = new BorderPane(tabPane);

            Tab resultsTab = new Tab("Data");
            resultsTab.setClosable(false);
            TableView<String[]> tableView = QueryResultsTableView.newTableView(queryResults);
            resultsTab.setContent(tableView);
            tabPane.getTabs().add(resultsTab);

            sql.ifPresent(s -> {
                String text = createMetadataString(queryResults, sql);
                Tab queryTab = new Tab("Query");
                queryTab.setClosable(false);
                TextArea textArea = new TextArea(text);
                queryTab.setContent(new BorderPane(textArea));
                tabPane.getTabs().add(queryTab);
            });

            ToolBar toolBar = new ToolBar();
            Button saveButton = new MaterialDesignButton("Save");
            saveButton.setOnAction(v -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialFileName("vars_query.txt");
                File saveFile = fileChooser.showSaveDialog(stage);
                if (saveFile != null) {
                    eventBus.send(new SaveAsTextMsg(saveFile, queryResults, sql));
                }

            });
            Button saveKMLButton = new MaterialDesignButton("Save KML");
            saveKMLButton.setOnAction(v -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialFileName("vars_query.kml");
                File saveFile = fileChooser.showSaveDialog(stage);
                if (saveFile != null) {
                    eventBus.send(new SaveAsKMLMsg(saveFile, queryResults, sql));
                }
            });
            Button saveImagesButton = new MaterialDesignButton("Save Images");
            toolBar.getItems().addAll(saveButton, saveKMLButton, saveImagesButton);
            borderPane.setTop(toolBar);

            Scene scene = new Scene(borderPane);
            stage.setScene(scene);

        });

    }

    public String createMetadataString(QueryResults queryResults, Optional<String> sql) {
        StringBuilder text = new StringBuilder(Instant.now().toString());

        sql.ifPresent(s -> {
            text.append("\n\n")
                    .append("DATABASE\n\t").append("\n")
                    .append("QUERY\n\t")
                    .append(s).append("\n\n")
                    .append("TOTAL RECORDS: ").append(queryResults.getRows());
        });

        return text.toString();
    }



}
