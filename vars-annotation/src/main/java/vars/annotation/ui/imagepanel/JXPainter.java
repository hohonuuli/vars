package vars.annotation.ui.imagepanel;

import org.jdesktop.jxlayer.JXLayer;
import vars.annotation.Observation;

import javax.swing.JComponent;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeSupport;

/**
 * Adjunct interface that allows us to add additional drawing/painting to a JXLayer object based. This class is
 * designed to add custom views to a LayerUI based on some view of Model data.
 *
 * @author Brian Schlining
 * @since 2011-12-21
 * @param <A> The type passed to the JXLayerUI that is being painted
 */
public interface JXPainter<A extends JComponent> {

    void paintLayer(Graphics2D g2, JXLayer<? extends A> jxl);

    void processMouseMotionEvent(MouseEvent me, JXLayer<? extends A> jxl);

    void processMouseEvent(MouseEvent me, JXLayer<? extends A> jxl);

    boolean isDirty();

    void setDirty(boolean dirty);

    PropertyChangeSupport getPropertyChangeSupport();

}
