/*
 * @(#)AddAreaMeasurementEvent.java   2012.11.26 at 08:48:39 PST
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

import vars.annotation.Observation;

/**
 * Immutable eventing class. It contains two properties: The observation to add a measurement to,
 * and the AreaMeasurement containing the measurment values.
 *
 * @author Brian Schlining
 * @since 2011-12-19
 */
public class AddAreaMeasurementEvent {

    private final AreaMeasurement areaMeasurement;
    private final Observation observation;

    /**
     * Constructs ...
     *
     * @param observation
     * @param areaMeasurement
     */
    public AddAreaMeasurementEvent(Observation observation, AreaMeasurement areaMeasurement) {
        this.observation = observation;
        this.areaMeasurement = areaMeasurement;
    }

    /**
     * @return Bean class containing information about the measurement to be added
     */
    public AreaMeasurement getAreaMeasurement() {
        return areaMeasurement;
    }

    /**
     * @return The observation to add the measurement information to.
     */
    public Observation getObservation() {
        return observation;
    }
}
