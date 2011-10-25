package vars.annotation.ui.eventbus;

import com.google.common.collect.ImmutableList;
import vars.annotation.Observation;

import java.util.Collection;

/**
 * Notfies UI components that new observations are to be selected in that component. In
 * general, observation selection should use the primary keys to lookup the correct
 * Observations to select.
 * @author Brian Schlining
 * @since 2011-09-20
 */
public class ObservationsSelectedEvent extends UISelectionEvent<Collection<Observation>> {

    public ObservationsSelectedEvent(Object selectionSource, Collection<Observation> observations) {
        super(selectionSource, ImmutableList.copyOf(observations));
    }
}
