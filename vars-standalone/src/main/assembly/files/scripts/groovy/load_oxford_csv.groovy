import uk.ac.ox.zoo.MergeData
/**
 * @author Brian Schlining
 * @since 2012-09-10
 */

if (args.size() != 2) {
    println("""

        This script loads position and CTD information from a CSV file into 
        the VARS database. The CSV info is merged using nearest-neighbor
        interpolation with the observations for a given tape or video-file.

        Usage:
            gsh load_oxford_csv <videoname> <csvfile>

        Inputs:
            videoname = The name of the video archive (either a tape or a video-file)
                        as it appears in VARS
            csvfile = The path and name to the csv file to be loaded.

    """.stripIndent())
    return
}

def videoArchiveName = args[0]
def file = new File(args[1])
def loader = new MergeData(videoArchiveName, file)
loader.apply()

