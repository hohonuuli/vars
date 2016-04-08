package vars.queryfx.ui;

import javafx.stage.Stage;
import vars.query.results.QueryResults;


/**
 * @author Brian Schlining
 * @since 2015-07-31T14:19:00
 */
public class QueryResultsStage extends Stage {

    private final QueryResults queryResults;

    public QueryResultsStage(QueryResults queryResults) {
        this.queryResults = queryResults;
    }
}
