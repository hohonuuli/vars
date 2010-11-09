package vars.query.ui.jdbc;

import vars.query.ui.StringValuePanel;
import vars.query.ui.ValuePanel;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Brian Schlining
 * @since Nov 9, 2010
 */
public class StringValuePanelWrapper extends AbstractValuePanelWrapper {

    public StringValuePanelWrapper(StringValuePanel valuePanel) {
        super(valuePanel);
    }

    public int bind(PreparedStatement statement, int idx) throws SQLException {
        return 0;  //TODO finish me
    }

    public String toSQL() {
        String s = "";
        if (isConstrained()) {
            // TODO finish implementation
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isConstrained() {
        boolean anySelected = ((StringValuePanel) getValuePanel()).getList().getSelectedValues().length > 0;
        return super.isConstrained() && anySelected;    //To change body of overridden methods use File | Settings | File Templates.
    }
}
