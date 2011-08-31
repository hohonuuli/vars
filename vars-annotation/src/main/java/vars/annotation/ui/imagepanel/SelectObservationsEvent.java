package vars.annotation.ui.imagepanel;

import vars.annotation.Observation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Brian Schlining
 * @since 2011-08-31
 */
public class SelectObservationsEvent {

    final Collection<Observation> observations;


    /**
     * The constructor creates an internal copy of the obserations collection.
     * @param observations
     */
    public SelectObservationsEvent(Collection<Observation> observations) {
        this.observations = Collections.unmodifiableCollection(new ArrayList<Observation>(observations));
    }

    /**
     *
     * @return An unmodifiable collection of observations to be sleec
     */
    public Collection<Observation> getObservations() {
        return observations;
    }
}
