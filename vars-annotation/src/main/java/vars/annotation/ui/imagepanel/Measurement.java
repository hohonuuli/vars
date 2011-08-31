package vars.annotation.ui.imagepanel;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import vars.ILink;
import vars.LinkBean;
import vars.annotation.Association;
import vars.annotation.Observation;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Bean class for holding information about a measurement
 * @author Brian Schlining
 * @since 2011-08-30
 */
public class Measurement {

    public static final String MEASUREMENT_LINKNAME = "measurement in pixels [x0 y0 x1 y1 comment]";

    /** Filter for the distance measurement association */
    public static final Predicate<Association> IS_MEASUREMENT_PREDICATE = new Predicate<Association>() {
        @Override
        public boolean apply(Association input) {
            return MEASUREMENT_LINKNAME.equalsIgnoreCase(input.getLinkName());
        }
    };

    /** Transform to convert association to measurements */
    public static final Function<ILink, Measurement> LINK_TO_MEASUREMENT_TRANSFORM = new Function<ILink, Measurement>() {
        @Override
        public Measurement apply(ILink input) {
            return Measurement.fromLink(input);
        }
    };


    private final int x0;
    private final int y0;
    private final int x1;
    private final int y1;
    private String comment;

    public Measurement(int x0, int y0, int x1, int y1) {
        this(x0, y0, x1, y1, null);
    }

    /**
     *
     * @param x0 x-coordinate of the starting point
     * @param y0 y-coordinate of the starting point
     * @param x1 x-coordinate of the ending point
     * @param y1 y-coordinate of the ending point
     * @param comment Some description about the measurement
     */
    public Measurement(int x0, int y0, int x1, int y1, String comment) {
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
        this.comment = comment;
    }

    public int getX0() {
        return x0;
    }

    public int getY0() {
        return y0;
    }

    public int getX1() {
        return x1;
    }

    public int getY1() {
        return y1;
    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Measurement that = (Measurement) o;

        if (x0 != that.x0) return false;
        if (x1 != that.x1) return false;
        if (y0 != that.y0) return false;
        if (y1 != that.y1) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = x0;
        result = 31 * result + y0;
        result = 31 * result + x1;
        result = 31 * result + y1;
        return result;
    }

    @Override
    public String toString() {
        return "Measurement{" +
                "x0=" + x0 +
                ", y0=" + y0 +
                ", x1=" + x1 +
                ", y1=" + y1 +
                '}';
    }

    /**
     * Transforms the measurement into an ILink object. The link can be converted to an
     * Association using:
     * <pre>
     *     AnnotationFactory f = // get an annotationFactory object
     *     Association = f.newAssociation(measurement.toLink());
     * </pre>
     * @return
     */
    public ILink toLink() {
        String c = "";
        if (comment != null && !comment.isEmpty()) {
            c = " " + comment;
        }
        return new LinkBean(MEASUREMENT_LINKNAME, ILink.VALUE_SELF,
                x0 + " " + y0 + " " + x1 + " " + y1 + c);
    }

    public static Measurement fromLink(ILink link) {
        String[] parts = link.getLinkValue().split(" ");
        if (parts.length < 4) {
            throw new IllegalArgumentException("The Association does not contain 2 pixel coordinates");
        }
        Integer x0 = Integer.valueOf(parts[0]);
        Integer y0 = Integer.valueOf(parts[1]);
        Integer x1 = Integer.valueOf(parts[2]);
        Integer y1 = Integer.valueOf(parts[3]);
        int idx = parts[0].length() + parts[1].length() + parts[2].length() + parts[3].length() + 4;
        String comment = link.getLinkValue().substring(idx);
        return new Measurement(x0, y0, x1, y1, comment);

    }

    public static Collection<Measurement> fromObservation(Observation observation) {
        Collection<Association> associations = Collections2.filter(observation.getAssociations(), IS_MEASUREMENT_PREDICATE);
        Collection<Measurement> measurements = Collections2.transform(associations, LINK_TO_MEASUREMENT_TRANSFORM);
        return new ArrayList<Measurement>(measurements);
    }
}
