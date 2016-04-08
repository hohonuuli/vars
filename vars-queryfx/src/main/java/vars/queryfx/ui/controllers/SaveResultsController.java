package vars.queryfx.ui.controllers;

import org.mbari.util.Tuple2;
import vars.query.results.QueryResults;
import vars.query.results.QueryResultsUtilities;
import vars.shared.rx.RXEventBus;
import vars.shared.rx.messages.NonFatalExceptionMsg;
import vars.queryfx.rx.messages.SaveAsKMLMsg;
import vars.queryfx.rx.messages.SaveAsTextMsg;
import vars.queryfx.rx.messages.SaveImagesMsg;
import vars.query.results.SaveImagesFn;
import vars.query.results.SaveResultsAsKMLFn;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Brian Schlining
 * @since 2015-08-11T14:29:00
 */
public class SaveResultsController {

    private final RXEventBus eventBus;
    private final Executor executor;

    public SaveResultsController(RXEventBus eventBus, Executor executor) {
        this.eventBus = eventBus;
        this.executor = executor;

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
                .subscribe(msg -> saveImages(msg.getTargetDir(), msg.getQueryResults(), msg.getProgressFn()));
    }

    public void saveAsText(File file, QueryResults queryResults, Optional<String> sql) {

        executor.execute(() -> {
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
        });


    }

    public void saveAsKML(File file, QueryResults queryResults, Optional<String> sql) {
        SaveResultsAsKMLFn fn = new SaveResultsAsKMLFn(executor, file, queryResults, sql);
        fn.apply();
    }

    public void saveImages(File targetDir, QueryResults queryResults, Consumer<Double> progressFn) {
        SaveImagesFn fn = new SaveImagesFn(executor, targetDir, queryResults, progressFn);
        fn.apply();
    }
}
