/*
 * @(#)Measurement.java   2012.11.26 at 08:48:29 PST
 *
 * Copyright 2011 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



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
 * Bean class for holding information about a linear measurement
 * @author Brian Schlining
 * @since 2011-08-30
 */
public class Measurement {

    /**  */
    public static final String MEASUREMENT_LINKNAME = "measurement in pixels [x0 y0 x1 y1 comment]";

    /** Transform to convert association to measurements */
    public static final Function<ILink, Measurement> LINK_TO_MEASUREMENT_TRANSFORM = new Function<ILink,
        Measurement>() {

        @Override
        public Measurement apply(ILink input) {
            return Measurement.fromLink(input);
        }
    };

    /** Filter for the distance measurement association */
    public static final Predicate<Association> IS_MEASUREMENT_PREDICATE = new Predicate<Association>() {

        @Override
        public boolean apply(Association input) {
            return MEASUREMENT_LINKNAME.equalsIgnoreCase(input.getLinkName());
        }
    };
    private String comment;
    private final int x0;
    private final int x1;
    private final int y0;
    private final int y1;

    /**
     * Constructs ...
     *
     * @param x0
     * @param y0
     * @param x1
     * @param y1
     */
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

    /**
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }

        Measurement that = (Measurement) o;

        if (x0 != that.x0) {
            return false;
        }
        if (x1 != that.x1) {
            return false;
        }
        if (y0 != that.y0) {
            return false;
        }
        if (y1 != that.y1) {
            return false;
        }

        return true;
    }

    /**
     * Build a Measurement from an ILink
     *
     * @param link
     * @return
     */
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

    /**
     *
     * @param observation
     * @return
     */
    public static Collection<Measurement> fromObservation(Observation observation) {
        Collection<Association> associations = Collections2.filter(observation.getAssociations(),
            IS_MEASUREMENT_PREDICATE);
        Collection<Measurement> measurements = Collections2.transform(associations, LINK_TO_MEASUREMENT_TRANSFORM);

        return new ArrayList<Measurement>(measurements);
    }

    /**
     * @return
     */
    public String getComment() {
        return comment;
    }

    /**
     * @return
     */
    public int getX0() {
        return x0;
    }

    /**
     * @return
     */
    public int getX1() {
        return x1;
    }

    /**
     * @return
     */
    public int getY0() {
        return y0;
    }

    /**
     * @return
     */
    public int getY1() {
        return y1;
    }

    /**
     * @return
     */
    @Override
    public int hashCode() {
        int result = x0;
        result = 31 * result + y0;
        result = 31 * result + x1;
        result = 31 * result + y1;

        return result;
    }

    /**
     *
     * @param comment
     */
    public void setComment(String comment) {
        this.comment = comment;
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
        if ((comment != null) && !comment.isEmpty()) {
            c = " " + comment;
        }

        return new LinkBean(MEASUREMENT_LINKNAME, ILink.VALUE_SELF, x0 + " " + y0 + " " + x1 + " " + y1 + c);
    }

    /**
     * @return
     */
    @Override
    public String toString() {
        return "Measurement{" + "x0=" + x0 + ", y0=" + y0 + ", x1=" + x1 + ", y1=" + y1 + '}';
    }
}
