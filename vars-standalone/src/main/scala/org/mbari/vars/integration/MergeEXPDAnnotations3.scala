package org.mbari.vars.integration

import java.util

import com.google.inject.Injector
import org.mbari.expd.{Dive, UberDatum}
import org.mbari.expd.jdbc.{UberDatumImpl, DAOFactoryImpl}
import org.mbari.math.FastCollator
import org.slf4j.LoggerFactory
import vars.annotation.{VideoArchiveSetDAO, AnnotationDAOFactory, VideoFrame}
import vars.annotation.ui.Lookup
import vars.integration.{MergeHistory, MergeType, MergeFunction}
import scala.collection.JavaConverters._

/**
 *
 *
 * @author Brian Schlining
 * @since 2015-06-05T16:17:00
 */
class MergeEXPDAnnotations3(platform: String,
                            sequenceNumber: Int,
                            useHD: Boolean,
                            offsetSecs: Double = 7.5)
    extends MergeFunction[Map[VideoFrame, UberDatum]] {

  private[this] val log = LoggerFactory.getLogger(getClass)
  private[this] val daoFactory = new DAOFactoryImpl
  private[this] val mergeHistory = new MergeHistory

  lazy val dive: Option[Dive] = {
    val diveDAO = daoFactory.newDiveDAO()
    Option(diveDAO.findByPlatformAndDiveNumber(platform, sequenceNumber))
  }

  lazy val uberData = fetchExpdData()
  lazy val videoFrames = fetchVarsData()

  override def apply(mergeType: MergeType): Map[VideoFrame, UberDatum] = {
    if (log.isDebugEnabled) {
      log.debug(s"Applying $mergeType merge to $platform #$sequenceNumber [use HD = $useHD ]")
    }
    val data = coallate(mergeType)
    update(data, mergeType)
    data
  }

  override def update(data: Map[VideoFrame, UberDatum], mergeType: MergeType): Unit = ???

  override def coallate(mergeType: MergeType): Map[VideoFrame, UberDatum] = mergeType match {
      case MergeType.CONSERVATIVE => coallateConservative()
      case MergeType.OPTIMISTIC => coallateOptimistic()
      case MergeType.PESSIMISTIC => coallatePessimistic()
      case MergeType.PRAGMATIC => coallatePragmatic()
    }

  private def coallateConservative(): Map[VideoFrame, UberDatum] = {
    FastCollator(videoFrames, (vf: VideoFrame) => vf.getRecordedDate.getTime.toDouble,
      uberData, (ud: UberDatum) => ud.getNavigationDatum.getDate.getTime.toDouble,
      offsetSecs)
  }

  private def fetchExpdData(): Iterable[UberDatum] = {
    dive match {
      case None => Nil
      case Some(d) =>
        val uberDatumDAO = daoFactory.newUberDatumDAO()
        val uberDatum = uberDatumDAO.fetchData(d, useHD, offsetSecs).asScala
        // If no cameraData is found we don't get any nav data either. In that case
        // we just fetch nav data and convert it to uberdata.
        if (uberDatum.isEmpty) {
          val navigationDatumDAO = daoFactory.newNavigationDatumDAO()
          val navigationData = navigationDatumDAO.fetchBestNavigationData(d).asScala
          navigationData.map(nd => new UberDatumImpl(null, nd, null))
        }
        else uberDatum
    }
  }

  private def fetchVarsData(): Iterable[VideoFrame] = {
    val injector = Lookup.getGuiceInjectorDispatcher.getValueObject.asInstanceOf[Injector]
    val annotationDAOFactory = injector.getInstance(classOf[AnnotationDAOFactory])
    val videoArchiveSetDAO = annotationDAOFactory.newVideoArchiveSetDAO

    videoArchiveSetDAO.startTransaction()

    val videoArchiveSets = videoArchiveSetDAO.findAllByPlatformAndSequenceNumber(platform, sequenceNumber).asScala
    val allVideoArchives = videoArchiveSets.flatMap(vas => vas.getVideoArchives.asScala)
    val videoArchives = if (useHD) allVideoArchives.filter(_.getName.toUpperCase.endsWith("HD"))
        else allVideoArchives.filter(!_.getName.toUpperCase.endsWith("HD"))
    val videoFrames = videoArchives.flatMap(_.getVideoFrames.asScala)
    videoArchiveSetDAO.endTransaction()
    videoArchiveSetDAO.close()

    if (videoFrames.isEmpty) {
      mergeHistory.setStatusMessage(mergeHistory.getStatusMessage + "; No annotations found in VARS")
    }

    videoFrames

  }



}

