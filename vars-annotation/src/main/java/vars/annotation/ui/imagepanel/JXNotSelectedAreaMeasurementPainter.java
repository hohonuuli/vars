package vars.annotation.ui.imagepanel;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import com.sun.istack.internal.Nullable;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jdesktop.jxlayer.JXLayer;
import org.mbari.swing.JImageUrlCanvas;
import vars.annotation.Association;
import vars.annotation.Observation;

import javax.swing.JComponent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Brian Schlining
 * @since 2013-01-17
 */
public class JXNotSelectedAreaMeasurementPainter<A extends JImageUrlCanvas> extends JXAreaMeasurementPainter<A> {


    public JXNotSelectedAreaMeasurementPainter() {
        super(new Font("Sans Serif", Font.PLAIN, 8), Color.LIGHT_GRAY, new BasicStroke(1));
        AnnotationProcessor.process(this);
    }


    @EventSubscriber(eventClass = IAFRepaintEvent.class)
    public void respondTo(IAFRepaintEvent event) {
        UIDataCoordinator dataCoordinator = event.get();
        Set<Observation> observations = new HashSet<Observation>(dataCoordinator.getObservations());
        observations.removeAll(dataCoordinator.getSelectedObservations());
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
