package vars.annotation.ui.imagepanel;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * @author Brian Schlining
 * @since 2015-01-21T15:21:00
 */
public class HorizontalLinesChangedEvent {

    private final List<Double> distances;

    public HorizontalLinesChangedEvent(List<Double> distances) {
        this.distances = distances;
    }

    public List<Double> getDistances() {
        return ImmutableList.copyOf(distances);
    }
}
