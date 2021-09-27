/*
 * @(#)CameraPoint.java   2011.12.10 at 08:54:48 PST
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

/**
 * Represents a point in space with a particular orientation.
 *
 * @author brian
 *
 * @param <T>
 */
public class CameraPoint<T extends Number> extends Point3D<T> {

    private final T heading;
    private final T pitch;
    private final T roll;

    /**
     * Constructs ...
     *
     * @param x
     * @param y
     * @param z
     * @param pitch
     * @param roll
     * @param heading
     */
    public CameraPoint(T x, T y, T z, T pitch, T roll, T heading) {
        super(x, y, z);
        this.pitch = pitch;
        this.roll = roll;
        this.heading = heading;
    }

    /**
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CameraPoint<T> other = (CameraPoint<T>) obj;
        if (!super.equals(obj)) {
            return false;
        }
        if ((this.pitch != other.pitch) && ((this.pitch == null) || !this.pitch.equals(other.pitch))) {
            return false;
        }
        if ((this.roll != other.roll) && ((this.roll == null) || !this.roll.equals(other.roll))) {
            return false;
        }
        if ((this.heading != other.heading) && ((this.heading == null) || !this.heading.equals(other.heading))) {
            return false;
        }

        return true;
    }

    /**
     * @return
     */
    public T getHeading() {
        return heading;
    }

    /**
     * @return
     */
    public T getPitch() {
        return pitch;
    }

    /**
     * @return
     */
    public T getRoll() {
        return roll;
    }

    /**
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + ((this.pitch != null) ? this.pitch.hashCode() : 0);
        hash = 43 * hash + ((this.roll != null) ? this.roll.hashCode() : 0);
        hash = 43 * hash + ((this.heading != null) ? this.heading.hashCode() : 0);

        return hash + super.hashCode();
    }
}
