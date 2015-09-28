package vars.queryfx.ui.db;

import com.google.common.base.Preconditions;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Brian Schlining
 * @since 2015-07-29T13:16:00
 */
public class InConstraint implements IConstraint<List<String>> {

    private final String columnName;
    private final List<String> values;

    public InConstraint(String columnName, List<String> values) {
        Preconditions.checkArgument(columnName != null);
        Preconditions.checkArgument(values != null && !values.isEmpty(),
                "Check your value arg! null and empty values are not allowed");
        this.columnName = columnName;
        this.values = new ArrayList<>(values);
    }


    public List<String> getPreparedStatementBindingValues() {
        return new ArrayList<>(values);
    }

    public int bind(PreparedStatement statement, int idx) throws SQLException{
        for (String v: values) {
            statement.setString(idx, "%" + v + "%");
            idx++;
        }
        return idx;
    }

    @Override
    public String toSQLClause() {
        if (values.size() == 1) {
            return columnName + " = '" + values.get(0) + "'";
        }
        else {
            return columnName + " IN " +
                    values.stream()
                            .map(s -> "'" + s + "'")
                            .collect(Collectors.joining(", ", "(", ")"));
        }
    }

    @Override
    public String toPreparedStatementTemplate() {
        if (values.size() == 1) {
            return columnName + " = ?";
        }
        else {
            return columnName + " IN " +
                    values.stream()
                            .map(s -> "?")
                            .collect(Collectors.joining(", ", "(", ")"));
        }
    }
}
