package org.mbari.vars.arctic


import java.io.File
import java.util.{Date, TimeZone}

import com.google.inject.Injector
import org.mbari.math.FastCollator
import org.slf4j.LoggerFactory
import vars.annotation.VideoFrame
import vars.annotation.ui.{StateLookup, ToolBelt}
import vars.shared.ui.GlobalLookup

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

/**
 *
 *
 * @author Brian Schlining
 * @since 2015-02-25T13:47:00
 */
class MergeVideoData(videoFrames: Iterable[VideoFrame], logRecords: Iterable[FullLogRecord])(implicit toolBelt: ToolBelt) {

  private[this] val log = LoggerFactory.getLogger(getClass)

  private def videoFrameToSeconds(videoFrame: VideoFrame): Double =
    videoFrame.getRecordedDate.getTime.toDouble / 1000D

  private def logRecordToSeconds(fullLogRecord: FullLogRecord): Double =
    fullLogRecord.gpsDate.getTime.toDouble / 1000D


  def collate(): Seq[(VideoFrame, Option[FullLogRecord])] = {
    val collated = FastCollator(videoFrames, videoFrameToSeconds, logRecords, logRecordToSeconds, 15)
    if (log.isDebugEnabled) {
      val (good, bad) = collated.map(_._2).partition(_.isDefined)
      log.debug(s"Of ${logRecords.size} log lines , ${good.size} matched videoframes by date. ${bad.size} videoframe had no matching log data.")
    }
    collated
  }

  def update(xs: Seq[(VideoFrame, Option[FullLogRecord])]): Seq[(VideoFrame, Option[FullLogRecord])] = {
    val dao = MergeVideoData.toolbelt.getAnnotationDAOFactory.newVideoArchiveSetDAO()
    dao.startTransaction()
    val ys = for {
      (videoFrame, logRecord) <- xs
    } yield {

      // Set existing values to null if no match is found
      val r = logRecord match {
        case Some(lr) => lr
        case None => MergeVideoData.NullLogRecord
      }

      val vf = dao.find(videoFrame)
      val pd = vf.getPhysicalData
      // HACK! Call into Java to allow setting of null Float and Double values
      PhysicalDataHack.setDepth(pd, r.depth.getOrElse(PhysicalDataHack.BAD_FLOAT))
      PhysicalDataHack.setSalinity(pd, r.salinity.getOrElse(PhysicalDataHack.BAD_FLOAT))
      PhysicalDataHack.setTemperature(pd, r.temperature.getOrElse(PhysicalDataHack.BAD_FLOAT))
      PhysicalDataHack.setLatitude(pd, r.latitude.getOrElse(PhysicalDataHack.BAD_DOUBLE))
      PhysicalDataHack.setLongitude(pd, r.longitude.getOrElse(PhysicalDataHack.BAD_DOUBLE))
      vf -> logRecord
    }
    dao.endTransaction()
    dao.close()
    ys
  }

}


/**
 */
object MergeVideoData {

  private[this] val log = LoggerFactory.getLogger(getClass)

  val NullLogRecord = RawLogRecord(None, None, None, None, None, "")

  def lookupVideoFrames(videoArchiveName: String): Iterable[VideoFrame] = {
    val dao = toolbelt.getAnnotationDAOFactory.newVideoArchiveDAO()
    dao.startTransaction()
    val videoArchive = dao.findByName(videoArchiveName)
    val videoFrames = if (videoArchive == null) {
      log.debug(s"No VideoArchive named $videoArchiveName was found.")
      Nil
    }
    else {
      videoArchive.getVideoFrames.asScala
    }
    dao.endTransaction()
    log.debug(s"Found ${videoFrames.size} videoFrames in $videoArchiveName")
    videoFrames
  }

  lazy val toolbelt: ToolBelt = StateLookup.GUICE_INJECTOR.getInstance(classOf[ToolBelt])

  def apply(videoArchiveName: String, logFile: File, startDate: Date): Unit = {
    val raw = CSVLogReader(logFile)
    val full = MergeUtilities.toFullLogRecords(raw, startDate)
    val videoFrames = lookupVideoFrames(videoArchiveName)

    if (log.isDebugEnabled) {
      val df = GlobalLookup.DATE_FORMAT_UTC

      val rs = full.sortBy(_.gpsDate.getTime)
      log.debug(s"Log spans ${df.format(rs.head.gpsDate)} to ${df.format(rs.last.gpsDate)}")

      val vs = videoFrames.toSeq.sortBy(_.getRecordedDate.getTime)
      log.debug(s"Annotations span ${df.format(vs.head.getRecordedDate)} to ${df.format(vs.last.getRecordedDate)}")

    }

    val withBadDates = videoFrames.filter(_.getRecordedDate.before(startDate))
    if (withBadDates.size > 0) {
      log.warn(s"Found ${withBadDates.size} VideoFrames with recordedDates" +
          s" before ${GlobalLookup.DATE_FORMAT_UTC.format(startDate)}")
    }
    val merge = new MergeVideoData(videoFrames, full)(toolbelt)
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
          |  MergeVideoData [videoArchiveName] [logFile] [startDate]
          |
          | Inputs:
          |  videoArchiveName: The name, as stored in VARS, of the video archive
          |     to merge
          |
          |  logFile: The full path to the log file to use for the merge
          |
          |  startDate: The UTC date formatted as yyyy-MM-dd HH:mm:ss of the start
          |     of the logFile. Note that currently the HH:mm:ss portion is not
          |     used so you can just use 00:00:00 there.
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

