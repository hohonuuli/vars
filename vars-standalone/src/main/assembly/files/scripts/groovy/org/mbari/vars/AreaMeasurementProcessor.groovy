package org.mbari.vars

import org.mbari.geometry.Point2D

import javax.imageio.ImageIO
import org.mbari.smith.Camera
import org.mbari.smith.Pixel
import vars.annotation.AreaMeasurement

/**
 * Converts AreaMeasurement annotations into actual area measurements. This processor makes the
 * following assumptions:
 *   1. The camera is oriented perpendicular to the surface (i.e. it's pointed straight down)
 *   2. The aspect ration of the image is square. (i.e. each side of the pixel has the same
 *      dimensions e.g. 1cm x 1cm; NOT 1cm x 2cm)
 *
 * References: 
 *   http://askville.amazon.com/calculate-area-irregular-polygon/AnswerViewer.do?requestId=2432521
 *   http://mathworld.wolfram.com/PolygonArea.html
 *
 * @author Brian Schlining
 * @since 2012-11-26
 */
class AreaMeasurementProcessor extends AbstractAreaMeasurementProcessor {

    Camera camera = null
    URL lastImage = null
    Integer imageWidth = null
    Integer imageHeight = null
    Double totalImageArea = null

    /**
     * If arguments are provided to the constructor then all images are assumed to view the same
     * area.
     *
     * @param metersWidth The width of the image
     * @param pixelsWidth
     */
    public AreaMeasurementProcessor(double cameraHeight,
                                    double alpha,
                                    double beta,
                                    double theta) {
        camera = Camera.fromJavaRadians(cameraHeight.doubleValue(),
                alpha.doubleValue(),
                beta.doubleValue(),
                theta.doubleValue())
    }


    private updateImageInfo(URL image) {
        if (lastImage != image) {
            println("Process: $image")
            lastImage = image
            def img = ImageIO.read(image)
            imageWidth = img.width
            imageHeight = img.height
            def cornersScala = Pixel.imageCorners(camera, imageWidth, imageHeight)
            def corners = []
            cornersScala.foreach({c ->
                corners << new Point2D<Integer>(c.x(), c.y())
            } as scala.Function1)
            def a = new AreaMeasurement(corners, "Total Area of Image")
            totalImageArea = calculateArea(a, image)
            println("\tTotal Image Area = $totalImageArea")
        }
    }

    private calculateArea(AreaMeasurement areaMeasurement, URL image) {
        def coords = areaMeasurement.coordinates
        def pixels = coords.collect { p ->
            try {
                return new Pixel(camera, imageWidth, imageHeight, p.x, p.y)
            }
            catch (Exception e) {
                println("ERROR on ${image}:\n\t${e.message}")
                return new Pixel(camera, 1, 1, 0, 0)
            }
        }
        def n = coords.size()

        def i1 = (0..<n).step(1)       // 0, 1, 2, ... , n
        def i2 = (1..<n).step(1) << 0  // 1, 2, 3, ... , n, 0

        def p1 = 0
        def p2 = 0
        for (i in 0..<n) {
            p1 = p1 + (pixels[i1[i]].xDistance() * pixels[i2[i]].yDistance())
            p2 = p2 + (pixels[i2[i]].xDistance() * pixels[i1[i]].yDistance())
        }
        // The sign just indicates the clockwise/counter-clockwises arrangement of
        // coordinates. So we just return the abs value
        return Math.abs((p1 - p2) / 2)
    }

    /**
     * Calculates area from areaMeasurement
     * @param areaMeasurement
     * @return THe planar area
     */
    def toArea(AreaMeasurement areaMeasurement, URL image) {
        updateImageInfo(image)
        calculateArea(areaMeasurement, image)
    }

}
