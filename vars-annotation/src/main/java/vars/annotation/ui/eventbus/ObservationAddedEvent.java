package vars.annotation.ui.eventbus;

import vars.annotation.Observation;

/**
 * Event that occurs when a new Observation has been created and needs to be added to a UI
 * @author Brian Schlining
 * @since 2011-10-10
 */
public class ObservationAddedEvent extends UIEvent<Observation> {

    public ObservationAddedEvent(Object source, Observation refs) {
        super(source, refs);
    }
}
