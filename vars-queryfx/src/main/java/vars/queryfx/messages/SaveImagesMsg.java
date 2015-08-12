package vars.queryfx.messages;

import javafx.stage.Stage;
import vars.queryfx.ui.db.results.QueryResults;

import javax.swing.text.html.Option;
import java.io.File;
import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2015-08-11T16:34:00
 */
public class SaveImagesMsg {
    private final File targetDir;
    private final QueryResults queryResults;
    private final Optional<Stage> stageOpt;

    public SaveImagesMsg(File targetDir, QueryResults queryResults, Optional<Stage> stageOpt) {
        this.queryResults = queryResults;
        this.targetDir = targetDir;
        this.stageOpt = stageOpt;
    }

    public SaveImagesMsg(File targetDir, QueryResults queryResults) {
        this(targetDir, queryResults, Optional.empty());
    }

    public QueryResults getQueryResults() {
        return queryResults;
    }

    public File getTargetDir() {
        return targetDir;
    }

    public Optional<Stage> getStageOpt() {
        return stageOpt;
    }
}
