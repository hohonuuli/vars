import java.text.SimpleDateFormat
import vars.annotation.VideofileDateAdjuster

/*
 This script will set the recorded date of each annotated video frame using
 the date you provide (for year, month, day) and the timecode (for hour, minute, second) with
  framerate of 100 frames/sec (centi-secs). THis is the prefered framerate to use when
  adjust videofiles as they use runtime.

 Usage: adjust_recordeddate_videofile [videoArchiveName] [date]
 Arguments:
    videoArchiveName = The video name
    date = date as yyyy-MM-ddTHH:mm:ss (e.g. 2015-02-23T13:01:58) UTC
*/

if (args.size() != 2) {
    println("""
  |  This script will set the recorded date of each annotated video frame using
  | the date you provide (for year, month, day, hour, minute second) and the 
  | timecode (for hour, minute, second). So that recordedDate = date + timecode
  |
  | Usage: adjust_timecode [videoArchiveName] [date]
  | Arguments:
  |   videoArchiveName = The video name
  |   date = date as yyyy-MM-ddTHH:mm:ss (e.g. 2015-02-23T13:01:58) UTC. This 
  |          is the time of the first frame in your video.
  """.stripMargin('|'))
    return
}

def videoArchiveName = args[0]
def df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
df.setTimeZone(TimeZone.getTimeZone('UTC'))
def date = df.parse(args[1])

def dateAdjuster = new VideofileDateAdjuster()
dateAdjuster.adjust(videoArchiveName, date, 100)
