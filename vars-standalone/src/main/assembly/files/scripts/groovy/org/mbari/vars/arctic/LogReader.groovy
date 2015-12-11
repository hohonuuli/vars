package org.mbari.vars.arctic

import org.slf4j.LoggerFactory

import java.text.SimpleDateFormat

/**
 * Created by brian on 1/29/14.
 */
class LogReader {

    static parseLatLon(String ddmm) {
        def d = null
        try {

            def idx = ddmm.indexOf(".")
            def degrees = ddmm[0..(idx - 3)] as double
            def minutes = ddmm[(idx - 2)..-1] as double
            d = degrees + minutes / 60.0
        }
        catch (Exception e) {
            // do nothing
        }
        return d
    }


    static parseDouble(String s) {
        def d = null
        try {
            d = s as double
        }
        catch (Exception e) {
            // do nothing, not a valid double
        }
        return d
    }

    static final log = LoggerFactory.getLogger(LogReader.class)

    static read(File file) {
        def logRecords = []
        file.eachLine { line ->
            if (line.startsWith("#")) {
                // ignore header lines. They're wrong anyway.
            }
            else {
                try {
                    def p = line.split(",") as List
                    // Line gets parsed according to the number of fields in it
                    if (p.size() == 8) {
                        logRecords << readShortLine(p)
                    } else if (p.size() == 12) {
                        logRecords << readLongLine(p)
                    } else {
                        log.debug("Unable to parse: $line")
                    }
                }
                catch (Exception e) {
                    log.debug("Unable to parse: $line")
                }
            }
        }
        return logRecords
    }

    /**
     * $(HEADER),C,T,D,TIME(CTD),HDG,LAT,LONG, (I *think)
     * 0         1 2 3 4         5   6   7
     *
     * @param file
     * @return
     */
    private static LogRecord readShortLine(List<String> p) {
        def conductivity = parseDouble(p[1])
        def temperature = parseDouble(p[2])
        def depth = parseDouble(p[3])
        def time = p[4]
        def latitude = parseLatLon(p[6])
        def longitude = parseLatLon(p[7])
        return new LogRecord(conductivity, depth, latitude, longitude, temperature,
                time)
    }

    /**
     * $(HEADER),C,T,D,TIME(CTD),SAL,TIME(GPS),HDG,DEPTH,LAT,LONG,SPEED
     * 0         1 2 3 4         5   6         7   8     9   10   11
     *
     * @param file
     * @return
     */
    private static LogRecord readLongLine(List<String> p) {
        def conductivity = parseDouble(p[1])
        def temperature = parseDouble(p[2])
        def depth = parseDouble(p[3])
        def time = p[6]
        def latitude = parseLatLon(p[9])
        def longitude = parseLatLon(p[10])
        return new LogRecord(conductivity, depth, latitude, longitude, temperature,
                time)
    }

    /**
     * Extracts the date from the header of the Arctic ROV log files
     * @param file
     * @return
     */
    static readDate(File file) {
        def date = null
        BufferedReader reader = file.newReader()
        def line = reader.readLine()
        while (line) {
            if (line.startsWith("#Date")) {
                def p = line.split(" ").collect { it.trim() }
                def df = new SimpleDateFormat("MM/d/yyyy")
                df.setTimeZone(TimeZone.getTimeZone("UTC"))
                date = df.parse(p[-1])
                break;
            }
            line = reader.readLine()
        }
        reader.close()
        return date
    }

}
