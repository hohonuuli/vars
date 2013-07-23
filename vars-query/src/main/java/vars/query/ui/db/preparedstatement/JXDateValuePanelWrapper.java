package vars.query.ui.db.preparedstatement;

import vars.query.ui.JXDateValuePanel;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Brian Schlining
 * @since 2013-07-18
 */
public class JXDateValuePanelWrapper extends AbstractValuePanelWrapper {

    public JXDateValuePanelWrapper(JXDateValuePanel valuePanel) {
        super(valuePanel);
    }

    public int bind(PreparedStatement statement, int idx) throws SQLException {
        return idx;
    }

    public String toSQL() {
        String s = "";
        if (isConstrained()) {
            s =  getValuePanel().getSQL();
        }
        return s;
    }
}