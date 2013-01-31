package org.mbari.smith

import vars.annotation.ui.{ToolBelt, Lookup}
import com.google.inject.Injector
import scala.collection.JavaConverters._
import vars.annotation.{Observation, VideoFrame}
import org.slf4j.LoggerFactory
import vars.annotation.ui.imagepanel.AreaMeasurement
import java.awt.Polygon
import collection.mutable
import java.net.URL
import javax.imageio.ImageIO
import scala.math._


/**
 *
 * @author Brian Schlining
 * @since 2013-01-29
 */
object CoverageEstimator {

  private[this] val log = LoggerFactory.getLogger(getClass)

  private[this] val toolBelt = Lookup.getGuiceInjectorDispatcher.getValueObject.
      asInstanceOf[Injector].getInstance(classOf[ToolBelt])


  def apply(videoArchiveName: String, camera: Camera): List[ActualArea] = {
    val frameAreas0 = toFrameAreas(fetchAnnotations(videoArchiveName))
    val frameAreas1 = frameAreas0.map(keepAnnotationsWithinFOV(_))
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
   * Transform the VideoFrames into FrameArea objects
   * @param videoFrames
   * @return
   */
  private def toFrameAreas(videoFrames: List[VideoFrame]): List[FrameAreas] = {

    var frameAreas = new mutable.ListBuffer[FrameAreas]
    for (vf <- videoFrames) {
      var fov: Option[AreaPolygon] = None
      var detritus = new mutable.ListBuffer[AreaPolygon]
      for {
        obs <- vf.getObservations.asScala
        ass <- obs.getAssociations.asScala
        if (ass.getLinkName == AreaMeasurement.AREA_MEASUREMENT_LINKNAME)
      } {

        val areaMeasurement = AreaMeasurement.fromLink(ass)
        val polygon = toPolygon(areaMeasurement)
        val areaPolygon = AreaPolygon(areaMeasurement, polygon)
        if (areaMeasurement.getComment.contains("fov")) {
          fov = Option(areaPolygon)
        }
        else {
          detritus :+ areaPolygon
        }
      }
      fov.foreach { am =>
        frameAreas :+ FrameAreas(vf, am, detritus.toList)
      }
    }
    frameAreas.toList
  }

  /**
   * Drops any AreaPolygons that do not intersect with the FOV
   * @param frameAreas
   * @return
   */
  private def keepAnnotationsWithinFOV(frameAreas: FrameAreas) = {
    val fovPolygon = frameAreas.fov.polygon
    val intersect = frameAreas.detritus.filter( fa => fovPolygon.intersects(fa.polygon.getBounds2D) )
    frameAreas.copy(detritus = intersect)
  }

  private def toPolygon(areaMeasurement: AreaMeasurement): Polygon = {
    val coords = areaMeasurement.getCoordinates.asScala
    def polygon = new Polygon
    coords.foreach { p =>
      polygon.addPoint(p.getX, p.getY)
    }
    polygon
  }

  private def toArea(frameAreas: List[FrameAreas], camera: Camera): List[ActualArea] = {

    for {
      fa <- frameAreas
    } yield {
      val (imageWidth, imageHeight) = imageDimensions(
        new URL(fa.videoFrame.getCameraData.getImageReference))
      val totalFovArea = calculateArea(fa.fov.areaMeasurement, imageWidth, imageHeight, camera)
      val detritalAreas = fa.detritus.map(ap =>
        calculateArea(ap.areaMeasurement, imageWidth, imageHeight, camera))

      ActualArea(fa.videoFrame, totalFovArea, detritalAreas.sum)

    }
  }

  private def imageDimensions(image: URL): (Int, Int) = {
    def img = ImageIO.read(image)
    (img.getWidth(), img.getHeight())
  }

  private def calculateArea(areaMeasurement: AreaMeasurement,
      imageWidth: Int,
      imageHeight: Int,
      camera: Camera): Double = {

    val coords = areaMeasurement.getCoordinates.asScala
    val pixels = coords.map { p =>
      new Pixel(camera, imageWidth, imageHeight, p.getX, p.getY)
    }

    val n = coords.size

    val i1 = 0 until n         // 0, 1, 2, ... , n
    val i2 = (1 until n) :+ 0  // 1, 2, 3, ... , n, 0

    var p1 = 0D
    var p2 = 0D
    for (i <- 0 until n) {
      p1 = p1 + (pixels(i1(i)).xDistance * pixels(i2(i)).yDistance)
      p2 = p2 + (pixels(i2(i)).xDistance * pixels(i1(i)).yDistance)
    }

    // The sign just indicates the clockwise/counter-clockwises arrangement of
    // coordinates. So we just return the abs value
    abs((p1 - p2) / 2)
  }



}

case class ActualArea(videoFrame: VideoFrame, fovArea: Double, detritalArea: Double)
case class AreaPolygon(areaMeasurement: AreaMeasurement, polygon: Polygon)
case class ImageAreas(videoFrame: VideoFrame, areas: List[AreaPolygon])
case class FrameAreas(videoFrame: VideoFrame, fov: AreaPolygon, detritus: List[AreaPolygon])