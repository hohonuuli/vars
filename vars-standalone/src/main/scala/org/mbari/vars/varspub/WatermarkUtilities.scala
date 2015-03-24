package org.mbari.vars.varspub

import java.awt.{AlphaComposite, Graphics2D}
import java.awt.image.{RenderedImage, BufferedImage}
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, File, IOException}
import java.net.URL
import javax.imageio.metadata.IIOMetadataNode
import javax.imageio.{IIOImage, ImageTypeSpecifier, ImageIO}

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
    log.debug("Attempting to read image at {}", url)
    Try {
      val inputStream = url.openStream()
      val buf = Array.ofDim[Byte](6)
      inputStream.read(buf)
      inputStream.close()
      true
    } getOrElse {
      log.debug("Failed to read {}", url)
      false
    }
  }

  /**
   * Convert a buffered image to an array of bytes in JPEG format
   * @param image
   * @return
   */
  def toJpegByteArray(image: BufferedImage): Array[Byte] = toImageByteArray(image, "jpg")

  /**
   * Convert a buffered image to an array of bytes in PNG format
   * @param image
   * @return
   */
  def toPngByteArray(image: BufferedImage): Array[Byte] = toImageByteArray(image, "png")

  def toImageByteArray(image: BufferedImage, format: String): Array[Byte] = {
    val os0 = new ByteArrayOutputStream
    ImageIO.write(image, format, os0)
    val imageBytes = os0.toByteArray
    os0.close()
    imageBytes
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

  /**
   * Add EXIF metadata to a JPG image for storing metadata.
   * @param jpegBytes an image formatted as JPG bytes.
   * @param outputSet Contains the metadata to write
   * @return An image representation as as JPG bytes with metadata added.
   */
  def addExifAsJPG(jpegBytes: Array[Byte], outputSet: TiffOutputSet): Array[Byte] = {
    val os1 = new ByteArrayOutputStream
    val is1 = new ByteArrayInputStream(jpegBytes)
    (new ExifRewriter).updateExifMetadataLossless(is1, os1, outputSet)
    val jpegWithExifBytes = os1.toByteArray
    os1.close()
    is1.close()
    jpegWithExifBytes
  }

  /**
   * Add iTXt nodes to a PNG image for storing metadata. Accepted keywords are found at
   * http://www.w3.org/TR/PNG/#11textinfo
   *
   * @param image The bufferedimage to add metadata to
   * @param txt A key-value map of metadata. See http://www.w3.org/TR/PNG/#11textinfo for
   *            predefined keywords
   * @return A byte array in PNG format
   */
  def addMetadataAsPNG(image: BufferedImage, txt: Map[String, String]): Array[Byte] = {
    // http://www.w3.org/TR/PNG/#11textinfo
    // https://stackoverflow.com/questions/6495518/writing-image-metadata-in-java-preferably-png
    val writer = ImageIO.getImageWritersByFormatName("png").next()
    val writeParam = writer.getDefaultWriteParam
    val typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB)

    // Add metadata
    val metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam)

    // I include notes to write tEXt nodes instead
    val text = new IIOMetadataNode("iTXt")               // new IIOMetadataNode("tEXt")
    for ((key, value) <- txt) {
      val textEntry = new IIOMetadataNode("iTXtEntry")   // new IIOMetadataNode("tEXtEntry")
      textEntry.setAttribute("keyword", key)
      textEntry.setAttribute("text", value)              // textEntry.setAttribute("value", value)
      textEntry.setAttribute("compressionFlag", "FALSE") // Comment out for tEXt
      textEntry.setAttribute("compressionMethod", "0")   // Comment out for tEXt
      textEntry.setAttribute("languageTag", "en-US")     // Comment out for tEXt
      textEntry.setAttribute("translatedKeyword", key)   // Comment out for tEXt
      text.appendChild(textEntry)
    }

    val root = new IIOMetadataNode("javax_imageio_png_1.0")
    root.appendChild(text)
    metadata.mergeTree("javax_imageio_png_1.0", root)

    // write image
    val os = new ByteArrayOutputStream()
    val stream = ImageIO.createImageOutputStream(os)
    writer.setOutput(stream)
    writer.write(metadata, new IIOImage(image, null, metadata), writeParam)
    os.toByteArray
  }

}
