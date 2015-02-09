package org.mbari.vars.varspub


import java.text.SimpleDateFormat
import java.util.Date

import com.google.inject.Injector
import java.awt.image.{BufferedImage}
import java.io.{FileOutputStream, BufferedOutputStream, File}
import java.net.URL
import java.nio.file.{Files, Paths, Path}
import javax.imageio.ImageIO
import org.apache.commons.imaging.formats.tiff.constants.{TiffTagConstants, ExifTagConstants}
import org.slf4j.LoggerFactory
import scala.collection.JavaConverters._
import scala.util.{Failure, Try}
import vars.knowledgebase.ui.{Lookup, ToolBelt}
import vars.knowledgebase.{Media, Concept}

/**
 *
 *
 * @author Brian Schlining
 * @since 2014-11-20T14:05:00
 */
class KBImageMigrator2(target: Path, overlayImageURL: URL, webpath: String = "http://dsg.mbari.org/images/dsg/",
                      overlayPercentWidth: Double = 0.4)(implicit toolBelt: ToolBelt) {

  private[this] val log = LoggerFactory.getLogger(getClass)
  private[this] val conceptDao = toolBelt.getKnowledgebaseDAOFactory.newConceptDAO()
  private[this] val yearFormat = new SimpleDateFormat("yyyy")
  private[this] val now = new Date

  private[this] val overlayImage = ImageIO.read(overlayImageURL)

  def processConcept(concept: Concept): Unit = {
    conceptDao.startTransaction()
    val c = conceptDao.find(concept)
    val medias = c.getConceptMetadata.getMedias.asScala.filter(_.getType == Media.TYPE_IMAGE)
    for {
      m <- medias
      p <- toTargetPath(m)
      i <- waterwark(m, overlayImage)
    } {

      if (!Files.exists(p.getParent)) {
        Files.createDirectories(p.getParent)
      }

      try {
        if (p.getFileName.toString.endsWith(".png")) {
          log.info(s"PNG Image! Not adding EXIF to $p")
          WatermarkUtilities.saveImage(i, p.toFile)
        }
        else {
          val jpegWithExif = addExif(c, i)
          val os = new BufferedOutputStream(new FileOutputStream(p.toFile))
          os.write(jpegWithExif)
          os.close()
        }
      }
      catch {
        case e: Exception => log.info(s"Failed to write $p", e)
      }
    }
  }

  def toTargetPath(media: Media): Option[Path] = {
    val source = media.getUrl
    val idx = source.indexOf(webpath)
    val subpath = if (idx >= 0) Some(source.substring(webpath.size))
    else None
    subpath.map(s => target.resolve(s))
  }

  def waterwark(media: Media, overlay: BufferedImage): Option[BufferedImage] = {
    Try {
      val image = ImageIO.read(new URL(media.getUrl))
      WatermarkUtilities.addWatermarkImage(image, overlayImage, overlayPercentWidth)
    }.toOption
  }

  def addExif(concept: Concept, image: BufferedImage): Array[Byte] = {
    val jpegBytes = WatermarkUtilities.toJpegByteArray(image)
    val outputSet = WatermarkUtilities.getOrCreateOutputSet(jpegBytes)

    val exifDirectory = outputSet.getOrCreateExifDirectory()
    exifDirectory.removeField(ExifTagConstants.EXIF_TAG_USER_COMMENT)
    exifDirectory.add(ExifTagConstants.EXIF_TAG_USER_COMMENT,
      s"Representative image for ${concept.getPrimaryConceptName.getName}")

    val rootDirectory = outputSet.getOrCreateRootDirectory()

    rootDirectory.removeField(TiffTagConstants.TIFF_TAG_COPYRIGHT)
    rootDirectory.add(TiffTagConstants.TIFF_TAG_COPYRIGHT,
      s"Copyright ${yearFormat.format(now)} Monterey Bay Aquarium Research Institute")

    WatermarkUtilities.addExif(jpegBytes, outputSet)

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
      val injector = Lookup.getGuiceInjectorDispatcher.getValueObject.asInstanceOf[Injector]
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
