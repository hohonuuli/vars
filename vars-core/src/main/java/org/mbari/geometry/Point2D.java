/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mbari.geometry;

/**
 * Represents a 2D point. It's assumed you are using the same units for X and Y
 * @author brian
 */
public class Point2D<T extends Number> {

    private final T x;
    private final T y;

    public Point2D(T x, T y) {
        this.x = x;
        this.y = y;
    }

    public T getX() {
        return x;
    }

    public T getY() {
        return y;
    }



    /**
     * Convert to Java's AWT Point2D
     * @return an AWT Point2D.Double object
     */
    public java.awt.geom.Point2D toJavaPoint2D() {
        return new java.awt.geom.Point2D.Double(x.doubleValue(), y.doubleValue());
    }

    /**
     * Convert from Java's AWT Point2D to our version. Although it would seem more
     * logical to do this using a constructor that takes an java.awt.geom.Point2D
     * as an argument, we don't because of Java generics limitations. Instead
     * we use this factory method.
     *
     * @param p The point2D object to convert
     * @return
     */
    public static Point2D<Double> fromJavaPoint2D(java.awt.geom.Point2D p) {
        return new Point2D<Double>(p.getX(), p.getY());
    }

    /**
     * Calculates the direction between 2 2D points. THe direction is from this point
     * to <b>that</b> point.
     * @param that The point to calculate the direction to.
     * @return The direction in Math angle radians
     */
    public Double direction(final Point2D<? extends Number> that) {

        final double x1 = that.getX().doubleValue();
        final double y1 = that.getY().doubleValue();
        final double x0 = getX().doubleValue();
        final double y0 = getY().doubleValue();

        return Math.atan2(y1 - y0, x1 - x0);
    }

    /**
     *
     * @param that The other point
     * @return The 2D Euclidian distance between 2 points. Assumes units between X and Y
     *      as well as the 2 points are the same.
     */
    public Double distance(final Point2D<? extends Number> that) {

        final double x1 = that.getX().doubleValue();
        final double y1 = that.getY().doubleValue();
        final double x0 = getX().doubleValue();
        final double y0 = getY().doubleValue();
        final double dx = Math.pow((x1 - x0), 2);
        final double dy = Math.pow((y1 - y0), 2);

        return Math.sqrt(dx + dy);
    }

    @Override
    public String toString() {
        return getX() + "," + getY();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Point2D point2D = (Point2D) o;

        if (x != null ? !x.equals(point2D.x) : point2D.x != null) {
            return false;
        }
        if (y != null ? !y.equals(point2D.y) : point2D.y != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = x != null ? x.hashCode() : 0;
        result = 31 * result + (y != null ? y.hashCode() : 0);
        return result;
    }
}
