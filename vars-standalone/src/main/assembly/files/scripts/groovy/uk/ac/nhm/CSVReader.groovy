package uk.ac.nhm

import org.slf4j.LoggerFactory

import java.text.SimpleDateFormat

/**
 * Created by brian on 12/18/13.
 */
class CSVReader {

    static final log = LoggerFactory.getLogger(CSVReader.class)

    /**
     * Map a field in VARS to a Header in the CSV file
     */
    static final HEADERS_OF_INTEREST = [
            "date":"#Date",
            "time":"Time",
            "depth":"SUB1_USBL_Depth",
            "latitude":"SUB1_Lat",
            "longitude":"SUB1_Lon"]

    /**
     * Parsers the timestamp
     */
    private static final timestampParser = new SimpleDateFormat('MM/dd/yyyy HH:mm:ss')
    static {
        timestampParser.timeZone = TimeZone.getTimeZone('UTC')
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
            // e.printStackTrace()
        }
        return d
    }

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
                }
            }

        }
    }

    /**
     * Parse the header row to find the index of the columns of interest
     * @param line
     * @return A map of [headerfield as String, column index]. For Example:
     *  [recordedDate:0, depth:1, temperature:5, salinity:6, logDate:9, latitude:11,
     *  latNS:12, longitude:13, latEW:14]
     */
    static parseHeader(String line) {
        def p = line.split("\t") as List
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
        def p = line.split("\t")
        def datum = new CSVDatum()
        def date = null
        def time = null
        m.each { k, v ->
            def s = p[v].trim()

            try {
                if (k == "time") {
                    time = s
                }
                else if (k == "date") {
                    date = s
                }
                else {
                    datum[k] = doubleParser(s)
                }
            }
            catch (Exception e) {
                // Do nothing
                //e.printStackTrace()
            }
        }
        if (date && time) {
            datum.recordedDate = timestampParser.parse("${date} ${time}")
        }
        return datum
    }


}
