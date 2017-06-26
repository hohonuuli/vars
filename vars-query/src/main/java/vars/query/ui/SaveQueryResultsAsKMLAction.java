/*
 * @(#)SaveQueryResultsAsKMLAction.java   2009.11.21 at 08:14:22 PST
 *
 * Copyright 2009 MBARI
 *
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
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;
import org.mbari.awt.event.ActionAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.query.results.QueryResults;

/**
 *
 * @author brian
 */
public class SaveQueryResultsAsKMLAction extends ActionAdapter {

    private static final String KEY_CONCEPTNAME = "conceptname";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_RECORDEDDATE = "recordeddate";
    private static final String KEY_DEPTH = "depth";
    private static final Logger log = LoggerFactory.getLogger(SaveQueryResultsAsKMLAction.class);
    private final String databaseUrl;
    private final File file;
    private final String query;
    private final QueryResults queryResults;
    final DateFormat dateFormatISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'") {{
        setTimeZone(TimeZone.getTimeZone("UTC"));
    }};

    /**
     *
     *
     * @param file
     * @param queryResults
     */
    public SaveQueryResultsAsKMLAction(File file, QueryResults queryResults) {
        this(file, queryResults, null, null);
    }

    /**
     *
     *
     * @param file
     * @param queryResults
     * @param query
     * @param databaseUrl
     */
    public SaveQueryResultsAsKMLAction(File file, QueryResults queryResults, String query, String databaseUrl) {
        this.file = file;
        this.queryResults = queryResults;
        this.query = query;
        this.databaseUrl = (databaseUrl == null) ? "unknown" : databaseUrl;

    }

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

            // Write header
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            out.write("<!-- " + new Date() + " -->");
            out.write("<!-- DATABASE: " + databaseUrl + " -->");
            out.write("<!-- QUERY: " + query + " -->");
            out.write("<kml xmlns=\"http://earth.google.com/kml/2.1\">\n");
            out.write("  <Document>\n");
            out.write("    <Style id=\"highlightPlacemark\"><IconStyle><Icon>\n");
            out.write("      <href>http://maps.google.com/mapfiles/kml/paddle/purple-blank.png</href>\n");
            out.write("    </Icon></IconStyle></Style>\n");
            out.write("    <Style id=\"normalPlacemark\"><IconStyle><Icon>\n");
            out.write("      <href>http://maps.google.com/mapfiles/kml/paddle/blu-blank.png</href>\n");
            out.write("    </Icon></IconStyle></Style>\n");
            out.write("    <StyleMap id=\"varsStyleMap\">\n");
            out.write("      <Pair><key>normal</key><styleUrl>#normalPlacemark</styleUrl></Pair>\n");
            out.write("      <Pair><key>highlight</key><styleUrl>#highlightPlacemark</styleUrl></Pair>\n");
            out.write("    </StyleMap>\n");
            out.write("    <Style id=\"highlightPhotoPlacemark\"><IconStyle><Icon>\n");
            out.write("      <href>http://maps.google.com/mapfiles/kml/paddle/purple-square.png</href>\n");
            out.write("    </Icon></IconStyle></Style>\n");
            out.write("    <Style id=\"normalPhotoPlacemark\"><IconStyle><Icon>\n");
            out.write("      <href>http://maps.google.com/mapfiles/kml/paddle/blu-square.png</href>\n");
            out.write("    </Icon></IconStyle></Style>\n");
            out.write("    <StyleMap id=\"varsPhotoStyleMap\">\n");
            out.write("      <Pair><key>normal</key><styleUrl>#normalPhotoPlacemark</styleUrl></Pair>\n");
            out.write("      <Pair><key>highlight</key><styleUrl>#highlightPhotoPlacemark</styleUrl></Pair>\n");
            out.write("    </StyleMap>\n");

            // Write Placemarks
            try {
                queryResultsToPlacemarks(out);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            // Write footer
            out.write("  </Document>\n");
            out.write("</kml>\n");
            out.close();

        }
        catch (IOException e) {
            log.error("Unable to save to " + file.getAbsolutePath(), e);
        }
    }

    /**
     * Format query results as KML placemarks
     * @param out The writer to write the KML markup out to
     */
    @SuppressWarnings("unchecked")
    private void queryResultsToPlacemarks(Writer out) throws IOException {
        StringBuilder sb = new StringBuilder();
        Map<String, ?> resultsMap = queryResults.copyData();
        Set<String> keys = new TreeSet<String>(resultsMap.keySet());

        // We need to be able to search using case-insensitive keys
        Map<String, String> lowerCaseKeys = new TreeMap<String, String>();
        for (String key : keys) {
            lowerCaseKeys.put(key.toLowerCase(), key);
        }

        // Find Latitude key
        if (lowerCaseKeys.containsKey(KEY_LATITUDE) && lowerCaseKeys.containsKey(KEY_LONGITUDE) &&
                lowerCaseKeys.containsKey(KEY_CONCEPTNAME)) {

            // Fetch the lists of the key values
            List<String> conceptNames = (List<String>) resultsMap.get(lowerCaseKeys.get(KEY_CONCEPTNAME));
            List<Number> latitudes = (List<Number>) resultsMap.get(lowerCaseKeys.get(KEY_LATITUDE));
            List<Number> longitudes = (List<Number>) resultsMap.get(lowerCaseKeys.get(KEY_LONGITUDE));
            List<Date> recordedDates = (List<Date>) resultsMap.get(lowerCaseKeys.get(KEY_RECORDEDDATE));
            List<Number> depths = (List<Number>) resultsMap.get(lowerCaseKeys.get(KEY_DEPTH));

            // Remove those keys 
            lowerCaseKeys.remove(KEY_CONCEPTNAME);
            lowerCaseKeys.remove(KEY_LATITUDE);
            lowerCaseKeys.remove(KEY_LONGITUDE);

            for (int i = 0; i < conceptNames.size(); i++) {
                String styleUrl = "#varsStyleMap";
                String conceptName = conceptNames.get(i);

                sb.append("    <Placemark>\n");    // START PLACEMARK
                sb.append("      <name>").append(conceptName).append("</name>\n");

                sb.append("      <description><![CDATA[\n");    // START DESCRIPTION

                /*
                 * The descrition content needs to be done generically.
                 * 1) Retrieve keys
                 * 2) Remove keys we've already dealt with (lat, long, conceptname)
                 * 3) Iterate throught the keys
                 * 4) If the key does not contain any content then skip it for that record
                 * 5) Write out the details as a list item. If it starts with HTTP
                 *      then put it in it's own div and create a link to it.
                 */
                for (String key : lowerCaseKeys.keySet()) {
                    Object value = ((List) resultsMap.get(lowerCaseKeys.get(key))).get(i);
                    if (value != null) {
                        
                        if (!key.endsWith("id_fk")) {
                            String valueString = (value instanceof Date) ? dateFormatISO.format(value) : value.toString();
                            sb.append("        <div>");
                            if (valueString.toLowerCase().startsWith("http://")) {
                                sb.append("<img src=\"").append(valueString).append("\" />");
                                styleUrl = "#varsPhotoStyleMap";
                            }
                            else {
                                sb.append(lowerCaseKeys.get(key)).append(": ").append(valueString);
                            }

                            sb.append("</div>\n");
                        }
                    }
                }

                sb.append("      ]]></description>\n");         // END DESCRIPTION
                sb.append("      <styleUrl>").append(styleUrl).append("</styleUrl>\n");

                // WRITE POSITION
                try {
                    Number latitude = latitudes.get(i);
                    Number longitude = longitudes.get(i);
                    Number depth = (depths == null || depths.get(i) == null) ? 0 : -depths.get(i).floatValue();
                    if ((latitude != null) && (longitude != null)) {
                        sb.append("      <Point><altitudeMode>absolute</altitudeMode><coordinates>").append(longitude).append(",");
                        sb.append(latitude).append(",").append(depth).append("</coordinates></Point>\n");
                    }
                }
                catch (Exception e) {
                    log.warn("Failed to extract position from ResultSet", e);
                }

                // Add TimeStamp
                try {
                    Date recordedDate = recordedDates.get(i);
                    if (recordedDate != null) {
                        sb.append("      <TimeStamp>");
                        sb.append("<when>").append(dateFormatISO.format(recordedDate)).append("</when>");
                        sb.append("</TimeStamp>\n");
                    }
                }
                catch (Exception e) {
                    log.warn("Failed to extract recorded date from ResultSet", e);
                }

                sb.append("    </Placemark>\n");    // END PLACEMARK
                out.write(sb.toString());
                sb.delete(0, sb.length());
            }

        }


    }
}
