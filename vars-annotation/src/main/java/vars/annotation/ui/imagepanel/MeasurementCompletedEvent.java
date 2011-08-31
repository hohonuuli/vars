package vars.annotation.ui.imagepanel;

import vars.annotation.Observation;

/**
 * @author Brian Schlining
 * @since 2011-08-30
 */
public class MeasurementCompletedEvent {

    private final Measurement measurement;
    private final Observation observation;

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
