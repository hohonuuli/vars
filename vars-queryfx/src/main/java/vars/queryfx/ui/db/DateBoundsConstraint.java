package vars.queryfx.ui.db;

import vars.queryfx.StateLookup;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Brian Schlining
 * @since 2015-07-29T16:13:00
 */
public class DateBoundsConstraint implements IConstraint<Date> {

    private final String columnName;
    private final Date minDate;
    private final Date maxDate;

    public DateBoundsConstraint(String columnName, Date minDate, Date maxDate) {
        this.columnName = columnName;
        this.minDate = minDate;
        this.maxDate = maxDate;
    }

    @Override
    public int bind(PreparedStatement statement, int idx) throws SQLException {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        statement.setDate(idx, new java.sql.Date(minDate.getTime()), cal);
        idx++;
        statement.setDate(idx, new java.sql.Date(maxDate.getTime()), cal);
        return idx + 1;
    }

    @Override
    public String toSQLClause() {
        DateFormat df = StateLookup.getUTCDateFormat();
        return columnName + " BETWEEN '" + df.format(minDate) + "' AND '" +
                df.format(maxDate) + "'";
    }

    @Override
    public String toPreparedStatementTemplate() {
        return columnName + " BETWEEN ? AND ?";
    }
}
