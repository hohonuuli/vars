import java.text.SimpleDateFormat
import vars.annotation.DateAdjuster

/*
 This script will set the recorded date of each annotated video frame using
 the date you provide (for year, month, day) and the timecode (for hour, minute, second)

 Usage: adjust_timecode [videoArchiveName] [date]
 Arguments:
    videoArchiveName = The video name
    date = date as yyyy-MM-dd (e.g. 2015-02-23)
*/

if (args.size() != 2) {
  println("""
  |  This script will set the recorded date of each annotated video frame using
  | the date you provide (for year, month, day) and the timecode (for hour, minute, second)
  |
  | Usage: adjust_timecode [videoArchiveName] [date]
  | Arguments:
  |   videoArchiveName = The video name
  |   date = date as yyyy-MM-dd (e.g. 2015-02-23)
  """.stripMargin('|'))
  return
}

def videoArchiveName = args[0]
def df = new SimpleDateFormat('yyyy-MM-dd')
def date = df.parse(args[1])

def dateAdjuster = new DateAdjuster()
dateAdjuster.adjust(videoArchiveName, date)