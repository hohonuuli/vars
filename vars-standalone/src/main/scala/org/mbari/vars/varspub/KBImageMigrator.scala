package org.mbari.vars.varspub


import com.google.inject.Injector
import java.awt.image.BufferedImage
import java.awt.{AlphaComposite, Graphics2D}
import java.io.File
import java.net.URL
import java.nio.file.{Files, Path, Paths}
import javax.imageio.ImageIO

import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import scala.math._
import scala.sys.process._
import scala.util.Try
import vars.knowledgebase.ui.{StateLookup, ToolBelt}
import vars.knowledgebase.{Concept, Media}

/**
 *
 *
 * @author Brian Schlining
 * @since 2014-11-20T14:05:00
 */
class KBImageMigrator(target: Path, overlayImageURL: URL, webpath: String = "http://dsg.mbari.org/images/dsg/",
                      overlayPercentWidth: Double = 0.4)(implicit toolBelt: ToolBelt) {

  private[this] val log = LoggerFactory.getLogger(getClass)
  private[this] val useExiftool = WatermarkUtilities.canUseExiftool()
  private[this] val year = "2015"
  private[this] val conceptDao = toolBelt.getKnowledgebaseDAOFactory.newConceptDAO()

  private[this] val overlayImage = ImageIO.read(overlayImageURL)
  private[this] val alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5F)

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
      WatermarkUtilities.saveImage(i, p.toFile, () => addExif(c, p))
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
      val g2 = image.getGraphics.asInstanceOf[Graphics2D]

      // Add watermark image
      val v = image.getWidth * overlayPercentWidth / overlay.getWidth
      val so = WatermarkUtilities.scaleOverlay(v, overlay)
      val x = round(image.getWidth * 0.05).toInt
      val y = round(image.getHeight * 0.85 - so.getHeight).toInt
      g2.setComposite(alphaComposite)
      g2.drawImage(so, x, y, null)
      g2.dispose()

      image
    }.toOption
  }

  def addExif(concept: Concept, path: Path): Unit = {
    if (useExiftool) {
      val cmd = Seq("exiftool",
          s"-Comment=Representative image for ${concept.getPrimaryConceptName.getName}",
          s"-Copyright=Copyright ${year} Monterey Bay Aquarium Research Institute",
          path.toString)
      try {
        cmd.!!
      }
      catch {
        case e: Exception => log.info(s"Failed to add EXIF to ${path}")
      }
    }
  }


}

/**
 * Example for editing EXIF metadata
 * https://svn.apache.org/repos/asf/commons/proper/imaging/trunk/src/test/java/org/apache/commons/imaging/examples/WriteExifMetadataExample.java
 *
 */
object KBImageMigrator {
  def main(args: Array[String]) {
    if (args.size != 2) {
      println(
        """
          | Process all concept media images and watermark them with an overlay for the external DSG
          |
          | Usage:
          |   KBImageMigrator [target] [overlay]
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
      val injector = StateLookup.GUICE_INJECTOR;
      injector.getInstance(classOf[ToolBelt])
    }
    val imageMigrator = new KBImageMigrator(target, overlayImageURL)
    val rootConcept = toolbelt.getKnowledgebaseDAOFactory.newConceptDAO().findRoot()
    run(imageMigrator, rootConcept)

  }

  private def run(imageMigrator: KBImageMigrator, concept: Concept): Unit = {
    imageMigrator.processConcept(concept)
    concept.getChildConcepts.asScala.foreach(run(imageMigrator, _))
  }
}
