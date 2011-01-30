package gov.noaa.olympiccoast

import org.mbari.util.DateConverter
import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat

/**
 * 
 * @author Brian Schlining
 * @since 2011-01-24
 */
class ObsReader {

    static final log = LoggerFactory.getLogger(ObsReader.class)
    static final dateFormat = new SimpleDateFormat('yyyy-MM-dd hh:mm:ss')
    static {
        dateFormat.timeZone = TimeZone.getTimeZone('UTC')   
    } 
    static final timecodeRegex = /^\d\d:\d\d:\d\d:\d\d$/
    
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
        def p = line.replace('"', '').split(',')
        def date = dateFormat.parse(p[0])
        
        // Some rows hav mulitple comma delimited concept names
        // this really mucks up things. We check this by verifying that the
        // parsed timecode is really a timecode
        def timecode = p[3].replace(' ', ':')
        if (! (timecode =~ timecodeRegex)) {
            throw new RuntimeException("Bad timecode: ${timecode}")   
        }
        
        return new ObsDatum(
                date,
                p[1] as Integer,
                p[2],
                timecode,
                p[4]
        );
    }
   
    
}
