package vars.annotation.ui.imagepanel;

import vars.annotation.Observation;

/**
 * @author Brian Schlining
 * @since 2011-12-19
 */
public class AddAreaMeasurementEvent {
    private final Observation observation;
    private final AreaMeasurement areaMeasurement;

    public AddAreaMeasurementEvent(Observation observation, AreaMeasurement areaMeasurement) {
        this.observation = observation;
        this.areaMeasurement = areaMeasurement;
    }

    public Observation getObservation() {
        return observation;
    }

    public AreaMeasurement getAreaMeasurement() {
        return areaMeasurement;
    }
}
