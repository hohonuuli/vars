package vars.queryfx.beans;

import java.util.List;

/**
 * @author Brian Schlining
 * @since 2015-07-29T13:13:00
 */
public class QueryReturns {

    private final List<String> columns;

    public QueryReturns(List<String> columns) {
        this.columns = columns;
    }

    public List<String> getColumns() {
        return columns;
    }
}
