package vars.queryfx.ui.db;

import com.google.common.base.Preconditions;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Brian Schlining
 * @since 2015-07-29T13:15:00
 */
public class MinConstraint implements IConstraint<Number> {

    private final String columnName;
    private final Number minValue;

    public MinConstraint(String columnName, Number minValue) {
        Preconditions.checkArgument(columnName != null && minValue != null);
        this.columnName = columnName;
        this.minValue = minValue;
    }

    @Override
    public int bind(PreparedStatement statement, int idx) throws SQLException {
        statement.setDouble(idx, minValue.doubleValue());
        return idx + 1;
    }

    @Override
    public String toSQLClause() {
        return columnName + " >= " + minValue;
    }

    @Override
    public String toPreparedStatementTemplate() {
        return columnName + " >= ?";
    }
}
