package vars.queryfx.beans;

import vars.queryfx.ui.db.IConstraint;

import java.util.List;

/**
 * @author Brian Schlining
 * @since 2015-07-30T13:58:00
 */
public class QueryParams {

    private final List<IConstraint> queryConstraints;
    private final List<String> queryReturns;


    public QueryParams(List<String> queryReturns, List<IConstraint> queryConstraints) {
        this.queryConstraints = queryConstraints;
        this.queryReturns = queryReturns;
    }

    public List<IConstraint> getQueryConstraints() {
        return queryConstraints;
    }

    public List<String> getQueryReturns() {
        return queryReturns;
    }
}
