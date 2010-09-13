package vars.annotation

import org.apache.sanselan.Sanselan
import org.apache.sanselan.formats.tiff.TiffImageMetadata
import org.apache.sanselan.formats.tiff.constants.GPSTagConstants

/**
 * 
 * @author Brian Schlining
 * @since Sep 13, 2010
 */
class ImageUtility {

    private static final dateRegex = "([0-9]+):([0-9]+):([0-9]+)"
    private static final timeRegex = "([0-9]+). ([0-9]+). ([0-9]+)/([0-9]+)"

    /**
     * Extract the GPS time from an image
     * @param image The image file to read
     * @return A date object with the GPS time. null is returned if no
     *      GPS metadata is found
     */

    static extractGPSTimeFromImage(File image) {

        def metadata = Sanselan.getMetadata(image).exif   // TiffImageMetadata

        // --- Extract Date
        def dateItem = new TiffImageMetadata.Item(metadata.findField(GPSTagConstants.GPS_TAG_GPS_DATE_STAMP))
        def dateString = dateItem.text // '2009:06:22'
        def dateMatcher = (dateString =~ dateRegex)
        def y, month, d = null
        if (dateMatcher) {
             y = dateMatcher[0][1] as int
             month = dateMatcher[0][2] as int
             d = dateMatcher[0][3] as int
        }
        else {
            return null
        }

        // --- Extract Time
        def timeItem = new TiffImageMetadata.Item(metadata.findField(GPSTagConstants.GPS_TAG_GPS_TIME_STAMP))
        def timeString = timeItem.text // 17, 32, 1703/100 (17.03)
        def matcher = (timeString =~ timeRegex)
        def h, m, fs = null
        if (matcher) {
             h = matcher[0][1] as int
             m = matcher[0][2] as int
             fs = (matcher[0][3] as double) / (matcher[0][4] as double)
        }
        else {
            return null
        }
        def s = Math.floor(fs) as int
        def ms = ((fs - s) * 1000) as int

        // --- Build calendar
        def utcCalendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"))
        utcCalendar.with {
            clear()
            set(Calendar.YEAR, y)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, d)
            set(Calendar.HOUR, h)
            set(Calendar.MINUTE, m)
            set(Calendar.SECOND, s)
            set(Calendar.MILLISECOND, ms)
        }

        return utcCalendar.time
    }

}


