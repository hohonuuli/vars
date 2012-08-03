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
import java.util.HashSet;

/**
 * @author Brian Schlining
 * @since 2012-08-03
 */
public class JXNotSelectedObservationsPainter<T extends JImageUrlCanvas>
        extends JXObservationsPainter<T> {

    private Collection<Observation> emptyCollection = new ArrayList<Observation>();

    public JXNotSelectedObservationsPainter() {
        super(MarkerStyle.NOTSELECTED, true, false);
    }

    @EventSubscriber(eventClass = ObservationsSelectedEvent.class)
    public void respondTo(ObservationsSelectedEvent event) {
        Collection<VideoFrame> selectedVideoFrames = PersistenceController.toVideoFrames(event.get());
        if (selectedVideoFrames.size() == 1 ) {
            VideoFrame videoFrame = selectedVideoFrames.iterator().next();
            Collection<Observation> drawableObservations = new HashSet<Observation>(videoFrame.getObservations());
            drawableObservations.remove(event.get());
            setObservations(drawableObservations);
        }
        else {
            setObservations(emptyCollection);
        }
        setDirty(true);
    }
}
