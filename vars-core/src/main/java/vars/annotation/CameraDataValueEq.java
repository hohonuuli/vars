package vars.annotation;

/**
 * @author Brian Schlining
 * @since 2015-10-21T09:32:00
 */
public class CameraDataValueEq implements IValueEq<CameraData> {

    private final double eps;

    /**
     *
     * @param eps The precision to be used when comparing floating points
     */
    public CameraDataValueEq(double eps) {
        this.eps = eps;
    }

    public CameraDataValueEq() {
        this(10e-6);
    }

    @Override
    public boolean equal(CameraData a, CameraData b) {
        return same(a.getDirection(), b.getDirection())
                && same(a.getFieldWidth(), b.getFieldWidth())
                && same(a.getFocus(), b.getFocus())
                && same(a.getHeading(), b.getHeading())
                && same(a.getImageReference(), b.getImageReference())
                && same(a.getIris(), b.getIris())
                && same(a.getName(), b.getName())
                && same(a.getPitch(), b.getPitch())
                && same(a.getRoll(), b.getRoll())
                && same(a.getViewHeight(), b.getViewHeight())
                && same(a.getViewUnits(), b.getViewUnits())
                && same(a.getViewWidth(), b.getViewWidth())
                && same(a.getX(), b.getX())
                && same(a.getXYUnits(), b.getXYUnits())
                && same(a.getY(), b.getY())
                && same(a.getZ(), b.getZ())
                && same(a.getZoom(), b.getZoom())
                && same(a.getZUnits(), b.getZUnits());
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

    private boolean same(String a, String b) {
        if (a == null && b == null) {
            return true;
        }
        else if (a == null || b == null) {
            return false;
        }
        else {
            return a.equals(b);
        }
    }
}
