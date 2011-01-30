package gov.noaa.olympiccoast

import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat

class HabitatReader {
    
    static final log = LoggerFactory.getLogger(NavigationReader.class)
    static final dateFormat = new SimpleDateFormat('M/dd/yyyy HH:mm:ss')
    static {
        dateFormat.timeZone = TimeZone.getTimeZone('UTC')   
    }
    
    
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
        return data
    }
    
    static parse(String line) {
        def p = split(line)
        def startDate = dateFormat.parse("${p[3]} ${p[8]}")
        def endDate = dateFormat.parse("${p[3]} ${p[9]}")
        return new HabitatDatum(
                p[1] as int, // diveNumber
                p[2],        // site
                p[4],        // clipName
                p[5],        // transect
                p[6],        // habitatCode
                p[7],        // surveyActivity
                startDate, 
                endDate,
                p[10]        // comments
        );
    }
    
    static split(line) {
        def values = []
        split(values, line  )
        return values
    }
    
    private static split(values, s) {
        def idx = s.indexOf('\t')
        //println(s + " ---- " + idx)
        if (idx == 0) {
            values << null
            if (s.size() > 1) {
                split(values, s[1..-1])   
            }
        }
        else if (idx > 0) {
            values << s[0..<idx]
            if (idx < s.size() - 2) {
                split(values, s[(idx + 1)..-1])  
            }
        }
        else {
            values << s[0..s.size()]
        }

    }
    
}
