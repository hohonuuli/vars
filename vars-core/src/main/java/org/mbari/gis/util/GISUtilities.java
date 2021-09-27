/*
 * @(#)GISUtilities.java   2011.12.10 at 08:57:52 PST
 *
 * Copyright 2009 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



//$Header: /home/cvs/iag/brian/mbari/src/main/java/org/mbari/gis/util/GISUtilities.java,v 1.1 2006/01/09 21:16:56 brian Exp $
package org.mbari.gis.util;

import java.awt.geom.Point2D;

/**
 * <p>Static methods useful for GIS Applications</p><hr>
 *
 */
public class GISUtilities {

    private static final double ANGLE_90_DEGREES = Math.PI / 2;
    private static final double ANGLE_360_DEGREES = Math.PI * 2;
    private static final double ANGLE_180_DEGREES = Math.PI;

    /**
     * Instantiation is not allowed
     */
    private GISUtilities() {}

    /**
     * Convert an angle (in radians) from geographic notation (0 is North, positive
     * values are clockwise) to math notation (0 is east, positive values are
     * counter-clockwise)
     *
     * @param angleInRadians Angle in geographic notation (radians)
     * @return
     */
    public static double geoToMath(double angleInRadians) {
        double a = (ANGLE_90_DEGREES) - (angleInRadians % (ANGLE_360_DEGREES));
        if (a <= -ANGLE_180_DEGREES) {
            a += ANGLE_360_DEGREES;
        }

        return a;
    }

    /**
     * Converts a set of Longitude and Latitude co-ordinates to UTM for the
     * WGS84 ellipsoid
     *
     * @param latitude A double value for the latitude to be converted.
     * @param longitude A double value for the longitude to be converted.
     * @param zoneNumber The desired zone number to use.
     *
     * @return A java.awt.Point where getX() returns the Eastings and getY()
     * returns the Northings
     * @deprecated Use Geotoolkit instead
     */
    @Deprecated
    public static Point2D geoToUtm(double longitude, double latitude, int zoneNumber) {

        double a = 6378137.0d;              // WGS84 ellipsoid radius
        double eccSquared = 0.00669438d;    // WGS84 eccsq
        double k0 = 0.9996;

        double longOrigin;
        double eccPrimeSquared;
        double N, T, C, A, M;

        //Make sure the longitude is between -180.00 .. 179.9
        double longTemp = (longitude + 180) - ((int) ((longitude + 180) / 360)) * 360 - 180;    // -180.00 .. 179.9;

        double latRad = Math.toRadians(latitude);
        double longRad = Math.toRadians(longitude);
        double longOriginRad;

        longOrigin = (zoneNumber - 1) * 6 - 180 + 3;    //+3 puts origin in middle of zone
        longOriginRad = Math.toRadians(longOrigin);

        eccPrimeSquared = (eccSquared) / (1 - eccSquared);

        N = a / Math.sqrt(1 - eccSquared * Math.sin(latRad) * Math.sin(latRad));
        T = Math.tan(latRad) * Math.tan(latRad);
        C = eccPrimeSquared * Math.cos(latRad) * Math.cos(latRad);
        A = Math.cos(latRad) * (longRad - longOriginRad);

        M = a *
            ((1 - eccSquared / 4 - 3 * eccSquared * eccSquared / 64 - 5 * eccSquared * eccSquared * eccSquared / 256) *
             latRad - (3 * eccSquared / 8 + 3 * eccSquared * eccSquared / 32 +
                 45 * eccSquared * eccSquared * eccSquared / 1024) * Math
                     .sin(2 * latRad) + (15 * eccSquared * eccSquared / 256 +
                         45 * eccSquared * eccSquared * eccSquared / 1024) * Math
                             .sin(4 * latRad) - (35 * eccSquared * eccSquared * eccSquared / 3072) * Math.sin(6 * latRad));

        double utmEasting = (double) (k0 * N *
                                      (A + (1 - T + C) * A * A * A / 6.0d +
                                       (5 - 18 * T + T * T + 72 * C - 58 * eccPrimeSquared) * A * A * A * A * A /
                                       120.0d) + 500000.0d);

        double utmNorthing = (double) (k0 *
                                       (M +
                                        N * Math.tan(latRad) *
                                        (A * A / 2 + (5 - T + 9 * C + 4 * C * C) * A * A * A * A / 24.0d +
                                         (61 - 58 * T + T * T + 600 * C - 330 * eccPrimeSquared) * A * A * A * A * A *
                                         A / 720.0d)));
        if (latitude < 0.0f) {
            utmNorthing += 10000000.0f;    //10000000 meter offset for southern hemisphere
        }

        return new Point2D.Double(utmEasting, utmNorthing);
    }

    /**
     * Converts a set of Longitude and Latitude co-ordinates to UTM for the
     * WGS84 ellipsoid
     *
     * @param latitude A double value for the latitude to be converted.
     * @param longitude A double value for the longitude to be converted.
     *
     * @return A java.awt.Point where getX() returns the Eastings and getY()
     * returns the Northings
     */
    public static Point2D geoToUtm(double longitude, double latitude) {
        return geoToUtm(longitude, latitude, geoToUtmZone(longitude, latitude));
    }

    /**
     * Calculate the prefered UTM zone number for a given latitude and longitude
     * @param longitude
     * @param latitude
     * @return The UTM zone number
     */
    public static int geoToUtmZone(double longitude, double latitude) {

        //Make sure the longitude is between -180.00 .. 179.9
        double longTemp = (longitude + 180) - ((int) ((longitude + 180) / 360)) * 360 - 180;    // -180.00 .. 179.9;
        int zoneNumber = (int) ((longTemp + 180) / 6) + 1;

        if ((latitude >= 56.0f) && (latitude < 64.0f) && (longTemp >= 3.0f) && (longTemp < 12.0f)) {
            zoneNumber = 32;
        }

        // Special zones for Svalbard
        if ((latitude >= 72.0f) && (latitude < 84.0f)) {
            if ((longTemp >= 0.0f) && (longTemp < 9.0f)) {
                zoneNumber = 31;
            }
            else if ((longTemp >= 9.0f) && (longTemp < 21.0f)) {
                zoneNumber = 33;
            }
            else if ((longTemp >= 21.0f) && (longTemp < 33.0f)) {
                zoneNumber = 35;
            }
            else if ((longTemp >= 33.0f) && (longTemp < 42.0f)) {
                zoneNumber = 37;
            }
        }

        return zoneNumber;
    }

    /**
     * Convert an angle (in radians) from math notation (0 is east, positive
     * values are counter-clockwise) to geographic notation (0 is North, positive
     * values are clockwise).
     *
     * @param angleInRadians Angle in math notation (radians)
     * @return
     */
    public static double mathToGeo(double angleInRadians) {
        double a = (ANGLE_90_DEGREES) - (angleInRadians % ANGLE_360_DEGREES);
        if (a < 0) {
            a += ANGLE_360_DEGREES;
        }

        return a;
    }
}
