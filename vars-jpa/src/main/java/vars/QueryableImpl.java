package vars;

import vars.query.IQueryable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mbari.sql.QueryResults;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DriverManager;

/**
 * Generic class for working with an SQL connection.
 */
public class QueryableImpl implements IQueryable {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Standard format for all Dates. No timezone is displayed.
     * THe date will be formatted for the UTC timezone
     */
    protected final DateFormat dateFormatUTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") {

        {
            setTimeZone(TimeZone.getTimeZone("UTC"));
        }
    };
    private final ThreadLocal<Connection> connections = new ThreadLocal<Connection>();
    private final String jdbcPassword;
    private final String jdbcUrl;
    private final String jdbcUsername;

    /**
     * Constructs ...
     */
    public QueryableImpl(String jdbcUrl, String jdbcUsername, String jdbcPassword, String driverClass) {
        this.jdbcUrl = jdbcUrl;
        this.jdbcUsername = jdbcUsername;
        this.jdbcPassword = jdbcPassword;

        try {
            Class.forName(driverClass);
        }
        catch (ClassNotFoundException ex) {
            throw new VARSException("Failed to initialize driver class:" + driverClass, ex);
        }
    }

    public QueryResults executeQuery(String sql) {

        // Just wrap the QueryResults returned by the sql query with a QueryResults object
        QueryFunction queryFunction = new QueryFunction() {

            public Object apply(ResultSet resultsSet) throws SQLException {
                return new QueryResults(resultsSet);
            }
        };

        return (QueryResults) executeQueryFunction(sql, queryFunction);

    }

    protected Object executeQueryFunction(String query, QueryFunction queryFunction) {
        log.debug("Executing SQL query: \n\t" + query);

        Object object = null;
        Connection connection = null;
        try {
            connection = getConnection();
            final Statement stmt = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery(query);
            object = queryFunction.apply(rs);
            rs.close();
            stmt.close();
            connection.close();
        }
        catch (Exception e) {
            if (connection != null) {
                log.error("Failed to execute the following SQL on EXPD:\n" + query, e);

                try {
                    connection.close();
                }
                catch (SQLException ex) {
                    log.error("Failed to close database connection", ex);
                }
            }

            throw new VARSException("Failed to execute the following SQL on EXPD: " + query, e);
        }

        return object;
    }

    protected int executeUpdate(String updateSql) {
        log.debug("Executing SQL update: \n\t" + updateSql);

        int n = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            final Statement stmt = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_UPDATABLE);
            n = stmt.executeUpdate(updateSql);
            stmt.close();
            connection.close();
        }
        catch (Exception e) {
            if (connection != null) {
                log.error("Failed to execute the following SQL on EXPD:\n" + updateSql, e);

                try {
                    connection.close();
                }
                catch (SQLException ex) {
                    log.error("Failed to close database connection", ex);
                }
            }

            throw new VARSException("Failed to execute the following SQL on EXPD: " + updateSql, e);
        }
        return n;
    }

    /**
     * Simple function that does some unit of work with a ResultSet and returns
     * a result.
     */
    protected interface QueryFunction {
        Object apply(ResultSet resultSet) throws SQLException;
    }

    /**
     * @return A {@link Connection} to the EXPD database. The connection should
     *      be closed when you're done with it.
     * @throws SQLException
     */
    protected Connection getConnection() throws SQLException {
        Connection connection = connections.get();
        if ((connection == null) || connection.isClosed()) {
            if (log.isDebugEnabled()) {
                log.debug("Opening JDBC connection:" + jdbcUsername + " @ " + jdbcUrl);
            }

            connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
            connections.set(connection);
        }

        return connection;
    }
}
