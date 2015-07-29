package vars.queryfx.ui.db;

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
    public String getPreparedStatementBindingValues() {
        return "%" + value + "%";
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
