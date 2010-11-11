package vars.query.ui.db.preparedstatement;

import vars.query.ui.BooleanValuePanel;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Brian Schlining
 * @since Nov 9, 2010
 */
public class BooleanValuePanelWrapper extends AbstractValuePanelWrapper {

    public BooleanValuePanelWrapper(BooleanValuePanel valuePanel) {
        super(valuePanel);
    }

    @Override
    public boolean isConstrained() {
        return false;
    }

    public int bind(PreparedStatement statement, int idx) throws SQLException {
        return idx;
    }

    public String toSQL() {
        return ""; 
    }
}

