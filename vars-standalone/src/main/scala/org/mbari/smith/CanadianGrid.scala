package org.mbari.smith

import vars.annotation.{AreaMeasurement, Association, VideoFrame}
import java.awt.Polygon
import java.net.URL
import javax.imageio.ImageIO
import org.slf4j.LoggerFactory
import scala.collection.JavaConverters._
import scala.math._
import scala.collection.mutable

/**
 *
 * @author Brian Schlining
 * @since 2013-04-18
 */
object CanadianGrid {

  private[this] val log = LoggerFactory.getLogger(getClass)

  /**
   * For many canadian grid operations we need to now the width and height of the image (in pixels)
   * @param image The image of interest
   * @return A tuple of (imageWidth, imageHeight)
   */
  def imageDimensions(image: URL): (Int, Int) = {
    log.debug("Reading " + image)
    def img = ImageIO.read(image)
    (img.getWidth, img.getHeight)
  }

  /**
   * Calculates the actual area of an [[AreaMeasurement]]. The units
   * of the area are the square of the same units as the [[org.mbari.smith.Camera]] height
   * @param areaMeasurement The measurement to convert
   * @param imageWidth The width of the image in pixels
   * @param imageHeight The height of the image in pixels
   * @param camera A camera object that defines the camera's position and field of view
   * @return The area of the areaMeasurement
   */
  def calculateArea(areaMeasurement: AreaMeasurement,
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

  /**
   * Batch convert each [[vars.annotation.VideoFrame]] to a FrameArea object
   * @param videoFrames The videoFrames
   * @return The FrameArea's The returned list may NOT always be the same size as the videoFrames
   *         list. Any videoFrames without a __fov__ area measurement will not be included in
   *         the returned list.
   */
  def toDetrialPolygons(videoFrames: List[VideoFrame]): List[DetritalPolygons] = {
    val frameAreas = for (vf <- videoFrames) yield {
      toDetritalPolygon(vf)
    }
    frameAreas.flatten.toList
  }

  def extractAreaMeasurements(videoFrame: VideoFrame): Seq[AreaMeasurement] = {
    for {
      obs <- videoFrame.getObservations.asScala
      ass <- obs.getAssociations.asScala
      if (ass.getLinkName == AreaMeasurement.AREA_MEASUREMENT_LINKNAME)
    } yield AreaMeasurement.fromLink(ass)
  }

  def toDetritalPolygon(videoFrame: VideoFrame): Option[DetritalPolygons] ={
    var fov: Option[VirtualPolygon] = None
    var detritus = new mutable.ListBuffer[VirtualPolygon]
    for {
      areaMeasurement <- extractAreaMeasurements(videoFrame)
    } {
      val areaPolygon = VirtualPolygon(areaMeasurement)
      if (areaMeasurement.getComment.contains("fov")) {
        fov = Option(areaPolygon)
      }
      else {
        detritus += areaPolygon
      }
    }
    fov.map(DetritalPolygons(videoFrame, _, detritus.toList))
  }

}


/**
 * The real area of a videoframe covered by polygons
 * @param videoFrame The VideoFrame
 * @param fovArea The field of view for VideoFrame (defined by an AreaMeasurement Association that
 *                has a comment of "fov"
 * @param detritalArea The area of all other AreaMeasurement polygons in the VideoFrame summed
 *                     together
 */
case class RealArea(videoFrame: VideoFrame, fovArea: Double, detritalArea: Double)


/**
 * A Polygon representing the coordinates of the vertices of an AreaMeasurement in image (i.e. virtual)
 * coordinates.
 *
 * @param areaMeasurement
 * @param polygon
 */
case class VirtualPolygon(areaMeasurement: AreaMeasurement, polygon: Polygon)



object VirtualPolygon {

  /**
   * Converts an [[AreaMeasurement]] to a [[java.awt.Polygon]].
   * This polygon has vertices defined as pixel coordinates.
   *
   * @param areaMeasurement
   * @return
   */
  def toPolygon(areaMeasurement: AreaMeasurement): Polygon = {
    val coords = areaMeasurement.getCoordinates.asScala
    val polygon = new Polygon
    coords.foreach { p =>
      polygon.addPoint(p.getX, p.getY)
    }
    polygon
  }

  def apply(areaMeasurement: AreaMeasurement): VirtualPolygon =
    VirtualPolygon(areaMeasurement, toPolygon(areaMeasurement))

}

/**
 * A collection of AreaPolygons contained in an image
 * @param videoFrame
 * @param areas
 */
case class ImagePolygons(videoFrame: VideoFrame, areas: List[VirtualPolygon])

/**
 * All area polygons contained in an image
 * @param videoFrame The VideoFrame of interest
 * @param fov The field of view AreaMeasurement in an image
 * @param detritus All other AreaMeasurements contained in the VideoFrame
 */
case class DetritalPolygons(videoFrame: VideoFrame, fov: VirtualPolygon, detritus: List[VirtualPolygon])
