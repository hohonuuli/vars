package vars.annotation.ui.imagepanel;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import com.sun.istack.internal.Nullable;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jdesktop.jxlayer.JXLayer;
import vars.annotation.Observation;

import javax.swing.JComponent;
import java.awt.Graphics2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Brian Schlining
 * @since 2013-01-17
 */
public class JXConcurrentAreaMeasurementPainter<A extends JComponent> extends AbstractJXPainter<A> {

    private final Collection<Observation> observations = new CopyOnWriteArraySet<Observation>();
    private Predicate<Observation> areaMeasurementPredicate = new Predicate<Observation>() {
        @Override
        public boolean apply(@Nullable Observation observation) {
            return false;
        }
    };

    public JXConcurrentAreaMeasurementPainter() {
        AnnotationProcessor.process(this);
    }

    @Override
    public void paintLayer(Graphics2D g2, JXLayer<? extends A> jxl) {
        super.paintLayer(g2, jxl);
        g2.setPaintMode();




    }

    @EventSubscriber(eventClass = IAFRepaintEvent.class)
    public void respondTo(IAFRepaintEvent event) {
        UIDataCoordinator dataCoordinator = event.get();
        Set<Observation> observations = new HashSet<Observation>(dataCoordinator.getObservations());
         // Search for all observations with area measurements

        Set<Observation> areaMeasurementObservations = Sets.filter(observations, areaMeasurementPredicate);

        //
    }


}
