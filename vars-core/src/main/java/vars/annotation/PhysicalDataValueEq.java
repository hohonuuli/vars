package vars.annotation;

import java.util.Comparator;

/**
 * Compares equality of 2 PhysicalData objects using values (but no primaryKey or logDate)
 * @author Brian Schlining
 * @since 2015-10-20T15:04:00
 */
public class PhysicalDataValueEq implements IValueEq<PhysicalData> {

    private final double eps;

    /**
     *
     * @param eps The precision to be used when comparing floating points
     */
    public PhysicalDataValueEq(double eps) {
        this.eps = eps;
    }

    /**
     * Defaults constructor uses precision of 10e-6
     */
    public PhysicalDataValueEq() {
        this(10e-6);
    }

    @Override
    public boolean equal(PhysicalData a, PhysicalData b) {
        return same(a.getAltitude(), b.getAltitude())
                && same(a.getDepth(), b.getDepth())
                && same(a.getLatitude(), b.getLatitude())
                && same(a.getLight(), b.getLight())
                && same(a.getLongitude(), b.getLongitude())
                && same (a.getOxygen(), b.getOxygen())
                && same(a.getSalinity(), b.getSalinity())
                && same(a.getTemperature(), b.getTemperature());
    }

    private boolean same(Number a, Number b) {
        if (a == null && b == null) {
            return true;
        }
        else if (a == null || b == null) {
            return false;
        }
        else {
            return Math.abs(a.doubleValue() - b.doubleValue()) <= eps;
        }
    }
}
