package vars.query.ui.db;

import org.mbari.sql.QueryResults;

/**
 * @author Brian Schlining
 * @since Nov 10, 2010
 */
public interface QueryExecutor {

    QueryResults query();

    String getSQL();

}
