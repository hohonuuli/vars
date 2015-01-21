package vars.annotation.ui.imagepanel;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.bushe.swing.event.annotation.AnnotationProcessor;

import javax.swing.JComponent;
import java.util.Collection;

/**
 * Container for painters that are used by each MultiLayerUI. JXPainters can't be shared,
 * if they are only the one that was added first to a component will get drawn, the others
 * will do nothing. As a workaround, we store distinct painters that replicate the same
 * functionality, one painter for each LayerUI.
 *
 * The CommonPainters class responds to events tossed onto the EventBus. Use events to
 * update properties of each JXPainter.
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
