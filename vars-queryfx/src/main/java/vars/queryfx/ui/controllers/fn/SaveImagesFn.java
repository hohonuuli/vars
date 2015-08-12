package vars.queryfx.ui.controllers.fn;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import vars.queryfx.ui.db.results.QueryResults;

import java.io.File;
import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2015-08-11T14:30:00
 */
public class SaveImagesFn {
    private final File targetDir;
    private final QueryResults queryResults;
    private final Optional<Stage> stageOpt;
    private volatile boolean ok = true;
    private Label fileLabel;
    private ProgressBar progressBar;

    public SaveImagesFn(QueryResults queryResults, File targetDir, Optional<Stage> stageOpt) {
        this.queryResults = queryResults;
        this.targetDir = targetDir;
        this.stageOpt = stageOpt;
    }

    public void apply() {
        stageOpt.ifPresent(this::getDialog);

    }

    private Dialog getDialog(Stage stage) {
        Dialog<Void> dialog = new Dialog();
        Label label = new Label("Saving images to " + targetDir.getAbsolutePath())
        fileLabel = new Label("");
        progressBar = new ProgressBar(0);
        HBox hbox = new HBox(fileLabel, progressBar);
        VBox vbox = new VBox(label, hbox);
        dialog.getDialogPane().setContent(vbox);
        dialog.setTitle("Saving images");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        btOk.setOnAction(v -> ok = false);
    }


}
