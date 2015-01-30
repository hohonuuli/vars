package org.mbari.vars.varspub

import java.awt.image.{RenderedImage, BufferedImage}
import java.io.{File, IOException}
import javax.imageio.ImageIO

import org.imgscalr.Scalr

import scala.math._
import scala.sys.process._ // implicit import for running external processes

/**
 *
 *
 * @author Brian Schlining
 * @since 2014-11-21T14:01:00
 */
object WatermarkUtilities {


  def scaleOverlay(scaleValue: Double, overlay: BufferedImage): BufferedImage = {
    val w = ceil(overlay.getWidth * scaleValue).toInt
    val h = ceil(overlay.getHeight * scaleValue).toInt
    Scalr.resize(overlay, Scalr.Method.ULTRA_QUALITY, w)
  }

  /**
   * Checks to see if we can run exiftool in an external process
   * @return
   */
  def canUseExiftool(): Boolean = "which exiftool".! == 0

  /**
   * Saves the image to the target file. The format of the saved
   * file is determined by it's extension
   *
   * @param image The image to save
   * @param target The file to save the image to.
   * @param postSave Function to be executed after image is saved. Default function does nothing
   *
   * @throws IOException
   */
  @throws(classOf[IOException])
  def saveImage(image: RenderedImage,
                target: File,
                postSave: () => Unit = () => {}): Unit = {
    val path: String = target.getAbsolutePath
    val dotIdx: Int = path.lastIndexOf(".")
    val ext: String = path.substring(dotIdx + 1)
    ImageIO.write(image, ext, target)
    postSave()
  }

}
