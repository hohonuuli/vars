/*
 * @(#)MeasurementCompletedEvent.java   2012.11.26 at 08:48:29 PST
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
 * @author Brian Schlining
 * @since 2011-08-30
 */
public class MeasurementCompletedEvent {

    private final Measurement measurement;
    private final Observation observation;

    /**
     * Constructs ...
     *
     * @param measurement
     * @param observation
     */
    public MeasurementCompletedEvent(Measurement measurement, Observation observation) {
        this.measurement = measurement;
        this.observation = observation;
    }

    /**
     *
     * @return The measurement
     */
    public Measurement getMeasurement() {
        return measurement;
    }

    /**
     *
     * @return The observation associated with the Measurement
     */
    public Observation getObservation() {
        return observation;
    }
}
