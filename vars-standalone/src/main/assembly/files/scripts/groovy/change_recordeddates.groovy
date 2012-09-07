import java.text.SimpleDateFormat
import vars.annotation.DateAdjuster

/**
 * @author Brian Schlining
 * @since 2012-09-06
 */
if (args.size() != 2) {
    println("""
        This script will change the date of all annotations in a video archive. Only the date
        portion (year, month, day) will be changed. The time (hour, minute, second) will not be
        modified. This allows users who are pulling the correct time of off a timecode track to
        asjust the date of their annotations after they've completed annotating a video.

        Usage:
            gsh change_recordedates <videoarchivename> <date>

        Inputs:
            videoarchivename = The name of your videoarchive. eg. K1234-01
            date = The correct date of all annotations in the videoarchive. It must be formated
                   like as YYYY-MM-DD, e.g. 2012-09-22
    """.stripIndent())
    return
}

def dateFormat = new SimpleDateFormat("yyyy-MM-dd");
dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))

println("Parsing your arguments of: \n\tvideoarchivename = ${args[0]}\n\tdate = ${args[1]}")
def videoArchiveName = args[0]
def date = dateFormat.parse(args[1])

println("Starting date adjustment")
def dateAdjuster = new DateAdjuster()
dateAdjuster.adjust(videoArchiveName, date)
println"Date adjustment for ${videoArchiveName} is completed"