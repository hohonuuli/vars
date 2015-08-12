package vars.queryfx.ui.controllers;

import javafx.stage.Stage;
import org.mbari.util.Tuple2;
import vars.queryfx.RXEventBus;
import vars.queryfx.messages.NonFatalExceptionMsg;
import vars.queryfx.messages.SaveAsKMLMsg;
import vars.queryfx.messages.SaveAsTextMsg;
import vars.queryfx.messages.SaveImagesMsg;
import vars.queryfx.ui.controllers.fn.SaveResultsAsKMLFn;
import vars.queryfx.ui.db.results.QueryResults;
import vars.queryfx.ui.db.results.QueryResultsUtilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Brian Schlining
 * @since 2015-08-11T14:29:00
 */
public class SaveResultsController {

    private final RXEventBus eventBus;

    public SaveResultsController(RXEventBus eventBus) {
        this.eventBus = eventBus;

        eventBus.toObserverable()
                .filter(msg -> msg instanceof SaveAsTextMsg)
                .map(msg -> (SaveAsTextMsg) msg)
                .subscribe(msg -> saveAsText(msg.getTarget(), msg.getQueryResults(), msg.getSql()));

        eventBus.toObserverable()
                .filter(msg -> msg instanceof SaveAsKMLMsg)
                .map(msg -> (SaveAsKMLMsg) msg)
                .subscribe(msg -> saveAsKML(msg.getTarget(), msg.getQueryResults(), msg.getSql()));

        eventBus.toObserverable()
                .filter(msg -> msg instanceof SaveImagesMsg)
                .map(msg -> (SaveImagesMsg) msg)
                .subscribe(msg -> saveImages(msg.getTargetDir(), msg.getQueryResults(), msg.getStageOpt()));
    }

    public void saveAsText(File file, QueryResults queryResults, Optional<String> sql) {
        String text = QueryResultsUtilities.createMetadataString(queryResults, sql);
        String[] lines = text.split("\n");
        String header = Arrays.stream(lines)
                .map(s -> "# " + s)
                .collect(Collectors.joining("\n"));
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(header);
            out.write("\n");

            Tuple2<List<String>, List<String[]>> content = queryResults.toRowOrientedData();
            List<String> columnNames = content.getA();
            List<String[]> rows = content.getB();

            String columns = "# " + columnNames.stream()
                    .collect(Collectors.joining("\t"));
            out.write(columns);
            out.write("\n");

            for (String[] r : rows) {
                String rs = Arrays.stream(r)
                        .collect(Collectors.joining("\t"));
                out.write(rs);
                out.write("\n");
            }
        }
        catch (Exception e) {
            eventBus.send(new NonFatalExceptionMsg("Unable to save results to " + file.getAbsolutePath(), e));
        }

    }

    public void saveAsKML(File file, QueryResults queryResults, Optional<String> sql) {
        SaveResultsAsKMLFn fn = new SaveResultsAsKMLFn(file, queryResults, sql);
        try {
            fn.apply();
        }
        catch (IOException e) {
            eventBus.send(new NonFatalExceptionMsg("Failed to save results to " + file.getAbsolutePath(), e));
        }

    }

    public void saveImages(File targetDir, QueryResults queryResults, Optional<Stage> stageOpt) {

    }
}
