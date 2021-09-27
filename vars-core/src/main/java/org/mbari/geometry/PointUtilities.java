/*
 * @(#)PointUtilities.java   2010.03.31 at 02:22:40 PDT
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



package org.mbari.geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author brian
 */
public class PointUtilities {

    private static final Point2D<Double> ORIGIN = new Point2D<Double>(0D, 0D);

    /**
     * Calculate the centroid of a collection of points
     *
     * @param points
     * @return A point representing the centroid
     */
    public static Point2D<Double> centroid(Collection<? extends Point2D> points) {

        double x = 0;
        double y = 0;

        for (Point2D p : points) {
            x += p.getX().doubleValue();
            y += p.getY().doubleValue();
        }

        x = x / points.size();
        y = y / points.size();

        return new Point2D<Double>(x, y);
    }

    /**
     * Convience utitity to calculate the lowest bound for points in a list
     *  (in image coordinates - 0,0 is the origin in images at the upper left.)
     *
     * @param points A collection of points
     * @return a Point2d<Integer> representing the lower right
     */
    public static Point2D maximum(Collection<? extends Point2D> points) {
        Point2D maxX = Collections.max(points, new XComparator());
        Point2D maxY = Collections.max(points, new YComparator());
        return new Point2D(maxX.getX(), maxY.getY());
    }

    /**
     * Convience utitity to calculate the lowest bound for points in a list
     * (in image coordinates - 0,0 is the origin in images at the upper left.)
     *
     * @param points A collection of points
     * @return a Point2d<Integer> representing the upper left
     */
    public static Point2D minimum(Collection<? extends Point2D> points) {
        Point2D minX = Collections.min(points, new XComparator());
        Point2D minY = Collections.min(points, new YComparator());
        return new Point2D(minX.getX(), minY.getY());
    }

    /**
     *
     * @param points
     * @param angleRadians
     * @param pivot
     * @return
     */
    public static List<Point2D<Double>> rotate(List<? extends Point2D> points, double angleRadians, Point2D pivot) {

        double pivotX = pivot.getX().doubleValue();
        double pivotY = pivot.getY().doubleValue();

        // Use pivot point as the new origin
        List<Point2D<Double>> translatedPoints = translate(points, -pivotX, -pivotY);

        // Apply rotation around pivot
        List<Point2D> rotatedAndTranslatedPoints = new ArrayList<Point2D>();
        for (Point2D<Double> p : translatedPoints) {
            double newX = p.getX() * Math.cos(angleRadians) - p.getY() * Math.sin(angleRadians);
            double newY = p.getX() * Math.sin(angleRadians) + p.getY() * Math.cos(angleRadians);
            rotatedAndTranslatedPoints.add(new Point2D<Double>(newX, newY));
        }

        // Move points back to original position
        return translate(rotatedAndTranslatedPoints, pivotX, pivotY);

    }

    /**
     *
     * @param points
     * @param angleRadians
     * @return
     */
    public static List<Point2D<Double>> rotate(List<? extends Point2D> points, double angleRadians) {
        return rotate(points, angleRadians, ORIGIN);
    }

    /**
     *
     * @param points
     * @param scale
     * @param center
     * @return
     */
    public static List<Point2D<Double>> scale(List<? extends Point2D> points, Double scale, Point2D center) {

        double centerX = center.getX().doubleValue();
        double centerY = center.getY().doubleValue();

        // Move points to be around center
        List<Point2D<Double>> translatedPoints = translate(points, -centerX, -centerY);

        // Apply scaling
        List<Point2D<Double>> scaledPoints = new ArrayList<Point2D<Double>>(translatedPoints.size());
        for (Point2D<Double> p : translatedPoints) {
            scaledPoints.add(new Point2D<Double>(p.getX() * scale, p.getY() * scale));
        }

        // Move points back to original position
        return translate(scaledPoints, centerX, centerY);
    }

    /**
     *
     * @param points
     * @param scale
     * @return
     */
    public static List<Point2D<Double>> scale(List<? extends Point2D> points, Double scale) {
        return scale(points, scale, ORIGIN);
    }

    /**
     *
     * @param points
     * @param x
     * @param y
     * @return
     */
    public static List<Point2D<Double>> translate(List<? extends Point2D> points, double x, double y) {
        List<Point2D<Double>> translatedPoints = new ArrayList<Point2D<Double>>(points.size());
        for (Point2D p : points) {
            translatedPoints.add(new Point2D<Double>(p.getX().doubleValue() + x,
                                             p.getY().doubleValue() + y));
        }

        return translatedPoints;
    }
}
