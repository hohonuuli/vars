package vars.annotation.ui.eventbus;

import vars.annotation.Observation;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Event that occurs when a new Observation has been created and needs to be added to a UI
 * @author Brian Schlining
 * @since 2011-10-10
 */
public class ObservationsAddedEvent extends UIEvent<Collection<Observation>> {

    public ObservationsAddedEvent(Object source, final Observation refs) {
        super(source, new ArrayList<Observation>() {{ add(refs); }});
    }

    public ObservationsAddedEvent(Object source, Collection<Observation> refs) {
        super(source, new ArrayList<Observation>(refs));
    }
}
