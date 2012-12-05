package org.mbari.smith

/**
 * References:
 *   Wakefield WW and Genin A. 1987. The use of a canadian (perspective) grid in deep-sea photography.
 *   Deep-Sea Res 34(3):469-78.
 *
 * @param cameraHeight The Camera's height above the bottom
 * @param cameraInclinationRadians The Camera's optical inclination in radians from horizontal
 *                                 (i.e. pointing at infinite horizon)
 * @param alpha The angular 'height' of the field of view in radians
 * @param beta The angular 'width' of the image field of view in radians
 *
 * @author Brian Schlining
 * @since 2012-11-27
 */
class CanadianPerspectiveGrid(cameraHeight: Double,
    cameraInclinationRadians: Double,
    alpha: Double,
    beta: Double) {


  /**
   * Calculate the actual vertical distance (along y-axis) between lower edge (b) and the y points (a).
   *
   * @param imageHeightInPixels
   * @param y
   * @return the vertical distance in the same units as cameraHeight, imageHeight
   */
  def verticalDistance(p: Double, y: Int) = {

  }

  def horizontalDistance() = {

  }

}
