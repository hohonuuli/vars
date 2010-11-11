package vars.query.ui.db.preparedstatement;

import vars.query.ui.ValuePanel;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Brian Schlining
 * @since Nov 9, 2010
 */
public interface ValuePanelWrapper {

    /**
     *
     * @return The underlying ValuePanel
     */
    ValuePanel getValuePanel();

    /**
     *
     * @return true if this shoudl be applied to the query
     */
    boolean isConstrained();

    int bind(PreparedStatement statement, int idx) throws SQLException;

    String toSQL();
}
