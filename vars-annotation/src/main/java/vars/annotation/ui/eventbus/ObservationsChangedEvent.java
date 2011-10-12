package vars.annotation.ui.eventbus;

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
public class ObservationsChangedEvent extends UIChangeEvent<Collection<Observation>> {


    public ObservationsChangedEvent(Object updateSource, Collection<Observation> observations) {
        super(updateSource, Collections.unmodifiableCollection(new ArrayList<Observation>(observations)));
    }


}
