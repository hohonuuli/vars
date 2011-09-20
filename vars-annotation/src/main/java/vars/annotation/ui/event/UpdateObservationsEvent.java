package vars.annotation.ui.event;

import vars.annotation.Observation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * When observations are updated in some component this event is fired. Listeners to this event should
 * updated references to observations to the one is this event (use primary kye).
 * The updated source is also included so that the owner of the event does not have to redo any updates
 * @author Brian Schlining
 * @since 2011-09-20
 */
public class UpdateObservationsEvent {

    private final Object updateSource;
    private final Collection<Observation> observations;

    public UpdateObservationsEvent(Object updateSource, Collection<Observation> observations) {
        this.updateSource = updateSource;
        this.observations = Collections.unmodifiableCollection(new ArrayList<Observation>(observations));
    }

    /**
     * @return The object that updated the observations
     */
    public Object getUpdateSource() {
        return updateSource;
    }

    /**
     * @return The updated observations
     */
    public Collection<Observation> getObservations() {
        return observations;
    }

}
