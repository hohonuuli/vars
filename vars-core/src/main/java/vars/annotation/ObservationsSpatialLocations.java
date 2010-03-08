package vars.annotation;

import com.google.common.collect.ImmutableMap;
import org.mbari.geometry.Point2D;
import org.mbari.gis.util.GISUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.VARSException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Calculates the Spatial positions of all Observations within a {@link vars.annotation.VideoFrame}.
 * This version was written for the SIMPA project so it may not be generically
 * applicable.
 *
 */
public class ObservationsSpatialLocations {

    private final VideoFrame videoFrame;
    private Integer widthInPixels;
    private Integer heightInPixels;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private Map spatialLocations;

    public ObservationsSpatialLocations(VideoFrame videoFrame) {
        this.videoFrame = videoFrame;
    }

    public void process() {
        if (spatialLocations == null) {
            Map<Observation, Point2D<Double>> points = new HashMap<Observation, Point2D<Double>>();
            for(Observation observation : videoFrame.getObservations()) {
                points.put(observation, process(observation));
            }
            spatialLocations = ImmutableMap.copyOf(points);
        }
    }

    public Point2D<Double> process(Observation observation) {

        try {
            processImageReference();
        } catch (IOException ex) {
            return new Point2D<Double>(Double.NaN, Double.NaN);
        }

      /* *********************************************************************
       * Calculate the position (in meters) of the annotation relative
       * to the center of the origin tile.
       *
       * ** Pixel Coordinates. Origin is top left.
       *
       * m = width in pixels
       * n = height in pixels
       * u = pixel position along u axis (+Right/-Left) origin top left
       * v = pixel position along v axis (-Up/+Down) origin top left
       *
       * ** Spatial coordinates. Origin is image center
       *
       * w = width in meters
       * h = height in meters
       * x = east-west position in meters (+E/-W) relative to center of origin tile
       * y = north-south position in meters (+N/-S) relative to center of origin tile
       * r = radius in meters from center to any corner
       * theta = relative heading in radians
       *
       ******************************************************************** */

        // Pixel coordinates
        final Integer m = widthInPixels;     // Number of pixel columns in image
        final Integer n = heightInPixels;    // Number of pixel rows in image
        if (m == null || n == null) {
            /*
            * This would occur if we were unable to read the image. For example
            * if it was hosted on a remote server and the server was down
            */
            throw new VARSException("Missing the size of a dimension (in pixels) for " + observation);
        }
        final int u = (int) Math.round(observation.getX()); // X position of annotation within image in pixels
        final int v = (int) Math.round(observation.getY()); // Y position of annotation within image in pixels

        // Spatial coordinates
        final CameraData cameraData = videoFrame.getCameraData();
        final double w = cameraData.getViewWidth();         // Width of image in meters
        final double h = cameraData.getViewHeight();        // Height of image in meters
        final double x = cameraData.getX();                 // X Center of image in meters relative to origin tile
        final double y = cameraData.getY();                 // Y Center of image in meters relative to origin tile

        final double m0 = m / 2D;                           // X pixel at center of image
        final double n0 = n / 2D;                           // Y pixel at center of image

        final double du0 = u - m0;                          // du of annotation relative to center in pixels
        final double dv0 = n0 - v;                          // dv of annotation relative to center in pixels (flip sign too)

        final double dx1 = du0 * w / m;                     // X meters from annotation to center of source image
        final double dy1 = dv0 * h / n;                     // Y meters from annotation to center of source image.

        final double phi = Math.atan2(dy1, dx1);            // Angle from center to annotation relative to image 'top'
        final double theta = phi - cameraData.getHeading(); // Math angle, in radians, from center of tile, relative to initial heading of origin tile to annotation
        final double r = Math.sqrt((dx1 * dx1) + (dy1 * dy1));  // Meters from annotation to center of source image

        final double xInMeters = x + r * Math.cos(theta);   // X meters from annotation to origin tile center
        final double yInMeters = y + r * Math.sin(theta);   // Y meters from annotation to origin tile center

        if (log.isDebugEnabled()) {
            log.debug("SpatialLocation: " + this + "\n\t" +
                    "Image:\n\t" +
                    "\tDimensions:                                [" + m + "px x " + n + "px]\n\t" +
                    "\tSize:                                      [" + w +  "m x " + h + "m]\n\t" +
                    "\tDistance from center to origin:            [" + x + "m, " + y + "m]\n\t" +
                    "\tHeading:                                    " + Math.toDegrees(cameraData.getHeading()) + " degrees\n\t" +
                    "Annotation:\n\t" +
                    "\tPixel:                                     [" + u + ", " + v + "]\n\t" +
                    "\tDistance from center to annotation:        [" + dx1 + "m, " + dy1 + "m]\n\t" +
                    "\tDistance from origin to annotation:        [" + xInMeters + "m, " + yInMeters + "m]\n\t" +
                    "\tDistance from center to annotation:         " + r + "m\n\t" +
                    "\tMath angle from center to annotation:       " + Math.toDegrees(theta) +  "\n\t" +
                    "\tgeographic angle from center to annotation: " + Math.toDegrees(GISUtilities.mathToGeo(theta)) + "\n\t");
        }

        return new Point2D<Double>(xInMeters, yInMeters);
    }

    /**
     * Reads the image width and height
     * @throws IOException
     */
    private void processImageReference() throws IOException {
        if (widthInPixels == null && heightInPixels == null) {
            String imageReference = videoFrame.getCameraData().getImageReference();
            if (imageReference != null) {
                URL imageUrl = new URL(imageReference);

                BufferedImage image = ImageIO.read(imageUrl);
                widthInPixels = image.getWidth();
                heightInPixels = image.getHeight();
            }
        }
    }

    public Integer getHeightInPixels() {
        process();
        return heightInPixels;
    }

    public Integer getWidthInPixels() {
        process();
        return widthInPixels;
    }

    public Map<Observation, Point2D<Double>> getSpatialLocations() {
        process();
        return spatialLocations;
    }

}
