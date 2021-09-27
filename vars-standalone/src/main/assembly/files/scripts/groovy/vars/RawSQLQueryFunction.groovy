package vars

import mbarix4j.sql.QueryFunction
import java.sql.ResultSet
import java.text.SimpleDateFormat

/**
 * 
 * @author Brian Schlining
 * @since 2011-12-06
 */
class RawSQLQueryFunction implements QueryFunction<String> {

    private final timeZone = TimeZone.getTimeZone("UTC")
    private final dateFormat
    private final utcCalendar = new GregorianCalendar(timeZone)
    private final df = new SimpleDateFormat('yyyyMMddHHmmss')

    RawSQLQueryFunction() {
        dateFormat = new SimpleDateFormat('yyyy-MM-dd HH:mm:ss')
        dateFormat.timeZone = timeZone
    }


    String apply(ResultSet rs) {
        def row = 0
        def columnCount = 0
        def metadata = null

        def out = new StringBuilder()

        def isTime = []
        while (rs.next()) {

            if (row == 0) {
                metadata = rs.metaData
                columnCount = rs.columnCount
                def column = 0
                for (i in 1..columnCount) {

                    // Write column names
                    if (i > 1) {
                        out << "\t"
                    }
                    out << metadata.getColumnName(i)

                    // Figure out if the column is a timestamp
                    isTime[i - 1] = metadata.getColumnClassName(i).equals('java.sql.Timestamp')
                    i++
                }
                out << "\n"
            }

            for (c in 1..columnCount) {
                if (c > 1) {
                    out << "\t"
                }
                if (isTime[c - 1]) {
                    try {
                        out << dateFormat.format(rs.getTimestamp(c, utcCalendar))
                    }
                    catch (Exception e) {
                        out << ''
                    }
                }
                else {
                    def obj = rs.getObject(c) ?: ''
                    out << obj
                }
            }
            out << "\n"
            row++

        }
        out << "\n# ---- ${row} records were found\n"
        return out.toString()
    }
}
