/*
 * Copyright 2005 MBARI
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1 
 * (the "License"); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package vars.query.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import mbarix4j.util.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mbarix4j.awt.event.ActionAdapter;
import vars.query.results.QueryResults;

//~--- classes ----------------------------------------------------------------

/**
 * <p><!-- Insert Description --></p>
 *
 * @author Brian Schlining
 * @version $Id: SaveQueryResultsAction.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class SaveQueryResultsAction extends ActionAdapter {

    /**
     *
     */
    private final Logger log = LoggerFactory.getLogger(SaveQueryResultsAction.class);

    private final File file;
    private final String query;
    private final QueryResults queryResults;

    private final String databaseUrl;

    //~--- constructors -------------------------------------------------------

    /**
     *
     *
     * @param file
     * @param queryResults
     */
    public SaveQueryResultsAction(File file, QueryResults queryResults) {
        this(file, queryResults, null, null);
    }

    /**
     *
     *
     * @param file
     * @param queryResults
     * @param query
     */
    public SaveQueryResultsAction(File file, QueryResults queryResults,
            String query, String databaseUrl) {
        this.file = file;
        this.queryResults = queryResults;
        this.query = query;
        this.databaseUrl = databaseUrl == null ? "unknown" : databaseUrl;
    }

    //~--- methods ------------------------------------------------------------

    /*
     *  (non-Javadoc)
     * @see mbarix4j.awt.event.IAction#doAction()
     */

    /**
     * <p>Writes the query results out as tab-delimited text to a file</p>
     *
     */
    public void doAction() {
        /*
         * Write out the string
         */
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(header());
            Tuple2<List<String>, List<String[]>> content = queryResults.toRowOrientedData();
            List<String> columnNames = content.getA();
            List<String[]> rows = content.getB();

            String columns = columnNames.stream()
                    .collect(Collectors.joining("\t"));
            out.write(columns);
            out.write("\n");

            for (String[] r : rows) {
                String rs = Arrays.stream(r)
                        .collect(Collectors.joining("\t"));
                out.write(rs);
                out.write("\n");
            }
            out.close();
        } catch (IOException e) {
            log.error("Unable to save to " + file.getAbsolutePath(), e);
        }
    }

    /**
     * Generates a simple header with a few bits of useful information
     * @return
     */
    private String header() {
        StringBuffer sb = new StringBuffer();
        sb.append("# ").append(new Date()).append("\n");
        sb.append("#\n");
        sb.append("# DATABASE\n# ").append(databaseUrl).append("\n");
        sb.append("#\n");
        sb.append("# QUERY\n# ").append(query).append("\n");
        sb.append("#\n");
        sb.append("# TOTAL RECORDS: ").append(
                queryResults.getRows()).append("\n");
        return sb.toString();
    }
}
