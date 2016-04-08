package vars.query.results;


import java.time.Instant;
import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2015-08-11T14:43:00
 */
public class QueryResultsUtilities {

    public static String createMetadataString(QueryResults queryResults, Optional<String> sql) {
        StringBuilder text = new StringBuilder(Instant.now().toString());

        sql.ifPresent(s -> {
            text.append("\n\n")
                    .append("DATABASE\n\t").append("\n")
                    .append("QUERY\n\t")
                    .append(s).append("\n\n")
                    .append("TOTAL RECORDS: ").append(queryResults.getRows());
        });

        return text.toString();
    }
}
