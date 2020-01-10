package org.mbari.vars.arctic

import java.io.File
import java.util.{Date, TimeZone}

import org.slf4j.LoggerFactory
import vars.shared.ui.GlobalStateLookup


/**
 *
 *
 * @author Brian Schlining
 * @since 2015-04-20T10:59:00
 */
object MergeVARS692b {

  private[this] val log = LoggerFactory.getLogger(getClass)

  def apply(videoArchiveName: String, logFile: File, startYear: Int): Unit = {
    val raw = SpecialShipLogReader2(logFile, startYear)
    val full = MergeUtilities.toFullLogRecords(raw)
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
          |  MergeVARS692b [videoArchiveName] [logFile] [year]
          |
          | Inputs:
          |  videoArchiveName: The name, as stored in VARS, of the video archive
          |     to merge
          |
          |  logFile: The full path to the log file to use for the merge
          |
          |  year: The start date of the video in yyyy
        """.stripMargin)
      return
    }
    val videoArchiveName = args(0)
    val logFile = new File(args(1))
    val year = args(2).toInt

    apply(videoArchiveName, logFile, year)
  }

}
