package org.mbari.vars.arctic

import java.io.File
import java.util.{TimeZone, Date}

import org.slf4j.LoggerFactory
import vars.shared.ui.GlobalLookup

import scala.util.{Failure, Success, Try}

/**
 *
 *
 * @author Brian Schlining
 * @since 2015-03-16T10:59:00
 */
object MergeVARS692 {

  private[this] val log = LoggerFactory.getLogger(getClass)

  def apply(videoArchiveName: String, logFile: File, startDate: Date): Unit = {
    val raw = SpecialCSVLogReader2(logFile)
    val full = MergeUtilities.toFullLogRecords(raw, startDate)
    val videoFrames = MergeVideoData.lookupVideoFrames(videoArchiveName).filter(_.getRecordedDate != null)

    if (log.isDebugEnabled) {
      val df = GlobalLookup.DATE_FORMAT_UTC

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
          |  MergeVARS692 [videoArchiveName] [logFile] [startDate]
          |
          | Inputs:
          |  videoArchiveName: The name, as stored in VARS, of the video archive
          |     to merge
          |
          |  logFile: The full path to the log file to use for the merge
          |
          |  startDate: The start date of the video in yyyy-MM-dd HH:mm:ss
        """.stripMargin)
      return
    }
    val videoArchiveName = args(0)
    val logFile = new File(args(1))
    val startDate = Try(GlobalLookup.DATE_FORMAT_UTC.parse(args(2))) match {
      case Success(d) => d
      case Failure(d) => {
        log.error(s"Unable to parse your date of ${args(2)}. Try yyyy-MM-dd HH:mm:ss format.")
        System.exit(-1)
        new Date() // Never reached. Added to keep compiler happy
      }
    }
    apply(videoArchiveName, logFile, startDate)
  }

}
