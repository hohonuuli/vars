import org.mbari.vars.arctic.*

if (args.size() != 2) {
    println("""
        This script loads position and temperature information from a CSV file
        into the VARS database. The CSV info is merged using nearest-neighbor
        interpolation with the observations for a given tape or video-file.

        Usage:
            gsh load_arctic_csv [videoArchiveName] [csvfile]

        Inputs:
            videoArchiveName = The name, in VARS, used to identify the movie. 
                For most video files this will be a path to the file.

            csvfile = The path and name to the csv file to be loaded.

        """.stripIndent())
    return
}

def videoArchiveName = args[0]
def csvfile = new File(args[1])
def loader = new MergeData(videoArchiveName, csvfile)
loader.apply()