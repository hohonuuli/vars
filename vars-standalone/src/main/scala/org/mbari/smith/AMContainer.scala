package org.mbari.smith

import vars.annotation.AreaMeasurement

import scala.collection.JavaConverters._

import java.awt.Polygon
import java.lang.{Double => JDouble}
import vars.ILink
import org.mbari.geometry.Point2D
import scala.math._

/**
 *
 * @author Brian Schlining
 * @since 2013-04-22
 */
trait AMContainer[T <: ILink] {
  def areaMeasurement: AreaMeasurement
  def link: Option[T]
}

object AMContainer {
  def apply[T <: ILink](aLink: T): Option[AMContainer[T]] = {
    if (aLink.getLinkName.equals(AreaMeasurement.AREA_MEASUREMENT_LINKNAME)) {
      Option(new AMContainer[T] {
        val link: Option[T] = Option(aLink)
        lazy val areaMeasurement: AreaMeasurement = AreaMeasurement.fromLink(aLink)
      })
    }
    else None
  }
}

trait HasCameraView {
  def camera: Camera
  def imageWidth: Int
  def imageHeight: Int
}

case class CameraView(camera: Camera, imageWidth: Int, imageHeight: Int) extends HasCameraView

trait HasPolygon {
  def polygon: Polygon
}

trait AMPolygon[T <: ILink] extends AMContainer[T] with HasPolygon

case class RawPolygon[T <: ILink](link: Option[T], areaMeasurement: AreaMeasurement)
    extends AMPolygon[T] {
  lazy val polygon: Polygon = {
    val coords = areaMeasurement.getCoordinates.asScala
    val polygon = new Polygon
    coords.foreach { p =>
      polygon.addPoint(p.getX, p.getY)
    }
    polygon
  }
}

object RawPolygon {
  def apply[T <: ILink](link: T): Option[RawPolygon[T]] =
    AMContainer(link).map(c => new RawPolygon[T](Option(link), c.areaMeasurement))
}


/* case class BasicPolygon[T <: ILink](link: Option[T],
    areaMeasurement: AreaMeasurement,
    cameraView: HasCameraView) extends AMPolygon {
  lazy val polygon: Polygon = {
    val coords = areaMeasurement.getCoordinates.asScala
    val pixels = coords.map { p =>
      new Pixel(cameraView.camera, cameraView.imageWidth, cameraView.imageHeight, p.getX, p.getY)
    }
    val polygon = new Polygon
    pixels.foreach( p => polygon.addPoint(p.xDistance, p.yDistance) )
    polygon
  }
} */

trait HasPoint[T <: Number] {
  def point: Point2D[T]
}

trait AMPoint[T <: ILink, P <: Number] extends AMContainer[T] with HasPoint[P] with HasCameraView

case class BasicPoint[T <: ILink, P <: Number](link: Option[T],
    areaMeasurement: AreaMeasurement,
    point: Point2D[P],
    cameraView: HasCameraView) extends AMPoint[T, P] {
  def camera: Camera = cameraView.camera

  def imageWidth: Int = cameraView.imageWidth

  def imageHeight: Int = cameraView.imageHeight
}

object RawPoint {

  def centerOfMass[T <: ILink](link: T, cameraView: HasCameraView): Option[AMPoint[T, JDouble]] =
    AMContainer(link).map { c =>
      val point = {
        val pixels = toPixels(c.areaMeasurement, cameraView)
        val cx = pixels.map(_.xDistance).sum / pixels.size
        val cy = pixels.map(_.yDistance).sum / pixels.size
        new Point2D[JDouble](cx, cy)
      }
      BasicPoint(Option(link), c.areaMeasurement, point, cameraView)
    }


  def farthestPoint[T <: ILink](link: T, cameraView: HasCameraView): Option[AMPoint[T, JDouble]] = {
    val a = AMContainer(link).map { c =>
      val pixels = toPixels(c.areaMeasurement, cameraView)
      val distances = pixels.map(p => sqrt(p.xDistance * p.xDistance + p.yDistance + p.yDistance))
      val max = distances.max
      val farthestPixel = pixels.find { p =>
        val d = sqrt(p.xDistance * p.xDistance + p.yDistance + p.yDistance)
        d == max
      }

      farthestPixel map { p =>
        BasicPoint(Option(link),
          c.areaMeasurement,
          new Point2D[JDouble](p.xDistance, p.yDistance),
          cameraView)
      }
    }
    a.getOrElse(None)
  }

  def farthestPointFrom[T <: ILink](link: T, cameraView: HasCameraView, origin: Pixel): Option[AMPoint[T, JDouble]] = {
    val a = AMContainer(link).map { c =>
      val pixels = toPixels(c.areaMeasurement, cameraView)
      val distances = pixels.map(p => sqrt(pow(p.xDistance - origin.xDistance, 2) +
          pow(p.yDistance - origin.yDistance, 2)))
      val max = distances.max
      val farthestPixel = pixels.find { p =>
        val d = sqrt(pow(p.xDistance - origin.xDistance, 2) + pow(p.yDistance - origin.yDistance, 2))
        d == max
      }

      farthestPixel map { p =>
        BasicPoint(Option(link),
          c.areaMeasurement,
          new Point2D[JDouble](p.xDistance, p.yDistance),
          cameraView)
      }
    }
    a.getOrElse(None)
  }

  private def toPixels(areaMeasurement: AreaMeasurement, cameraView: HasCameraView): IndexedSeq[Pixel] = {
    val coords = areaMeasurement.getCoordinates.asScala
    coords.map { p =>
      new Pixel(cameraView.camera, cameraView.imageWidth, cameraView.imageHeight, p.getX, p.getY)
    } toIndexedSeq
  }
}




