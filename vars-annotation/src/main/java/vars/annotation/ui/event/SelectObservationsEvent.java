package vars.annotation.ui.event;

import vars.annotation.Observation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Brian Schlining
 * @since 2011-08-31
 */
public class SelectObservationsEvent {

    private final Object selectionSource;
    private final Collection<Observation> observations;


    /**
     * The constructor creates an internal copy of the obserations collection.
     * @param observations
     */
    public SelectObservationsEvent(Object selectionSource, Collection<Observation> observations) {
        this.selectionSource = selectionSource;
        this.observations = Collections.unmodifiableCollection(new ArrayList<Observation>(observations));
    }

    /**
     *
     * @return An unmodifiable collection of observations to be sleec
     */
    public Collection<Observation> getObservations() {
        return observations;
    }

    /**
     *
     * @return THe object that triggered the selection event. Useful so that the object doesn't
     * try to update it's selection, triggering an infinite loop.
     */
    public Object getSelectionSource() {
        return selectionSource;
    }
}
