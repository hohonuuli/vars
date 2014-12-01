package org.mbari.vars.varspub

import java.awt._
import java.awt.image.BufferedImage
import java.net.URL
import java.nio.file.{Files, Paths, Path}
import java.sql.{ResultSet, DriverManager}
import java.text.SimpleDateFormat
import java.util.Date
import javax.imageio.ImageIO

import org.mbari.awt.image.ImageUtilities
import org.slf4j.LoggerFactory
import vars.annotation.ui.ToolBelt

import scala.collection.mutable
import scala.math._
import scala.util.Try

/**
 *
 *
 * @author Brian Schlining
 * @since 2014-11-21T11:01:00
 */
class AnnoImageMigrator(target: Path, overlayImageURL: URL, pathKey: String = "framegrabs",
                        overlayPercentWidth: Double = 0.4)(implicit toolBelt: ToolBelt) {

  private[this] val log = LoggerFactory.getLogger(classOf[AnnoImageMigrator])
  private[this] val dateFormat = new SimpleDateFormat("yyyy-MM-dd")
  private[this] val alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5F)
  private[this] val overlayImage = ImageIO.read(overlayImageURL)

  implicit class ResultSetRowMapper(resultSet: ResultSet) {

    def map[A](rowMapper: ResultSet => A): Seq[A] = {
      // -- Read the results.
      val results = new mutable.ArrayBuffer[A]
      while (resultSet.next()) {
        results += rowMapper(resultSet)
      }
      results.toSeq
    }

  }

  private[this] val internalConnection = DriverManager.getConnection("jdbc:jtds:sqlserver://equinox.shore.mbari.org:1433/VARS",
      "everyone", "quest")

  private[this] val externalConnection = DriverManager.getConnection("jdbc:jtds:sqlserver://dione.mbari.org:51001/VARS",
      "everyone", "guest")

  def run(): Unit = {
    for {
      (e, i) <- mapURLs()
      p <- toTargetPath(e, i)
    } {
      try {
        val image = ImageIO.read(new URL(i))
        val watermarked = watermark(image, overlayImage)
        ImageUtilities.saveImage(watermarked, p.toFile)
        Thread.sleep(50) // REQUIRED!! Otherwise Apache on Eione ends up hanging after a few hundred files
      }
      catch {
        case e: Exception => log.info("Failed to watermark {}", e)
      }
    }

  }


  private def toTargetPath(external: String, internal: String): Option[Path] = {
    val externalURL = new URL(external)
    if (imageExistsAt(externalURL)) None
    else {
      val idx = internal.indexOf(pathKey)
      val parts = internal.substring(idx + pathKey.size).split("/")
      val externalTarget = Paths.get(target.toString, parts:_*)
      val externalDir = externalTarget.getParent
      if (Files.notExists(externalDir)) {
        log.info("Creating {}", externalDir)
        Files.createDirectories(externalDir)
      }
      Some(externalTarget)
    }
  }


  /**
   * Create a Map of [external image URL] -> [internal image URL] for all annotation images
   * @return
   */
  private def mapURLs(): Seq[(String, String)] = {
    log.info("Starting database lookup")
    val urls = new mutable.ArrayBuffer[(String, String)]
    val external = externalConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
    val internal = internalConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
    val ers = external.executeQuery(
      """
        |SELECT
        |  id,
        |  StillImageURL
        |FROM
        |  CameraData
        |WHERE
        |  StillImageURL IS NOT NULL
        |ORDER BY
        |  id DESC
      """.stripMargin)
    while (ers.next()) {
      val (id, externalURL) = (ers.getLong(1), ers.getString(2))
      val irs = internal.executeQuery(
        s"""
          |SELECT
          |  id,
          |  StillImageURL
          |FROM
          |  CameraData
          |WHERE
          |  id = $id
        """.stripMargin)
      if (irs.next()) {
        urls += externalURL -> irs.getString(2)
      }
      irs.close()
    }
    external.close()
    internal.close()
    log.info("Finished database lookup")
    urls
  }

  private def imageExistsAt(url: URL): Boolean = {
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

  def watermark(image: BufferedImage, overlay: BufferedImage) : BufferedImage = {
    val i0 = addWatermarkImage(image, overlay)
    val text = dateFormat.format(new Date)
    addWatermarkText(i0, text, 0.15, 0.5)
  }


  def addWatermarkText(image: BufferedImage, text: String, rightPercent: Double, bottomPercent: Double, color: Color = Color.WHITE,
                       font: Font = new Font("Arial", Font.BOLD, 16)): BufferedImage = {

    val g2 = image.getGraphics.asInstanceOf[Graphics2D]
    g2.setColor(color)
    g2.setComposite(alphaComposite)
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
    g2.setFont(font)
    val fontMetrics = g2.getFontMetrics
    val rect = fontMetrics.getStringBounds(text, g2)
    val w = image.getWidth
    val h = image.getHeight
    val x = (w - w * rightPercent - rect.getWidth).toFloat
    val y = (h - h * bottomPercent).toFloat
    g2.drawString(text, x, y)
    g2.dispose()
    image
  }

  def addWatermarkImage(image: BufferedImage, overlay: BufferedImage): BufferedImage = {
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




}
