import org.mbari.biauv.integration.LogRecordReader
import org.mbari.biauv.integration.IOUtilities
import groovy.io.FileType
import org.apache.sanselan.Sanselan
import org.apache.sanselan.common.IImageMetadata
import org.apache.sanselan.formats.tiff.TiffImageMetadata
import org.apache.sanselan.formats.tiff.constants.GPSTagConstants
/**
 * 
 * @author Brian Schlining
 * @since Sep 7, 2010
 */

/**
 * Example:
 * gsh ../scripts/groovy/load_benthicrover.groovy \
 *   /Volumes/DigitalImages/DocRicketts/2009/docr84 \
 *   84 \
 *   http://search.mbari.org/ARCHIVE/digitalImages/DocRicketts/2009/docr84/
 */

 if (args.size() != 4) {
     println("""\
            Usage: load_biauv missionDirectory missionWebMapping imageDirectory imageWebMapping

            Arguments:
                missionDirectory = The directory containing the AUV mission logs
                missionWebMapping = The directory on a web serer that maps to missionDirectory
                imageDirectory = The name of the directory containing the images to load
                    (this script does NOT recurse through subdirectories)
                imageWebMapping = The directory on a web server that maps to imageDirectory

            Example:

            """.stripIndent())
     return
 }

def missionDirectory = new File(args[0])
def missionWebMapping = new URL(args[1])
def imageDirectory = new File(args[2])
def imageWebMapping = new URL(args[3])

def cameraLog = new File(missionDirectory, "camera.log")
def navigationLog = new File(missionDirectory, "navigation.log")

def mergeData = IOUtilities.extractLogRecords(navigationLog, cameraLog)

def images = imageDirectory.findAll { File file ->
    file.name.endsWith(".jpg")
}

def imageMap = [:]
def utcCalendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"))
def dateRegex = "([0-9]+):([0-9]+):([0-9]+)"
def timeRegex = "([0-9]+). ([0-9]+). ([0-9]+)/([0-9]+)"
images.each { File file ->
    // Extract GPS time and associate in a Map
    //        get all metadata stored in EXIF format (ie. from JPEG or TIFF).
//            org.w3c.dom.Node node = Sanselan.getMetadataObsolete(imageBytes);
    def metadata = Sanselan.getMetadata(file) // IImageMetadata and JpegImageMetadata
    def exifMetadata = metadata.exif // TiffImageMetadata
    def dateItem = new TiffImageMetadata.Item(exifMetadata.findField(GPSTagConstants.GPS_TAG_GPS_DATE_STAMP))
    def timeItem = new TiffImageMetadata.Item(exifMetadata.findField(GPSTagConstants.GPS_TAG_GPS_TIME_STAMP))

    def dateString = dateItem.text // '2009:06:22'
    def dateMatcher = (dateString =~ dateRegex)
    def y, month, d, h, m, fs = null
    if (dateMatcher) {
         y = dateMatcher[0][1] as int
         month = dateMatcher[0][2] as int
         d = dateMatcher[0][3] as int
    }
    def timeString = timeItem.text // 17, 32, 1703/100 (17.03)
    def matcher = (timeString =~ timeRegex)
    if (matcher) {
         h = matcher[0][1] as int
         m = matcher[0][2] as int
         fs = (matcher[0][3] as double) / (matcher[0][4] as double)
    }

    def s = Math.floor(s) as int
    def ms = (fs - s) as int

    utcCalendar.with {
        clear()
        set(Calendar.YEAR, y)
        set(Calendar.MONTH, m - 1)
        set(Calendar.DAY_OF_MONTH, d)
        set(Calendar.HOUR, h)
        set(Calendar.MINUTE, m)
        set(Calendar.SECOND, s)
        set(Calendar.MILLISECOND, ms)
    }

    imageMap[utcCalendar.time, file]
}

//def dir = new File(args[0])             // Path to the directory containing the benthic rover images
//def platform = "Benthic Rover"          // ROV Name
//def sequenceNumber = args[1] as Integer // Dive number
//def webMapping = args[2]                // URL that maps 'dir'
//webMapping = webMapping.endsWith("/") ? webMapping : webMapping + "/"
//
//def images = dir.listFiles({d, name ->
//    name = name.toLowerCase();
//    name.endsWith(".jpeg") || name.endsWith(".jpg") } as FilenameFilter)
//
//def urls = []
//for (image in images) {
//    urls << new URL("${webMapping}${image.name}")
//}
//
//println("Found the following images ...")
//urls.each {println it}
//def loader = new RoverLoader()
//loader.load(urls, platform, sequenceNumber)

