package gov.noaa.olympiccoast

import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat

/**
 * 
 * @author Brian Schlining
 * @since 2011-01-24
 */
class NavigationReader {

    static final log = LoggerFactory.getLogger(NavigationReader.class)
    static final dateFormat = new SimpleDateFormat('yyyy-MM-dd hh:mm:ss')
    static final dateFormat2 = new SimpleDateFormat('M/dd/yyyy hh:mm:ss')

    static read(File file) {
        def data = []
        file.eachLine { line ->
            try {
                data << parse(line)
            }
            catch (Exception e) {
                log.warn("Failed to parse line: '${line}'")
            }
        }
    }

    static parse(String line) {
        def p = line.split(",")
        def date = stringToDate(p[4])
        if (!date) {
            throw new RuntimeException("${p[4]} is not a valid date")   
        }
        def depth = p[1] as Float
        depth = (depth > 0) ? depth : -depth
        return new NavigationDatum(
                p[0] as int,
                depth,
                p[2] as Double,
                p[3] as Double,
                date,
                stringToFloat(p[5]),
                stringToFloat(p[6]),
                stringToFloat(p[7]),
                stringToFloat(p[8]),
                stringToFloat(p[9])
        );
    }
    
    static stringToFloat(String s) {
        Float f = null
        try {
            f = Float.parseFloat(s)   
        }
        catch (Exception e) {
            // Do nothing. Value will be null   
        }
        return f
    }
    
    static stringToDate(String s) {
        s = s.replace('"', '')
        def date = null
        if (s.contains('/')) {
            date = dateFormat2.parse(s)
        }
        else {
            date = dateFormat.parse(s)   
        }
    }
}
