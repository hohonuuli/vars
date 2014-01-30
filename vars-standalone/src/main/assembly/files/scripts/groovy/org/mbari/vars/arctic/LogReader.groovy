package org.mbari.vars.arctic

import org.slf4j.LoggerFactory

/**
 * Created by brian on 1/29/14.
 */
class LogReader {

    private static final latLonParser = { ddmm ->
        def degrees = ddmm[0..1] as double
        def minutes = ddmm[2..-1] as double
        return degrees + minutes / 60.0
    }

    private static final doubleParser = { String s ->
        def d = null
        try {
            d = s as double
        }
        catch (Exception e) {
            // do nothing, not a valid double
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
        def firstBomb = true
        file.eachLine { line ->
            if (line.startsWith("#")) {
                // ignore header lines. They're wrong anyway.
            }
            else {
                try {
                    def p = line.split(",")
                    //def conductivity = parseDouble(p[1])
                    //def temperature = parseDouble(p[2])
                    def depth = parseDouble(p[3])
                    def time = p[4]
                    def latitude = latLonParser(p[6])
                    def longitude = latLonParser(p[7])
                    def logRecord = new LogRecord(conductivity, depth, latitude, longitude, temperature,
                            time)
                    logRecords << logRecord
                    
                }
                catch (Exception e) {
                    if (firstBomb) {
                        log.debug("Unable to parse: $line", e)
                        firstBomb = false
                    }
                    
                }
            }
        }
        return logRecords
    }

}
