package uk.ac.ox.zoo

import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat

/**
 *
 * @author Brian Schlining
 * @since 2012-09-06
 */
class CSVReader {

    /**
     * Map a field in VARS to a Header in the CSV file
     */
    static final HEADERS_OF_INTEREST = ["recordedDate":"Timestamp",
            "altitude":"ROV.RovAlt",
            "depth":"ROV.RovDepth",
            "temperature":"CTD.Temperature",
            "salinity":"CTD.Salinity",
            "logDate":"JCUSBL.Date",
            "latitude":"JCUSBL.Lat",
            "latNS":"JCUSBL.LatN",
            "longitude":"JCUSBL.Lon",
            "lonEW":"JCUSBL.LonW",
            "x":"ROV.DvlWaterXVel",
            "y":"ROV.DvlWaterYVel",
            "z":"ROV.DvlWaterZVel"]

    // --- ALL parsers except only strings as arguments

    /**
     * Parsers the timestamp
     */
    private static final timestampParser = new SimpleDateFormat('dd.MM.yyyy HH:mm:ss')
    static {
        timestampParser.timeZone = TimeZone.getTimeZone('UTC')
    }

    /**
     * Parse latitude or longitude
     * @param latLon The lat or lon string formatted like ddmm.mmmmm (e.g. 3242.53822)
     * @param latNLon@ Hemisphere. N or S for latitude. E or W for longitude
     */
    private static final latLonParser = {latLon, latNlonE ->
        // Split lat into degrees and minutes, e.g. 3242.53822
        def i = latLon.indexOf('.')
        def degrees = latLon[0..i - 3] as Double
        def minutes = latLon[i - 2..-1] as Double
        // Get sign from latNlonE
        def sign = (latNlonE.equalsIgnoreCase('N') || latNlonE.equalsIgnoreCase('E')) ? 1 : -1
        return sign * (degrees + minutes / 60D)
    }

    /**
     * Fallback parser. Try to parse everything as double value
     */
    private static final doubleParser = { s ->
        def d = null
        try {
            d = s as Double
        }
        catch (Exception e) {
            // Do nothing. Not a valid double
        }
        return d
    }

    /**
     * Special parsers. If no match is found in this list, the code falls back to using the
     * doubleParser
     */
    static final PARSERS = ["recordedDate": {ds -> timestampParser.parse(ds)},
            "latitude": latLonParser,
            "longitude": latLonParser]

    static final log = LoggerFactory.getLogger(CSVReader.class)

    static read(File file) {
        def isHeader = true
        def data = []
        def m = null
        file.eachLine { line ->

            if (isHeader) {
                m = parseHeader(line)
                isHeader = false
            }
            else if (m) {
                try {
                    data << parse(line, m)
                }
                catch (Exception e) {
                    // Do nothing ... bogus line
                    //log.warn("Failed to parse line: '${line}'")
                }
            }
        }
        return data

    }

    /**
     * Parse the header row to find the index of the columns of interest
     * @param line
     * @return A map of [headerfield as String, column index]. For Example:
     *  [recordedDate:0, depth:1, temperature:5, salinity:6, logDate:9, latitude:11,
     *  latNS:12, longitude:13, latEW:14]
     */
    static parseHeader(String line) {
        def p = line.split(",") as List
        def m = [:]
        HEADERS_OF_INTEREST.each { k, v ->
            def i = p.indexOf(v)
            if (i > -1) {
                m[k] = i
            }
            else {
                log.warn("Unable to find the '${v}' column in the header")
            }
        }
        log.info("Results of parsing the header:\n${m.toString()}")
        return m
    }


    static parse(String line, Map m) {
        def p = line.split(",")
        def datum = new CSVDatum()
        m.each { k, v ->
            def s = p[v].trim()
            def parser = PARSERS.get(k, doubleParser)

            try {
                if (k.equals('latitude')) {
                    def j = m['latNS']
                    def ns = p[j]
                    datum[k] = parser(s, ns)

                }
                else if (k.equals('longitude')) {
                    def j = m['lonEW']
                    def ew = p[j]
                    datum[k] = parser(s, ew)
                }
                else {
                    datum[k] = parser(s)
                }
            }
            catch (Exception e) {
                // Do nothing
            }

        }
        return datum
    }
}
