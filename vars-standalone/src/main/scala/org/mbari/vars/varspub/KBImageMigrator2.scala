package org.mbari.vars.varspub


import java.text.SimpleDateFormat
import java.util.Date

import com.google.inject.Injector
import java.awt.image.BufferedImage
import java.io.{BufferedOutputStream, File, FileOutputStream}
import java.net.URL
import java.nio.file.{Files, Path, Paths}
import javax.imageio.ImageIO

import org.apache.commons.imaging.formats.tiff.constants.{ExifTagConstants, TiffTagConstants}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import scala.util.{Failure, Try}
import vars.knowledgebase.ui.{StateLookup, ToolBelt}
import vars.knowledgebase.{Concept, Media}

/**
 * Fetches representational images registered in the VARS knowledgebase and creates copies
 * of them with a watermark overlay and modified image metadata appropriate for external
 * use. Currently works with PNG and JPG.
 *
 * @author Brian Schlining
 * @since 2014-11-20T14:05:00
 * @param target The directory to write the images into. The directory structure from
 *               their source location will be (mostly) preserved
 * @param overlayImageURL URL reference to the overlay to use on the images. This overlay
 *                        will be scaled to the image size, so big overlays are OK
 * @param webpath The path of the image root on the webserver that the KB images are stored. This is
 *                used to parse out the subdirectories to write the images into inorder to maintain
 *                the same directory structure. This assumes that all KB images are rooted in the
 *                same folder
 * @param overlayPercentWidth The width of the image that the overlay should be. The number should be
 *                            > 0 and < 1. Default is 0.4
 * @param toolBelt Toolbelt for accessing VARS factories.
 */
class KBImageMigrator2(target: Path, overlayImageURL: URL, webpath: String = "http://dsg.mbari.org/images/dsg/",
                      overlayPercentWidth: Double = 0.4)(implicit toolBelt: ToolBelt) {

  private[this] val log = LoggerFactory.getLogger(getClass)
  private[this] val conceptDao = toolBelt.getKnowledgebaseDAOFactory.newConceptDAO()
  private[this] val yearFormat = new SimpleDateFormat("yyyy")
  private[this] val now = new Date

  private[this] val overlayImage = ImageIO.read(overlayImageURL)

  /**
   * Generate representational images for a single concept
   * @param concept The concept of interest. [[Media]] objects associated with the
   *                concept are used for locating images.
   */
  def processConcept(concept: Concept): Unit = {
    conceptDao.startTransaction()
    val c = conceptDao.find(concept)
    val medias = c.getConceptMetadata.getMedias.asScala.filter(_.getType == Media.TYPE_IMAGE)
    for {
      media <- medias
      path <- toTargetPath(media)
      image <- waterwark(media, overlayImage)
    } {

      if (!Files.exists(path.getParent)) {
        Files.createDirectories(path.getParent)
      }

      try {


        val bytes = if (path.getFileName.toString.toLowerCase.endsWith(".png")) {
          addText(c, image, Option(media.getCredit))
        }
        else {
          addExif(c, image, Option(media.getCredit))
        }

        val os = new BufferedOutputStream(new FileOutputStream(path.toFile))
        os.write(bytes)
        os.close()
      }
      catch {
        case e: Exception => log.info(s"Failed to write $path", e)
      }
    }
  }

  /**
   * Converts a media's URL to a path that we can write to.
   * @param media Media of interest
   * @return A Path to write the image to. None if the webpath used in the constructor
   *         is not found in the media's path.
   */
  def toTargetPath(media: Media): Option[Path] = {
    val source = media.getUrl
    val idx = source.indexOf(webpath)
    val subpath = if (idx >= 0) Some(source.substring(webpath.length))
    else None
    subpath.map(s => target.resolve(s))
  }

  /**
   * Watermarks the image in a media
   * @param media
   * @param overlay
   * @return
   */
  def waterwark(media: Media, overlay: BufferedImage): Option[BufferedImage] = {
    Try {
      val image = ImageIO.read(new URL(media.getUrl))
      WatermarkUtilities.addWatermarkImage(image, overlayImage, overlayPercentWidth)
    }.toOption
  }

  /**
   * Adds EXIF metadata to a JPG image
   * @param concept The concept associated with an image
   * @param image Java representation of the image
   * @return A JPG image as a byte array. Just write it out!!
   */
  def addExif(concept: Concept, image: BufferedImage, credit: Option[String]): Array[Byte] = {
    val jpegBytes = WatermarkUtilities.toJpegByteArray(image)
    val outputSet = WatermarkUtilities.getOrCreateOutputSet(jpegBytes)

    credit.foreach(s => {
      val exifDirectory = outputSet.getOrCreateExifDirectory()
      exifDirectory.removeField(ExifTagConstants.EXIF_TAG_USER_COMMENT)
      exifDirectory.add(ExifTagConstants.EXIF_TAG_USER_COMMENT, s)
    })

    val rootDirectory = outputSet.getOrCreateRootDirectory()

    rootDirectory.removeField(TiffTagConstants.TIFF_TAG_COPYRIGHT)
    rootDirectory.add(TiffTagConstants.TIFF_TAG_COPYRIGHT,
      s"Copyright ${yearFormat.format(now)} Monterey Bay Aquarium Research Institute")

    rootDirectory.removeField(TiffTagConstants.TIFF_TAG_IMAGE_DESCRIPTION)
    rootDirectory.add(TiffTagConstants.TIFF_TAG_IMAGE_DESCRIPTION,
      s"Representative image for ${concept.getPrimaryConceptName.getName}")

    WatermarkUtilities.addExifAsJPG(jpegBytes, outputSet)
  }

  /**
   * Adds iTXt metadata to a PNG image
   * @param concept The concept associated with an image
   * @param image Java representation of the image
   * @return A PNG image as a byte array. Just write it out!!
   */
  def addText(concept: Concept, image: BufferedImage, credit: Option[String]): Array[Byte] = {
    val text = Map("Description" -> s"Representative image for ${concept.getPrimaryConceptName.getName}",
        "Copyright" -> s"Copyright ${yearFormat.format(now)} Monterey Bay Aquarium Research Institute") ++ credit.map("Source" -> _)
    WatermarkUtilities.addMetadataAsPNG(image, text)
  }


}

/**
 * Example for editing EXIF metadata
 * https://svn.apache.org/repos/asf/commons/proper/imaging/trunk/src/test/java/org/apache/commons/imaging/examples/WriteExifMetadataExample.java
 *
 */
object KBImageMigrator2 {
  def main(args: Array[String]) {
    if (args.size != 2) {
      println(
        """
          | Process all concept media images and watermark them with an overlay for the external DSG
          |
          | Usage:
          |   KBImageMigrator2 [target] [overlay]
          |
          | Inputs:
          |   target = the root directory to write the new images to
          |   overlay = the path to the overlay image that will be watermarked onto all images
          |
          | Example: KbImageMigrator(Array(
          |
        """.stripMargin)
      return
    }

    val target = Paths.get(args(0))
    val overlayImageURL = new File(args(1)).toURI.toURL
    implicit val toolbelt = {
      val injector = StateLookup.GUICE_INJECTOR
      injector.getInstance(classOf[ToolBelt])
    }
    val imageMigrator = new KBImageMigrator2(target, overlayImageURL)
    val rootConcept = toolbelt.getKnowledgebaseDAOFactory.newConceptDAO().findRoot()
    run(imageMigrator, rootConcept)

  }

  private def run(imageMigrator: KBImageMigrator2, concept: Concept): Unit = {
    imageMigrator.processConcept(concept)
    concept.getChildConcepts.asScala.foreach(run(imageMigrator, _))
  }
}
