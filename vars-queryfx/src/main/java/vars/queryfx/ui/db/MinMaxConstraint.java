package vars.queryfx.ui.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Brian Schlining
 * @since 2015-07-29T13:16:00
 */
public class MinMaxConstraint implements IConstraint {

    private final String columnName;
    private final Number minValue;
    private final Number maxValue;


    public MinMaxConstraint(String columnName, Number minValue, Number maxValue) {
        this.columnName = columnName;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public int bind(PreparedStatement statement, int idx) throws SQLException {
        statement.setDouble(idx, minValue.doubleValue());
        idx++;
        statement.setDouble(idx, maxValue.doubleValue());
        return idx + 1;
    }

    @Override
    public String toSQLClause() {
        return "(" + columnName + " >= " + minValue + " AND " +
                columnName + " <= " + maxValue + ")";
    }

    @Override
    public String toPreparedStatementTemplate() {
        return "(" + columnName + " >= ? AND " + columnName + " <= ?)";
    }
}
