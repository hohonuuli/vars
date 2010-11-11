package vars.query.ui.db.preparedstatement;

import vars.query.ui.NumberValuePanel;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Brian Schlining
 * @since Nov 9, 2010
 */
public class NumberValuePanelWrapper extends AbstractValuePanelWrapper {

    public NumberValuePanelWrapper(NumberValuePanel valuePanel) {
        super(valuePanel);
    }

    public int bind(PreparedStatement statement, int idx) throws SQLException {
        return idx;
    }

    public String toSQL() {
        String s = "";
        if (isConstrained()) {
            s = getValuePanel().getSQL();
        }
        return s;
    }
}
