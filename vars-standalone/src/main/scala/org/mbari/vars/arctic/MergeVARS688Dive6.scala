package org.mbari.vars.arctic

import java.io.File
import java.text.SimpleDateFormat
import java.util.TimeZone

import org.slf4j.LoggerFactory
import vars.shared.ui.GlobalStateLookup

/**
 *
 *
 * @author Brian Schlining
 * @since 2015-03-02T14:15:00
 */
object MergeVARS688Dive6 {

  private[this] val log = LoggerFactory.getLogger(getClass)
  private[this] val dateFormat = new SimpleDateFormat("HH:mm:ss")

  def apply(videoArchiveName: String, logFile: File, year: Int): Unit = {
    val simple = SpecialCSVLogReader1(logFile, year)
    val full = MergeUtilities.toFullLogRecords(simple)
    val videoFrames = MergeVideoData.lookupVideoFrames(videoArchiveName).filter(_.getRecordedDate != null)

    if (log.isDebugEnabled) {
      val df = GlobalStateLookup.getUTCDateFormat

      val rs = full.sortBy(_.gpsDate.getTime)
      log.debug(s"Log spans ${df.format(rs.head.gpsDate)} to ${df.format(rs.last.gpsDate)}")

      val vs = videoFrames.toSeq.sortBy(_.getRecordedDate.getTime)
      log.debug(s"Annotations span ${df.format(vs.head.getRecordedDate)} to ${df.format(vs.last.getRecordedDate)}")
    }

    val merge = new MergeVideoData(videoFrames, full)(MergeVideoData.toolbelt)
    merge.update(merge.collate())

  }

  def main(args: Array[String]) {
    /**
     * We like to do all database transaction in the UTC timezone
     */
    System.setProperty("user.timezone", "UTC")
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))

    if (args.size != 3) {
      println(
        """
          | Usage:
          |  MergeVARS688Dive6 [videoArchiveName] [logFile] [year]
          |
          | Inputs:
          |  videoArchiveName: The name, as stored in VARS, of the video archive
          |     to merge
          |
          |  logFile: The full path to the log file to use for the merge
          |
          |  year: The year of the data within the logfile
        """.stripMargin)
      return
    }
    val videoArchiveName = args(0)
    val logFile = new File(args(1))
    val year = args(2).toInt
    apply(videoArchiveName, logFile, year)

  }
}

