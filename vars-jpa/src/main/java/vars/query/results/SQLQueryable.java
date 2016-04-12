package vars.query.results;


import java.util.function.Supplier;

/**
 * Created by brian on 4/10/16.
 */
public interface SQLQueryable {
     QueryResults executeQuery(String sql) throws Exception;

    <T> T executeQueryFunction(String sql, Supplier<T> fn);
}
