package vars.queryfx.rx.messages;

import vars.queryfx.ui.db.results.QueryResults;

import java.io.File;
import java.util.function.Consumer;

/**
 * @author Brian Schlining
 * @since 2015-08-11T16:34:00
 */
public class SaveImagesMsg {
    private final File targetDir;
    private final QueryResults queryResults;
    private final Consumer<Double> progressFn;

    public SaveImagesMsg(File targetDir, QueryResults queryResults, Consumer<Double> progressFn) {
        this.queryResults = queryResults;
        this.targetDir = targetDir;
        this.progressFn = progressFn;
    }

    public SaveImagesMsg(File targetDir, QueryResults queryResults) {
        this(targetDir, queryResults, (Double d) -> {});
    }

    public QueryResults getQueryResults() {
        return queryResults;
    }

    public File getTargetDir() {
        return targetDir;
    }

    public Consumer<Double> getProgressFn() {
        return progressFn;
    }
}
