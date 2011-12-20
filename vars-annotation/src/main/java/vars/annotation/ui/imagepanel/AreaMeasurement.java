/*
 * @(#)AreaMeasurement.java   2011.12.09 at 02:23:02 PST
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



package vars.annotation.ui.imagepanel;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import org.mbari.geometry.Point2D;
import vars.ILink;
import vars.LinkBean;
import vars.annotation.Association;
import vars.annotation.Observation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Bean class for holding information about an AreaMeasurement
 *
 * @author Brian Schlining
 * @since 2011-12-09
 */
public class AreaMeasurement {

    /**
     *
     */
    public static final String AREA_MEASUREMENT_LINKNAME = "area measurement coordinates [x0 y0 ... xn yn, comment]";

    /** Filter for the distance measurement association */
    public static final Predicate<Association> IS_AREA_MEASUREMENT_PREDICATE = new Predicate<Association>() {

        @Override
        public boolean apply(Association input) {
            return AREA_MEASUREMENT_LINKNAME.equalsIgnoreCase(input.getLinkName());
        }
    };

    /** Transform to convert association to measurements */
    public static final Function<ILink, AreaMeasurement> LINK_TO_AREA_MEASUREMENT_TRANSFORM = new Function<ILink,
        AreaMeasurement>() {

        @Override
        public AreaMeasurement apply(ILink input) {
            return AreaMeasurement.fromLink(input);
        }
    };
    private String comment;
    private final List<Point2D<Integer>> coordinates;

    /**
     * Constructs ...
     *
     * @param coordinates
     */
    public AreaMeasurement(List<Point2D<Integer>> coordinates) {
        this(coordinates, null);
    }

    /**
     * Constructs ...
     *
     * @param coordinates
     * @param comment
     */
    public AreaMeasurement(List<Point2D<Integer>> coordinates, String comment) {
        Preconditions.checkArgument(coordinates.size() > 2,
                                    "Area measurement requires at least 3 points (found " + coordinates.size() + ")");

        /*
         If the list forms a closed polygon, drop the last point. We'll assume the first point is always
         the close point of the loop. So we don't need to duplicate it at the end of the coordinate list
         */
        if (coordinates.get(0).equals(coordinates.get(coordinates.size() - 1))) {
            coordinates = coordinates.subList(0, coordinates.size() - 1);
        }
        this.coordinates = ImmutableList.copyOf(coordinates);
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

        AreaMeasurement that = (AreaMeasurement) o;

        if (!coordinates.equals(that.coordinates)) {
            return false;
        }

        return true;
    }

    /**
     *
     * @param link
     * @return
     */
    public static AreaMeasurement fromLink(ILink link) {
        String[] coordsAndComment = link.getLinkValue().split(",");
        String comment = (coordsAndComment.length == 2) ? coordsAndComment[1] : null;
        Preconditions.checkArgument(coordsAndComment.length > 0, "No data was found in " + link);
        String[] parts = coordsAndComment[0].split(" ");
        Preconditions.checkArgument(parts.length > 6, "The Association does not contain 3 or more pixel coordinates");
        Preconditions.checkArgument(parts.length % 2 == 0, "The Association does not contain X-Y coordinate pairs");
        int idx = 0;
        List<Point2D<Integer>> points = new ArrayList<Point2D<Integer>>();
        while (idx < parts.length) {
            Integer x = Integer.valueOf(parts[idx]);
            idx++;
            Integer y = Integer.valueOf(parts[idx]);
            idx++;
            points.add(new Point2D<Integer>(x, y));
        }

        return new AreaMeasurement(points, comment);
    }

    /**
     *
     * @param observation
     * @return
     */
    public static Collection<AreaMeasurement> fromObservation(Observation observation) {
        Collection<Association> associations = Collections2.filter(observation.getAssociations(),
            IS_AREA_MEASUREMENT_PREDICATE);
        Collection<AreaMeasurement> measurements = Collections2.transform(associations,
            LINK_TO_AREA_MEASUREMENT_TRANSFORM);

        return new ArrayList<AreaMeasurement>(measurements);
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
    public List<Point2D<Integer>> getCoordinates() {
        return coordinates;
    }
    

    /**
     * @return
     */
    @Override
    public int hashCode() {
        return coordinates.hashCode();
    }

    /**
     *
     * @param comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return
     */
    public ILink toLink() {
        String c = Strings.isNullOrEmpty(comment) ? "" : ", " + comment;
        StringBuilder sb = new StringBuilder();

        for (Point2D<Integer> p : coordinates) {
            sb.append(p.getX()).append(" ").append(p.getY()).append(" ");
        }

        // Strip trailing blank and append comment
        String linkValue = sb.toString().substring(0, sb.length() - 1) + c;

        return new LinkBean(AREA_MEASUREMENT_LINKNAME, ILink.VALUE_SELF, linkValue);
    }

    /**
     * @return
     */
    @Override
    public String toString() {
        return "AreaMeasurement{coordinates=" + coordinates + '}';
    }
}
