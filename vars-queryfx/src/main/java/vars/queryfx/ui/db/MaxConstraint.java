package vars.queryfx.ui.db;

import com.google.common.base.Preconditions;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Brian Schlining
 * @since 2015-07-29T13:16:00
 */
public class MaxConstraint implements IConstraint<Number> {

    private final String columnName;
    private final Number maxValue;

    public MaxConstraint(String columnName, Number maxValue) {
        Preconditions.checkArgument(columnName != null && maxValue != null);
        this.columnName = columnName;
        this.maxValue = maxValue;
    }

    @Override
    public int bind(PreparedStatement statement, int idx) throws SQLException {
        statement.setDouble(idx, maxValue.doubleValue());
        return idx + 1;
    }

    @Override
    public String toSQLClause() {
        return columnName + " <= " + maxValue ;
    }

    @Override
    public String toPreparedStatementTemplate() {
        return columnName + " <= ?";
    }
}
