package vars.annotation.ui.imagepanel;

import com.google.common.base.Predicate;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.mbari.swing.JImageUrlCanvas;
import vars.annotation.Observation;
import vars.annotation.VideoFrame;
import vars.annotation.ui.PersistenceController;
import vars.annotation.ui.eventbus.ObservationsSelectedEvent;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Brian Schlining
 * @since 2012-08-03
 */
public class JXSelectedObservationsPainter<T extends JImageUrlCanvas>
        extends JXObservationsPainter<T> {

    private Collection<Observation> emptyCollection = new ArrayList<Observation>();

    public JXSelectedObservationsPainter() {
        super(MarkerStyle.SELECTED, true, false);
    }

    @EventSubscriber(eventClass = ObservationsSelectedEvent.class)
    public void respondTo(ObservationsSelectedEvent event) {
        Collection<VideoFrame> selectedVideoFrames = PersistenceController.toVideoFrames(event.get());
        if (selectedVideoFrames.size() == 1 ) {
            setObservations(event.get());
        }
        else {
            setObservations(emptyCollection);
        }
        setDirty(true);
    }
}
