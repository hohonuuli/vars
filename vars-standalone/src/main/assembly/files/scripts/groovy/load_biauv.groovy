import org.mbari.biauv.integration.IOUtilities
import org.mbari.vars.biauv.AUVLoader
import vars.annotation.ImageUtility
import org.mbari.biauv.integration.*
/**
 * 
 * @author Brian Schlining
 * @since Sep 7, 2010
 */

/**
 * Example:
 * gsh ../scripts/groovy/load_biauv.groovy \
 *  /Volumes/AUVBI/missionlogs/2009/2009173/2009.173.03 \
 *  /Volumes/AUVBI/missionlogs/2009/2009173/Images \
 *  http://ssds.shore.mbari.org/auvbi/missionlogs/2009/2009173/Images
 */

/* --------------------------------------------------------------------
    Parse arguments
 */
 if (args.size() != 3) {
     println("""\
            Usage: load_biauv missionDirectory missionWebMapping imageDirectory imageWebMapping

            Arguments:
                missionDirectory = The directory containing the AUV mission logs
                imageDirectory = The name of the directory containing the images to load
                    (this script does NOT recurse through subdirectories)
                imageWebMapping = The directory on a web server that maps to imageDirectory

            Example:

            """.stripIndent())
     return
 }
def missionDirectory = new File(args[0])
def imageDirectory = new File(args[1])
def webMapping = args[2]
webMapping = webMapping.endsWith("/") ? webMapping : webMapping + "/"


/* --------------------------------------------------------------------
    Extract the data needed for the merge from the AUV logs
 */
println("Loading mission data from logs")
def cameraLog = new File(missionDirectory, "camera.log")
def navigationLog = new File(missionDirectory, "navigation.log")
def mergeData = new ArrayList(MergeData.toMergeDatumList(IOUtilities.extractLogRecords(navigationLog, cameraLog)))


/* --------------------------------------------------------------------
    Create a Map<Date, URL> where the date is the GPS date/time that
    the image was taken and the file is the corresponding image
 */
println("Extracting GPS timestamp from images")
def imageMap = [:]
def images = imageDirectory.listFiles({d, name ->
        name = name.toLowerCase();
        name.endsWith(".jpeg") || name.endsWith(".jpg") } as FilenameFilter)
images.each { File file ->
    def url = new URL("${webMapping}${file.name}")
    try {
        def date = ImageUtility.extractGPSTimeFromImage(file)
        imageMap[date] = url
        println("$date --> $url")
    }
    catch (Exception e) {
        println("\t--- Unable to parse GPS time from $url")
    }
     
    
}

/* --------------------------------------------------------------------
    Load data into VARS
 */

// --- Make a name
def platform = 'Benthic Imaging AUV'
def diveNumber = missionDirectory.name                  // 2009.173.01
def sequenceNumber = diveNumber.replace(".", "") as int // 200917301

def loader = new AUVLoader()
loader.load("biauv-${diveNumber}" as String, platform, sequenceNumber, imageMap, mergeData)


