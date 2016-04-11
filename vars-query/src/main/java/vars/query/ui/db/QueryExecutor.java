package vars.query.ui.db;


import vars.query.results.QueryResults;

/**
 * @author Brian Schlining
 * @since Nov 10, 2010
 */
public interface QueryExecutor {

    QueryResults query();

    String getSQL();

}
