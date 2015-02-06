package org.mbari.vars.varspub

import java.awt.{AlphaComposite, Graphics2D}
import java.awt.image.{RenderedImage, BufferedImage}
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, File, IOException}
import java.net.URL
import javax.imageio.ImageIO

import org.apache.commons.imaging.Imaging
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet
import org.imgscalr.Scalr
import org.slf4j.LoggerFactory

import scala.math._
import scala.sys.process._
import scala.util.Try

// implicit import for running external processes

/**
 *
 *
 * @author Brian Schlining
 * @since 2014-11-21T14:01:00
 */
object WatermarkUtilities {

  private[this] lazy val alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5F)
  private[this] val log = LoggerFactory.getLogger(getClass)


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


  /**
   *
   * @param image
   * @param overlay
   * @return
   */
  def addWatermarkImage(image: BufferedImage, overlay: BufferedImage, overlayPercentWidth: Double = 0.4): BufferedImage = {
    val g2 = image.getGraphics.asInstanceOf[Graphics2D]
    val v = image.getWidth * overlayPercentWidth / overlay.getWidth
    val so = WatermarkUtilities.scaleOverlay(v, overlay)
    val x = round(image.getWidth * 0.05).toInt
    val y = round(image.getHeight * 0.85 - so.getHeight).toInt
    g2.setComposite(alphaComposite)
    g2.drawImage(so, x, y, null)
    g2.dispose()
    image
  }

  /**
   * Tests to see fi an image exists
   *
   * @param url The image URL
   * @return true if an image was found at the URL, false otherwise
   */
  def imageExistsAt(url: URL): Boolean = {
    log.info("Attempting to read image at {}", url)
    Try {
      val inputStream = url.openStream()
      val buf = Array.ofDim[Byte](6)
      inputStream.read(buf)
      inputStream.close()
      true
    } getOrElse {
      log.info("Failed to read {}", url)
      false
    }
  }

  def toJpegByteArray(image: BufferedImage): Array[Byte] = {
    // -- Convert BufferedImage to a jpeg in a byte array
    val os0 = new ByteArrayOutputStream
    ImageIO.write(image, "jpg", os0)
    val jpegBytes = os0.toByteArray
    os0.close()
    jpegBytes
  }

  def getOrCreateOutputSet(jpegBytes: Array[Byte]): TiffOutputSet = {
    val is0 = new ByteArrayInputStream(jpegBytes)
    val metadata = Option(Imaging
        .getMetadata(is0, "")
        .asInstanceOf[JpegImageMetadata])
    val outputSet = metadata.map(_.getExif)
        .map(_.getOutputSet)
        .getOrElse(new TiffOutputSet)
    is0.close()
    outputSet
  }

  def addExif(jpegBytes: Array[Byte], outputSet: TiffOutputSet): Array[Byte] = {
    val os1 = new ByteArrayOutputStream
    val is1 = new ByteArrayInputStream(jpegBytes)
    (new ExifRewriter).updateExifMetadataLossless(is1, os1, outputSet)
    val jpegWithExifBytes = os1.toByteArray
    os1.close()
    is1.close()
    jpegWithExifBytes
  }

}
