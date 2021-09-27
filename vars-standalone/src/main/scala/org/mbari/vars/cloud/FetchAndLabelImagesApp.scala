package org.mbari.vars.cloud

import java.io.File
import java.net.URL
import javax.imageio.ImageIO

import org.imgscalr.Scalr
import mbarix4j.awt.image.ImageUtilities
import org.slf4j.LoggerFactory
import vars.ToolBox
import vars.annotation.ui.ToolBelt
import scala.collection.JavaConverters._
import scala.util.control.NonFatal

/**
 *
 *
 * @author Brian Schlining
 * @since 2015-07-16T08:51:00
 */
class FetchAndLabelImagesApp(conceptName: String, targetRoot: String, imageWidth: Int = 1920)(implicit toolBelt: ToolBelt) {

  private[this] val log = LoggerFactory.getLogger(getClass)

  lazy val target = {
    val f = new File(targetRoot)
    if (!f.exists()) f.mkdirs()
    f
  }

  lazy val concept = {
    val dao = toolBelt.getKnowledgebaseDAOFactory.newConceptDAO()
    dao.findByName(conceptName)
  }

  /**
   * Looks up images for a concept and descendant. Maps to an image name
   * @return a map of [image url to png -> name of file to save]
   */
  private def findImages(): Map[String, String] = {

    // --- Lookup desdendants
    val conceptDAO = toolBelt.getKnowledgebaseDAOFactory.newConceptDAO()
    val observationDAO = toolBelt.getAnnotationDAOFactory.newObservationDAO()
    val observations = observationDAO.findAllByConcept(concept, true, conceptDAO).asScala

    val urlName1 = for (obs <- observations) yield {
      val vf = obs.getVideoFrame
      try {
        val url = vf.getCameraData.getImageReference
        if (url.toLowerCase.startsWith("http")) {
          val videoArchiveName = vf.getVideoArchive.getName
          val timecode = vf.getTimecode.replace(':', '_')
          val name = s"${obs.getConceptName}-$videoArchiveName-$timecode.png"
          val pngUrl = url.replace(".jpg", ".png")
          Option(pngUrl -> name)
        }
        else {
          None
        }
      }
      catch {
        case NonFatal(e) => {
          log.debug(s"Failed to map $obs")
          None
        }
      }
    }
    urlName1.flatten.toMap
  }

  private def saveImage(url: String, filename: String): Unit = {
    // --- Read as bufferedimage
    val bufferedImage = ImageIO.read(new URL(url))

    // --- Rescale to HD size
    val scaledImage = Scalr.resize(bufferedImage, Scalr.Method.ULTRA_QUALITY, imageWidth)

    // --- Save
    ImageUtilities.saveImage(scaledImage, new File(target, filename))

    log.debug(s"Copied $url to $filename")
  }

  def apply(): Unit = {
    val m = findImages().par
    m.foreach({case (u, f) => {
      try {
        saveImage(u, f)
      }
      catch {
        case NonFatal(e) => log.debug(s"Failed to write $u to $f")
      }
    }})
  }



}



object FetchAndLabelImagesApp {
  def main(args: Array[String]) {
    val conceptName = args(0)
    val targetRoot = args(1)
    val toolBox = new ToolBox
    implicit val toolBelt = toolBox.getToolBelt
    val app = new FetchAndLabelImagesApp(conceptName, targetRoot)
    app()
  }


}
