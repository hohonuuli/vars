package org.mbari.vars.integration

import org.mbari.expd.UberDatum
import org.mbari.math.FastCollator
import org.mbari.movie.Timecode
import org.slf4j.LoggerFactory
import vars.annotation.VideoFrame
import vars.annotation.ui.ToolBelt
import vars.integration.MergeType

import scala.util.Try
import scala.util.control.NonFatal

/**
 *
 *
 * @author Brian Schlining
 * @since 2015-06-07T14:22:00
 */
class MergeVCRData(videoFrames: Iterable[VideoFrame], uberData: Iterable[UberDatum],
                   isHD: Boolean = true, epsilonSecs: Double = 7.5)(implicit toolBelt: ToolBelt) {

  private[this] val log = LoggerFactory.getLogger(getClass)

  private def videoFrameToSeconds(videoFrame: VideoFrame): Double =
    try {
      videoFrame.getRecordedDate.getTime.toDouble / 1000D
    }
    catch {
      case NonFatal(e) => Double.MaxValue
    }


  private def uberDatumToSeconds(uberDatum: UberDatum): Double =
    try {
      uberDatum.getNavigationDatum.getDate.getTime.toDouble / 1000D
    }
    catch {
      case NonFatal(e) => Double.MinValue
    }


  private def videoFrameToFrames(videoFrame: VideoFrame): Double = {
    try {
      val tc = new Timecode(videoFrame.getTimecode)
      tc.getFrames / tc.getFrameRate
    }
    catch {
      case NonFatal(e) => Double.MaxValue
    }
  }

  private def uberDatumToFrames(uberDatum: UberDatum): Double = {
    try {
      val tc = new Timecode(uberDatum.getCameraDatum.getTimecode)
      tc.getFrames / tc.getFrameRate
    }
    catch {
      case NonFatal(e) => Double.MinValue
    }
  }



  def collate(mergeType: MergeType): Seq[(VideoFrame, Option[UberDatum])] = {

  }

  def collateConservative(): Seq[(VideoFrame, Option[UberDatum])] = {
    // vf0 = videoframes with recordeddate, vf1 = videoframes without recordeddate
    val c0 = FastCollator(videoFrames, videoFrameToSeconds, uberData, uberDatumToSeconds, epsilonSecs)
    val (c1, c2)  = c0.partition(_._2.isDefined)

    val vf2 = c2.map(_._1)       // videoframes that were not merged
    val c3 = FastCollator(vf2, videoFrameToFrames, uberData, uberDatumToFrames, epsilonSecs)

    logCollation(c3)            // Only need to check c3 for missing UberData matches

    c1 ++ c3
  }

  def collateOptimistic(): Seq[(VideoFrame, Option[UberDatum])] = {
    val c0 = FastCollator(videoFrames, videoFrameToSeconds, uberData, uberDatumToSeconds, epsilonSecs)
    logCollation(c0)
    c0
  }

  def collatePessimistic(): Seq[(VideoFrame, Option[UberDatum])] = {
    val c0 = FastCollator(videoFrames, videoFrameToFrames, uberData, uberDatumToFrames, epsilonSecs)
    logCollation(c0)
    c0
  }

  def collatePragmatic(): Seq[(VideoFrame, Option[UberDatum])] = {
    val minDate = uberData.minBy(_.getNavigationDatum.getDate.getTime).getNavigationDatum.getDate
    val maxDate = uberData.maxBy(_.getNavigationDatum.getDate.getTime).getNavigationDatum.getDate

    val bogusVf = videoFrames.filter(vf => {
      val rd = vf.getRecordedDate
      rd == null || rd.before(minDate) || rd.after(maxDate)
    })

    val c0 = FastCollator(bogusVf, videoFrameToFrames, uberData, uberDatumToFrames, epsilonSecs)

    c0

  }


  def logCollation(collated: Seq[(VideoFrame, Option[UberDatum])]): Unit = {
    if (log.isDebugEnabled) {
      val (good, bad) = collated.map(_._2).partition(_.isDefined)
      log.debug(s"Of ${uberData.size} EXPD records, ${good.size} matched videoframes by date. ${bad.size} videoframe had no matching log data.")
    }
  }



}
