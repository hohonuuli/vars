package vars.queryfx.rx.messages;

import vars.queryfx.ui.db.results.QueryResults;

import java.io.File;
import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2015-08-11T16:34:00
 */
public class SaveAsTextMsg {

    private final File target;
    private final QueryResults queryResults;
    private final Optional<String> sql;

    public SaveAsTextMsg(File target, QueryResults queryResults, Optional<String> sql) {
        this.queryResults = queryResults;
        this.target = target;
        this.sql = sql;
    }

    public QueryResults getQueryResults() {
        return queryResults;
    }

    public Optional<String> getSql() {
        return sql;
    }

    public File getTarget() {
        return target;
    }
}
