package org.mbari.smith

import scala.math._

/**
 * Given a pixel within an image and a [[org.mbari.smith.Camera]] object, __Pixel__
 * calculates the actual position of the pixel relative to the camera's location. This class uses
 * the image coordinate system which has the origin at the top-left corner of the image and +X is
 * right and +Y is down.
 *
 * @param camera The camera whose capturing images
 * @param width The width of the image in pixels
 * @param height The height of the image in pixels
 * @param x The x coordinate of the pixel of interest (0 index)
 * @param y The y coordinate of the pixel of interest (0 index)
 *
 * @author Brian Schlining
 * @since 2012-12-06
 */

class Pixel(val camera: Camera, val width: Int, val height: Int, val x: Int, val y: Int) {
  require(x >= 0 && x <= width, "x must be between 0 and " + width + ". You supplied " + x)
  require(y >= 0 && y <= width, "y must be between 0 and " + height  + ". You supplied " + x)

  /**
   * The vertical angle between the principal point and the pixel
   */
  val alpha: Double = {
    val bp = height / 2D
    val ip = bp - y
    atan(ip * tan(camera.alpha / 2D) / bp)
  }

  /**
   * The horizontal angle between the principal point and the pixel
   */
  val beta: Double = {
    val gp = width / 2
    val ip = gp - x
    atan(ip * tan(camera.beta / 2D) / gp) * -1  // Flip sign (+ is right)
  }

  /**
   * The side-to-side distance from the central meridian (a line extending from the point directly
   * beneath the camera on the view plane through the principal point)
   */
  val xDistance: Double = {
    val oiDistance = camera.height / sin(camera.theta - alpha)
    oiDistance * tan(beta)
  }

  /**
   * The forward distance from the the point on the view plane directly under the camera to the
   * row in our image containing the pixel.
   */
  val yDistance: Double = camera.height / tan(camera.theta - alpha)

}


object Pixel {

  /**
   * Retuns a List representing the 4 corners of an image in clockwise order of top-left, top-right
   * bottom-right, bottom-left.
   *
   * @param camera The camera whose capturing images
   * @param width The width of the image in pixels
   * @param height The height of the image in pixels
   */
  def imageCorners(camera: Camera, width: Int, height: Int): List[Pixel] =
    List(new Pixel(camera, width, height, 0, 0),
      new Pixel(camera, width, height, width - 1, 0),
      new Pixel(camera, width, height, width - 1, height - 1),
      new Pixel(camera, width, height, 0, height - 1))

}
