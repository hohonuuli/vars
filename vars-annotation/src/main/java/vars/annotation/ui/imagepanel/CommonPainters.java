package vars.annotation.ui.imagepanel;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.bushe.swing.event.annotation.AnnotationProcessor;

import javax.swing.JComponent;
import java.util.Collection;

/**
 * Container for painters that are used by each MultiLayerUI. Painters can be shared between each
 * LayerUI.
 *
 * @author Brian Schlining
 * @since 2015-01-21T13:13:00
 */
public class CommonPainters<A extends JComponent> {

    private final JXHorizontalLinePainter<A> horizontalLinePainter;
    private final JXCrossHairPainter<A> crossHairPainter;

    public CommonPainters(JXHorizontalLinePainter<A> horizontalLinePainter,
                          JXCrossHairPainter<A> crossHairPainter) {
        Preconditions.checkNotNull(horizontalLinePainter,
                "The horizontalLinePainter reference can not be null");
        Preconditions.checkNotNull(crossHairPainter,
                "The crossHairPainter reference can not be null");
        this.horizontalLinePainter = horizontalLinePainter;
        this.crossHairPainter = crossHairPainter;
        AnnotationProcessor.process(this);
    }

    public Collection<? extends JXPainter<A>> getPainters() {
        return ImmutableList.of(horizontalLinePainter, crossHairPainter);
    }

    public JXHorizontalLinePainter<A> getHorizontalLinePainter() {
        return horizontalLinePainter;
    }

    public JXCrossHairPainter<A> getCrossHairPainter() {
        return crossHairPainter;
    }

    public void respondTo(HorizontalLinesChangedEvent event) {
        horizontalLinePainter.setDistances(event.getDistances());
    }
}
