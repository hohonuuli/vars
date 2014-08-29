import org.mbari.vars.arctic.*
import vars.annotation.VideofileDateAdjuster

import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

if (args.size() < 2 || args.size() > 3) {
    println("""
        This script loads position and temperature information from a CSV file
        into the VARS database. The CSV info is merged using nearest-neighbor
        interpolation with the observations for a given tape or video-file.

        Usage:

            To merge using timecode track ...
            gsh load_arctic_csv [videoArchiveName] [csvfile]

            To merge using elapsed time ...
            gsh load_arctic_csv [videoArchiveName] [csvfile] [videoStartDate]

        Inputs:
            videoArchiveName = The name, in VARS, used to identify the movie. 
                For most video files this will be a path to the file.

            csvfile = The path and name to the csv file to be loaded.

            videoStartDate = The UTC date and time that the video starts in ISO8601
                format (yyyy-mm-ddTHH:MM:ssZ). e.g. 2013-09-27T11:21:54Z. Note that the
                seperating 'T' and terminating 'Z' are required. As an alternative to the Z,
                which indicates the UTC timezone, you can use time offset:
                2013-09-27T11:21:54-07:00

        """.stripIndent())
    return
}

def videoArchiveName = args[0]
def csvfile = new File(args[1])

if (args.size() == 2) {  // Merge using timecode
    def loader = new MergeData(videoArchiveName, csvfile)
    loader.apply()
}
else {                   // Merge using recordedDate

    // --- Step 1: Adjust the recordedDates in the VideoArchive based on the
    // start date and elapsed time
    def df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
    df.setTimeZone(TimeZone.getTimeZone("UTC"))
    def startDate = df.parse(args[2])

    def dateAdjuster = new VideofileDateAdjuster()
    dateAdjuster.adjust(videoArchiveName, startDate)

    // --- Step 2: Merge

    def loader = new MergeDataByDate(videoArchiveName, csvfile)
    loader.apply()


}


