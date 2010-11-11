package vars.query.ui.db.preparedstatement;

import vars.query.ui.StringLikeValuePanel;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Brian Schlining
 * @since Nov 9, 2010
 */
public class StringLikeValuePanelWrapper extends AbstractValuePanelWrapper {

    public StringLikeValuePanelWrapper(StringLikeValuePanel valuePanel) {
        super(valuePanel);
    }

    public int bind(PreparedStatement statement, int idx) throws SQLException {
        if (isConstrained()) {
            String text = ((StringLikeValuePanel) getValuePanel()).getTextField().getText();
            statement.setString(idx, "%" + text + "%");
            idx++;
        }
        return idx;
    }

    public String toSQL() {
        String s = "";
        if (isConstrained()) {
            s = " " + getValuePanel().getValueName() + " LIKE ? ";
        }
        return s;
    }

    @Override
    public boolean isConstrained() {
        String text = ((StringLikeValuePanel) getValuePanel()).getTextField().getText();
        return super.isConstrained() && text.length() > 0;    //To change body of overridden methods use File | Settings | File Templates.
    }
}
