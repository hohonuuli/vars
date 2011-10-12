package vars.annotation.ui.eventbus;

import vars.annotation.Observation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Brian Schlining
 * @since 2011-10-10
 */
public class ObservationsRemovedEvent extends UIEvent<Collection<Observation>> {

    public ObservationsRemovedEvent(Object source, Collection<Observation> refs) {
        super(source, Collections.unmodifiableCollection(new ArrayList<Observation>(refs)));
    }

}
