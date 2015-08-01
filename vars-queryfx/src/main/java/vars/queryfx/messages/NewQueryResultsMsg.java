package vars.queryfx.messages;

import vars.queryfx.ui.db.results.QueryResults;

/**
 * @author Brian Schlining
 * @since 2015-07-31T14:05:00
 */
public class NewQueryResultsMsg implements Msg {

    private final QueryResults queryResults;

    public NewQueryResultsMsg(QueryResults queryResults) {
        this.queryResults = queryResults;
    }

    public QueryResults getQueryResults() {
        return queryResults;
    }
}
