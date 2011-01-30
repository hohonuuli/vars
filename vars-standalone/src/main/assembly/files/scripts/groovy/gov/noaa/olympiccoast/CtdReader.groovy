package gov.noaa.olympiccoast

import org.mbari.util.DateConverter
import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat

/**
 * 
 * @author Brian Schlining
 * @since 2011-01-24
 */
class CtdReader {

    static final log = LoggerFactory.getLogger(CtdReader.class)
    

    static read(File file, int year) {
        def data = []
        file.eachLine { line ->
            try {
                
                data << parse(line)
            }
            catch (Exception e) {
                log.warn("Failed to parse line: '${line}'")
            }
        }
        
        // Convert 'Julian day' (actually day of year)
        def calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"))
        data.each { ctdDatum ->
            ctdDatum.date = DateConverter.doyToDate(ctdDatum.julianDay, year)
        }
    }

    static parse(String line) {
        def p = line.split(" ").findAll { it.size() > 0 }
        def depth = p[0] as Float
        depth = (depth > 0) ? depth : -depth
        return new CtdDatum(
                depth,
                stringToFloat(p[1]),
                stringToFloat(p[2]),
                stringToFloat(p[3]),
                stringToFloat(p[4]),
                p[5] as Double,
                stringToFloat(p[6])
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
    
}
