package vars.query.ui.db.preparedstatement;

import vars.query.ui.StringValuePanel;

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
        if (isConstrained()) {
            Object[] obj = ((StringValuePanel) getValuePanel()).getList().getSelectedValues();
            for (Object o : obj) {
                statement.setString(idx, obj.toString());
                idx++;
            }
        }
        return idx; 
    }

    public String toSQL() {
        StringBuffer sb = new StringBuffer("");
        if (isConstrained()) {
            Object[] obj = ((StringValuePanel) getValuePanel()).getList().getSelectedValues();
            sb.append(" ").append(getValuePanel().getValueName()).append(" IN (");
            for (Object o : obj) {
                sb.append("?, ");
            }
            // Delete trailing ", "
            sb.delete(sb.length() - 2, sb.length());
            sb.append(")");
        }
        return sb.toString();
    }

    @Override
    public boolean isConstrained() {
        boolean anySelected = ((StringValuePanel) getValuePanel()).getList().getSelectedValues().length > 0;
        return super.isConstrained() && anySelected;    //To change body of overridden methods use File | Settings | File Templates.
    }
}
