package org.mbari.varspub

import java.io.{File, FileOutputStream}
import javax.imageio.ImageIO

import org.junit.runner.RunWith
import org.mbari.vars.varspub.WatermarkUtilities
import org.scalatest.junit.JUnitRunner
import org.scalatest.{Matchers, FlatSpec}

/**
 *
 *
 * @author Brian Schlining
 * @since 2015-03-24T12:18:00
 */
@RunWith(classOf[JUnitRunner])
class WatermarkUtilitiesSpec extends FlatSpec with Matchers {

  "WatermarkUtilities" should "write png metadata" in {

    val png = ImageIO.read(getClass.getResource("/images/Opisthoteuthis_spA_01.png"))
    val metadata = Map("Title" -> "This is a title",
      "Author" -> "Brian Schlining",
      "Copyright" -> "2015",
      "Software" -> getClass.getSimpleName)
    val bytes = WatermarkUtilities.addMetadataAsPNG(png, metadata)
    val os = new FileOutputStream(new File("target", s"${getClass.getSimpleName}.png"))
    os.write(bytes)
    os.close()
  }

}
