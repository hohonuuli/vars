package org.mbari.geometry;

/**
 * 4D point (e.g. space-time)
 *
 * A type param is for space dimensions
 * B type param is for time dimensions
 *
 * @author Brian Schlining
 * @since Sep 28, 2010
 */
public class Point4D<A extends Number, B> extends Point3D<A> {

    private final B t;

    public Point4D(A x, A y, A z, B t) {
        super(x, y, z);
        this.t = t;
    }

    public B getT() {
        return t;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Point4D<A, B> other = (Point4D<A, B>) obj;
        if (!super.equals(obj) && this.t != other.t && (this.t == null || !this.t.equals(other.t))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7 + super.hashCode();
        hash = 53 * hash + (this.t != null ? this.t.hashCode() : 0);
        return hash;
    }


    @Override
    public String toString() {
        return getX() + "," + getY() + "," + getZ() + "," + getT();
    }
}
