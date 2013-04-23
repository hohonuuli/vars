package org.mbari.smith

import vars.annotation.ui.{ToolBelt, Lookup}
import com.google.inject.Injector
import scala.collection.JavaConverters._
import vars.annotation.{VideoFrame}
import org.slf4j.LoggerFactory
import java.net.URL


/**
 *
 * @author Brian Schlining
 * @since 2013-01-29
 */
object CoverageEstimator {

  private[this] val log = LoggerFactory.getLogger(getClass)

  private[this] val toolBelt = Lookup.getGuiceInjectorDispatcher.getValueObject.
      asInstanceOf[Injector].getInstance(classOf[ToolBelt])

  private[this] var imageWidth: Int = _
  private[this] var imageHeight: Int = _
  private[this] var gotDimensions = false


  def apply(videoArchiveName: String, camera: Camera): List[RealArea] = {
    val frameAreas0 = CanadianGrid.toDetrialPolygons(fetchAnnotations(videoArchiveName))
    log.debug("Found " + frameAreas0.size + " areaMeasurements")
    val frameAreas1 = frameAreas0.map(keepAnnotationsWithinFOV(_))
    log.debug("Found " + frameAreas1.size + " areaMeasurements within FOV")
    toArea(frameAreas1, camera)
  }

  /**
   * Retrieve the VideoFrames for the VideoArchive
   * @param videoArchiveName
   * @return
   */
  def fetchAnnotations(videoArchiveName: String): List[VideoFrame] = {
    val dao = toolBelt.getAnnotationDAOFactory.newVideoArchiveDAO()
    dao.startTransaction()
    val list = Option(dao.findByName(videoArchiveName)) match {
      case Some(videoArchive) => videoArchive.getVideoFrames.asScala.toList
      case None => {
        log.info("No VideoArchive named " + videoArchiveName + " was found in the database")
        List.empty[VideoFrame]
      }
    }
    dao.endTransaction()
    list
  }



  /**
   * Drops any AreaPolygons that do not intersect with the FOV
   * @param frameAreas
   * @return
   */
  private def keepAnnotationsWithinFOV(frameAreas: DetritalPolygons) = {
    val fovPolygon = frameAreas.fov.polygon
    val intersect = frameAreas.detritus.filter( fa => fovPolygon.intersects(fa.polygon.getBounds2D) )
    log.debug(frameAreas.videoFrame + " has " + intersect.size + " measurements in the FOV")
    frameAreas.copy(detritus = intersect)
  }



  private def toArea(frameAreas: List[DetritalPolygons], camera: Camera): List[RealArea] = {

    for {
      fa <- frameAreas
    } yield {
      if (!gotDimensions) {
        val d = CanadianGrid.imageDimensions(new URL(fa.videoFrame.getCameraData.getImageReference))
        imageWidth = d._1
        imageHeight = d._2
        gotDimensions = true
      }

      val totalFovArea = CanadianGrid.calculateArea(fa.fov.areaMeasurement, imageWidth, imageHeight, camera)
      val detritalAreas = fa.detritus.map(ap =>
        CanadianGrid.calculateArea(ap.areaMeasurement, imageWidth, imageHeight, camera))
      log.debug(detritalAreas.sum + "")
      RealArea(fa.videoFrame, totalFovArea, detritalAreas.sum)

    }
  }


}

