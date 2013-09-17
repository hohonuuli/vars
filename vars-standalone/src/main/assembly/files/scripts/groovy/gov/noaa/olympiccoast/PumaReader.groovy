package gov.noaa.olympiccoast

import java.io.File
import org.slf4j.LoggerFactory

class PumaReader {

    private static final calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"))
    private static final log = LoggerFactory.getLogger(PumaReader.class)

    static final HEADERS_OF_INTEREST = ["elapsedTime":"log frame time (s)", 
            "altitude":"0x31 - Downlink Data (High Rate) - avx current altitude (m)",
            "heading":"0x31 - Downlink Data (High Rate) - avx mag heading (deg M)",
            "year":"0x33 - GPS Data - avx gps year (YYYY)",
            "month":"0x33 - GPS Data - avx gps month (1 - 12)",
            "day":"0x33 - GPS Data - avx gps day (1 - 31)",
            "hour":"0x33 - GPS Data - avx gps hour (0 - 23)",
            "minute":"0x33 - GPS Data - avx gps minute (1 - 60)",
            "second":"0x33 - GPS Data - avx gps second (1 - 60)",
            "latitude":"0x33 - GPS Data - avx gps latitude (deg)",
            "longitude":"0x33 - GPS Data - avx gps longitude (deg)"]

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
                    log.warn("Failed to parse line:\n\t${line}")
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
        def p = line.split('\t')
        def elapsedTime = p[m["elapsedTime"]] as Double
        def altitude = p[m["altitude"]] as Double
        def heading = p[m["heading"]] as Double
        def latitude = p[m["latitude"]] as Double
        def longitude = p[m["longitude"]] as Double
        def year  = p[m["year"]] as Integer
        def month = p[m["month"]] as Integer
        def day = p[m["day"]] as Integer
        def hour = p[m["hour"]] as Integer
        def minute = p[m["minute"]] as Integer
        def second = p[m["second"]] as Integer

        calendar.set(year, month - 1, day, hour, minute, second)
        def date = calendar.time
        return new PumaDatum(elapsedTime, date, altitude, heading, latitude, longitude)

    }

}
