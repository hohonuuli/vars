/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mbari.geometry;


/**
 * Represents a 3D point
 * @author brian
 */
public class Point3D<T extends Number> extends Point2D<T> {

    private final T z;

    public Point3D(T x, T y, T z) {
        super(x, y);
        this.z = z;
    }

    public T getZ() {
        return z;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Point3D<T> other = (Point3D<T>) obj;
        if (!super.equals(obj) && this.z != other.z && (this.z == null || !this.z.equals(other.z))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7 + super.hashCode();
        hash = 53 * hash + (this.z != null ? this.z.hashCode() : 0);
        return hash;
    }

    /**
     *
     * @param that The other point
     * @return The 3D Euclidian distance between 2 points. Assumes units between
     *      X,Y and Z as well as the 2 points are the same.
     */
    public Double getDistance(final Point3D<? extends Number> that) {

        final double x1 = that.getX().doubleValue();
        final double y1 = that.getY().doubleValue();
        final double z1 = that.getZ().doubleValue();
        final double x0 = getX().doubleValue();
        final double y0 = getY().doubleValue();
        final double z0 = getZ().doubleValue();
        final double dx = Math.pow((x1 - x0), 2);
        final double dy = Math.pow((y1 - y0), 2);
        final double dz = Math.pow((z1 - z0), 2);

        return Math.sqrt(dx + dy + dz);
    }

    @Override
    public String toString() {
        return getX() + "," + getY() + "," + getZ();
    }




}
