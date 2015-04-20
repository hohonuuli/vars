import java.text.SimpleDateFormat
import vars.annotation.VideofileDateAdjuster

/**
 Brian Schlining
 2015-02-23
*/

if (args.size() != 2) {
  println("""
  |  This script will set the recorded date of each annotated video frame using
  | 1) timecode (assumed to be runtime since the start of the video) and 
  | 2) a initial date that you provide
  |
  | Usage: adjust_recordeddate1 [videoArchiveName] [date]
  | Arguments:
  |  videoArchiveName = The video name
  |  date = date as yyyy-MM-ddTHH:mm:ss (e.g. 2015-02-23T13:01:58) UTC
  """.stripMargin('|'))
  return
}

def videoArchiveName = args[0]
def df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
df.setTimeZone(TimeZone.getTimeZone('UTC'))
def date = df.parse(args[1])

def dateAdjuster = new VideofileDateAdjuster()
dateAdjuster.adjust(videoArchiveName, date)