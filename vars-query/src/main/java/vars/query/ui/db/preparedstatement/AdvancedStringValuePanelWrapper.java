package vars.query.ui.db.preparedstatement;

import vars.query.ui.AdvancedStringValuePanel;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Brian Schlining
 * @since Nov 10, 2010
 */
public class AdvancedStringValuePanelWrapper extends AbstractValuePanelWrapper {

    public AdvancedStringValuePanelWrapper(AdvancedStringValuePanel valuePanel) {
        super(valuePanel);
    }

    public int bind(PreparedStatement statement, int idx) throws SQLException {
        AdvancedStringValuePanel vp = (AdvancedStringValuePanel) getValuePanel();

        if (vp.getToggleButton().isSelected()) {
            Object[] obj = vp.getList().getSelectedValues();
            if (obj.length > 0) {
                for (int i = 0; i < obj.length; i++) {
                    statement.setString(idx, obj[i].toString());
                    idx++;
                }
            }

        }
        else {
            if (vp.isConstrained()) {
                String text = vp.getTextField().getText();
                if (text.length() > 0) {
                    statement.setString(idx, "%" + vp.getTextField().getText() + "%");
                    idx++;
                }
            }
        }
        return idx;
    }

    public String toSQL() {

        StringBuffer sb = new StringBuffer();
        AdvancedStringValuePanel vp = (AdvancedStringValuePanel) getValuePanel();

        if (vp.getToggleButton().isSelected()) {
            Object[] obj = vp.getList().getSelectedValues();
            if (obj.length > 0) {
                sb.append(" ").append(vp.getValueName()).append(" IN (");
                for (int i = 0; i < obj.length; i++) {
                    sb.append("?");
                    if ((obj.length > 0) && (i < obj.length - 1)) {
                        sb.append(", ");
                    }
                }
                sb.append(")");
            }

        }
        else {
            if (vp.isConstrained()) {
                String text = vp.getTextField().getText();

                if (text.length() > 0) {
                    sb.append(" ").append(vp.getValueName()).append(" LIKE ? ");
                }
            }
        }

        return sb.toString();

    }

}
