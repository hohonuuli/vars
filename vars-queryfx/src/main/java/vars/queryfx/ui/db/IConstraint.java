package vars.queryfx.ui.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Brian Schlining
 * @since 2015-07-29T13:15:00
 */
public interface IConstraint<T> {

    String toSQLClause();

    String toPreparedStatementTemplate();

    int bind(PreparedStatement statement, int idx) throws SQLException;

}
