package vars.annotation.ui.imagepanel;

import com.google.common.collect.Collections2;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.mbari.swing.JImageUrlCanvas;
import vars.annotation.Association;
import vars.annotation.Observation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Brian Schlining
 * @since 2013-02-04
 */
public class JXSelectedAreaMeasurementPainter<A extends JImageUrlCanvas> extends JXAreaMeasurementPainter<A> {


    public JXSelectedAreaMeasurementPainter() {
        super(new Font("Sans Serif", Font.PLAIN, 10), Color.GREEN, new BasicStroke(2));
        AnnotationProcessor.process(this);
    }


    @EventSubscriber(eventClass = IAFRepaintEvent.class)
    public void respondTo(IAFRepaintEvent event) {
        UIDataCoordinator dataCoordinator = event.get();
        Set<Observation> observations = new HashSet<Observation>(dataCoordinator.getSelectedObservations());
        // Search for all observations with area measurements
        List<Association> associations = new ArrayList<Association>();
        for (Observation obs : observations) {
            associations.addAll(obs.getAssociations());
        }

        Collection<Association> amAssociations =
                Collections2.filter(associations, AreaMeasurement.IS_AREA_MEASUREMENT_PREDICATE);
        setAssociations(amAssociations);
    }


}
