package vars.annotation.ui.imagepanel;

import org.jdesktop.jxlayer.JXLayer;
import vars.annotation.Observation;

import java.awt.Graphics2D;

/**
 * Adjunct interface that allows us to add additional drawing/painting to a JXLayer object based. This class is
 * designed to add custom views to a LayerUI based on some view of an Observation
 *
 * @author Brian Schlining
 * @since 2011-12-21
 */
public interface ObservationPainter<T> {

    Observation getObservation();

    void paintLayer(Graphics2D g2, JXLayer<? extends T> jxl);

}
