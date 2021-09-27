package vars.query.results;


import mbarix4j.util.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * @author Brian Schlining
 * @since 2015-08-11T14:30:00
 */
public class SaveResultsAsKMLFn {

    private static final String KEY_CONCEPTNAME = "conceptname";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_RECORDEDDATE = "recordeddate";
    private static final String KEY_DEPTH = "depth";
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final File target;
    private final Optional<String> sql;
    final DateFormat dateFormatISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'") {{
        setTimeZone(TimeZone.getTimeZone("UTC"));
    }};

    private final QueryResults queryResults;
    private final Executor executor;

    public SaveResultsAsKMLFn(Executor executor, File target, QueryResults queryResults, Optional<String> sql) {
        this.executor = executor;
        this.target = target;
        this.queryResults = queryResults;
        this.sql = sql;
    }

    public void apply()  {
        executor.execute(() -> {

            try {
                final BufferedWriter out = new BufferedWriter(new FileWriter(target));

                // Write header
                out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                out.write("<!-- " + new Date() + " -->");
                sql.ifPresent(s -> {
                    try {
                        out.write("<!-- QUERY: " + s + " -->");
                    }
                    catch (IOException e) {
                        // We can swallow this without significant consequence
                    }
                });
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
                queryResultsToPlacemarks(out);

                // Write footer
                out.write("  </Document>\n");
                out.write("</kml>\n");
                out.close();
            }
            catch (IOException e) {
                log.warn("Failed to save KML to " +target.getAbsolutePath());
            }

        });

    }

    /**
     * Format query results as KML placemarks
     * @param out The writer to write the KML markup out to
     */
    @SuppressWarnings("unchecked")
    private void queryResultsToPlacemarks(Writer out) throws IOException {
        StringBuilder sb = new StringBuilder();
        final Tuple2<List<String>, List<String[]>> rowData = queryResults.toRowOrientedData();

        // We need to be able to search using case-insensitive keys
        List<String> keys = rowData.getA();
        List<String> lowerCaseKeys = keys.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());


        // Find Latitude key
        if (lowerCaseKeys.contains(KEY_LATITUDE) && lowerCaseKeys.contains(KEY_LONGITUDE) &&
                lowerCaseKeys.contains(KEY_CONCEPTNAME)) {

            int conceptNameIdx = lowerCaseKeys.indexOf(KEY_CONCEPTNAME);
            int latitudeIdx = lowerCaseKeys.indexOf(KEY_LATITUDE);
            int longitudeIdx = lowerCaseKeys.indexOf(KEY_LONGITUDE);
            int depthIdx = lowerCaseKeys.indexOf(KEY_DEPTH);
            int recordedDateIdx = lowerCaseKeys.indexOf(KEY_RECORDEDDATE);

            List<String[]> rows = rowData.getB();
            for (int r = 0; r < rows.size(); r++) {

                String[] row = rows.get(r);
                String conceptName = row[conceptNameIdx];
                String latitudeS = latitudeIdx > -1 ? row[latitudeIdx] : "";
                String longitudeS = longitudeIdx > -1 ? row[longitudeIdx] : "";
                String depthS = depthIdx > -1 ? row[depthIdx] : "";
                String recordedDateS = recordedDateIdx > -1 ? row[recordedDateIdx] : null;

                String styleUrl = "#varsStyleMap";
                sb.append("    <Placemark>\n")    // START PLACEMARK
                        .append("      <name>").append(conceptName).append("</name>\n")
                        .append("      <description><![CDATA[\n");    // START DESCRIPTION

                for (int c = 0; c < row.length; c++) {
                    String key = keys.get(c);
                    String lowerKey = lowerCaseKeys.get(c);
                    if (c != conceptNameIdx && c != latitudeIdx && c != longitudeIdx && !lowerKey.endsWith("id_fk")) {
                        String value = row[c];
                        sb.append("        <div>");
                        if (value.toLowerCase().startsWith("http://")) {
                            sb.append("<img src=\"").append(value).append("\" />");
                            styleUrl = "#varsPhotoStyleMap";
                        }
                        else {
                            sb.append(key).append(": ").append(value);
                        }

                        sb.append("</div>\n");
                    }
                }
                sb.append("      ]]></description>\n");         // END DESCRIPTION
                sb.append("      <styleUrl>").append(styleUrl).append("</styleUrl>\n");

                // WRITE POSITION
                Number latitude = latitudeS.isEmpty() ? null : Double.parseDouble(latitudeS);
                Number longitude = longitudeS.isEmpty() ? null : Double.parseDouble(longitudeS);
                Number depth = depthS.isEmpty() ? 0D : Double.parseDouble(depthS) * -1;
                if ((latitude != null) && (longitude != null)) {
                    sb.append("      <Point><altitudeMode>absolute</altitudeMode><coordinates>")
                            .append(longitude).append(",")
                            .append(latitude).append(",")
                            .append(depth)
                            .append("</coordinates></Point>\n");
                }

                // Add TimeStamp
                if (recordedDateS != null) {
                    sb.append("      <TimeStamp>");
                    sb.append("<when>").append(recordedDateS).append("</when>");
                    sb.append("</TimeStamp>\n");
                }

                sb.append("    </Placemark>\n");    // END PLACEMARK
                out.write(sb.toString());
                sb.delete(0, sb.length());
            }

        }


    }
}
