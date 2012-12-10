package org.mbari.smith

import java.lang.{Double => JDouble}
import scala.math._

/**
 * Calculates view and image parameters for a camera mounted above a view plane. Here's a few
 * important terms:
 *   1. View plane - The plane that we're trying to image (e.g. the seafloor)
 *   2. Image plane - The plane of the image (abstract concept)
 *   3. Principal point - The center of the image on our image plane. (e.g. if you have an image
 *      400x300 pixels then pixel at coordinates 200, 150 is the principal point). A line can
 *      be extended from this point to intersect with our view plane.
 *
 * References: [1] W. Wakefield and A. Genin, “The use of a Canadian (perspective) grid in deep-sea
 *   photography,” Deep Sea Research Part A. Oceanographic …, vol. 34, no. 3, pp. 469–478, 1987.
 *
 * @param height The height of the camera above the plane
 * @param alpha The vertical view angle of the camera in radians
 * @param beta The horizontal view angle of the camera in radians
 * @param theta The tilt of the camera in radians (0 degree is parallel to the plane, 90 is pointed
 *              directly at plane)
 * @param units An optional value that indicates the units of height. All distance measurements will
 *              be in the same units as height
 *
 * @author Brian Schlining
 * @since 2012-12-06
 */

class Camera(val height: Double,
    val alpha: Double,
    val beta: Double,
    val theta: Double,
    val units: Option[String] = None) {

  require(theta > 0 && theta <= Pi / 2, "alpha must be greater than 0 and less than Pi/2. " +
      "You gave " + theta)

  require(alpha > 0 && alpha < Pi, "beta must be greater than 0 and less than Pi. " +
        "You gave " + alpha)

  require(beta > 0 && beta < Pi, "beta must be greater than 0 and less than Pi. " +
          "You gave " + beta)

  /**
   * The distance from the point directly under the camera on the view plane to the principal point
   * on the view plane.
   */
  val planeDistance = height / tan(theta)

  /**
   * The distance from the camera to the principal point on the view plane
   */
  val lensDistance = height / sin(theta)

  /**
   * The distance from the point directly under the camera to the nearest edge of the image on the
   * view plane
   */
  val nearViewEdgeDistance = height / tan(theta + alpha / 2)

  /**
   * The distance from the point directly under the camera to the farthest edge of the image on the
   * view plane
   */
  val farViewEdgeDistance = height / tan(theta - alpha / 2)

  /**
   * The distance between the near and far edges of our image projected onto the view plane
   */
  val viewHeight = farViewEdgeDistance - nearViewEdgeDistance

  /**
   * The width of the image on the view plane at the principal point
   */
  val viewWidth = lensDistance * tan(beta / 2) * 2

}



object Camera {

  /**
   * Factory method for creating a Camera object using arguments in degrees.
   *
   * @param height The height of the camera above the plane
   * @param alpha The vertical view angle of the camera in degrees
   * @param beta The horizontal view angle of the camera in degrees
   * @param theta The tilt of the camera in degrees (0 degree is parallel to the plane, 90 is pointed
   *              directly at plane)
   * @param units An optional value that indicates the units of height. All distance measurements will
   *              be in the same units as height
   * @return
   */
  def fromDegrees(height: Double, alpha: Double, beta: Double, theta: Double,
      units: Option[String] = None) =
    new Camera(height, toRadians(alpha), toRadians(beta), toRadians(theta), units)

  /**
   * Factory method for creating a Camera object using arguments in radians. This is exactly the same
   * as calling the Camera constuctor directly, but is included as an analog to the __fromDegrees__
   * factory method.
   *
   * @param height The height of the camera above the plane
   * @param alpha The vertical view angle of the camera in radians
   * @param beta The horizontal view angle of the camera in radians
   * @param theta The tilt of the camera in radians (0 degree is parallel to the plane, 90 is pointed
   *              directly at plane)
   * @param units An optional value that indicates the units of height. All distance measurements will
   *              be in the same units as height
   * @return
   */
  def fromRadians(height: Double, alpha: Double, beta: Double, theta: Double,
      units: Option[String] = None) = new Camera(height, alpha, beta, theta, units)

  /**
   * Let's play nicely with Java/Groovy
   * @param height
   * @param alpha
   * @param beta
   * @param theta
   * @return
   */
  def fromJavaRadians(height: JDouble, alpha: JDouble, beta: JDouble, theta: JDouble) =
      new Camera(height, alpha, beta, theta)

}