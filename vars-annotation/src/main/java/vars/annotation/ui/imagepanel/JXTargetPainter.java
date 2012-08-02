package vars.annotation.ui.imagepanel;

import org.jdesktop.jxlayer.JXLayer;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;

/**
 * @author Brian Schlining
 * @since 2012-08-02
 */
public class JXTargetPainter<A extends JComponent> extends AbstractJXPainter<A> {

    private double diameter = 33;
    private double radius = diameter / 2;
    private Shape circle = new Ellipse2D.Double(0D, 0D, diameter, diameter);

    @Override
       public void paintLayer(Graphics2D gd, JXLayer<? extends A> jxl) {
           gd.setXORMode(Color.WHITE);
           gd.draw(circle);
           gd.setPaintMode();
       }

       @Override
       public void processMouseMotionEvent(MouseEvent me, JXLayer jxl) {

           if (me.getID() == MouseEvent.MOUSE_MOVED || me.getID() == MouseEvent.MOUSE_DRAGGED) {
               Point point = SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), jxl);
               int w = jxl.getWidth();
               int h = jxl.getHeight();

               if (point.y <= h && point.x < w) {
                   circle = new Ellipse2D.Double(point.x - radius, point.y - radius, diameter, diameter);
               }

               // mark the ui as dirty and needed to be repainted
               setDirty(true);
           }
       }

}
