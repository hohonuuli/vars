package uk.ac.ox.zoo

import org.slf4j.LoggerFactory

/**
 *
 * @author Brian Schlining
 * @since 2012-09-06
 */
class CSVReader {


    static final HEADERS_OF_INTEREST = ["recordedDate":"Timestamp",
            "depth":"ROV.RovDepth",
            "temperature":"CTD.Temperature",
            "salinity":"CTD.Salinity",
            "logDate":"JCUSBL.Date",
            "latitude":"JCUSBL.Lat",
            "latNS":"JCUSBL.LatN",
            "longitude":"JCUSBL.Lon",
            "latEW":"JCUSBL.LonW"]

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

            if (m) {
                try {
                    data << parse(line, m)
                }
                catch (Exception e) {
                    log.warn("Failed to parse line: '${line}'")
                }
            }
        }
        return data

    }

    /**
     * Parse the header row to find the index of the columns of interest
     * @param line
     * @return
     */
    static parseHeader(String line) {
        def p = line.split(",")
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
        return m
    }


    static parse(String line, Map m) {
        def p = line.split(",")
        def datum = new CSVDatum()
        m.each { k, v ->
            datum[k] = p[v]
        }
        return datum
    }
}
