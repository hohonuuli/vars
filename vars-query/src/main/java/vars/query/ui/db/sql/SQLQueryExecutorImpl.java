package vars.query.ui.db.sql;

import org.mbari.sql.IQueryable;
import vars.VARSException;
import vars.query.results.QueryResults;
import vars.query.ui.db.AbstractQueryExecutor;

import java.util.Collection;

/**
 * @author Brian Schlining
 * @since Nov 10, 2010
 */
public class SQLQueryExecutorImpl extends AbstractQueryExecutor {

    private final IQueryable queryable;

    public SQLQueryExecutorImpl(Collection conceptConstraints,
            Collection valuePanels, boolean allInterpretations,
                boolean allAssociations, IQueryable queryable) {
        super(conceptConstraints, valuePanels, allInterpretations, allAssociations);
        this.queryable = queryable;

     }

    public QueryResults query() {
        String sql = getSQL();
        try {
            return queryable.executeQuery(sql);
        } catch (Exception e) {
            throw new VARSException("Failed to execute sql:\n" + sql, e);
        }
    }


}
