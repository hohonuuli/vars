package vars.queryfx.ui.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Brian Schlining
 * @since 2015-07-29T13:16:00
 */
public class LikeConstraint implements IConstraint<String> {

    private final String columnName;
    private final String value;

    public LikeConstraint(String columnName, String value) {
        this.columnName = columnName;
        this.value = value;
    }

    @Override
    public int bind(PreparedStatement statement, int idx) throws SQLException {
        statement.setString(idx, '%' + value + '%');
        return idx + 1;
    }

    @Override
    public String toSQLClause() {
        return columnName + " LIKE '%" + value + "%'";
    }

    @Override
    public String toPreparedStatementTemplate() {
        return columnName + " LIKE ?";
    }
}
