/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui.imagepanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.util.Collection;
import java.util.Vector;
import javax.swing.SwingUtilities;
import org.jdesktop.jxlayer.JXLayer;
import org.mbari.util.ImageCanvas;

/**
 * Class for testing out resizing images
 * @author brian
 */
public class PointAdditionLayerUI <T extends ImageCanvas> extends CrossHairLayerUI<T> {

    final Collection<Point> sourcePoints = new Vector<Point>();
    private String coordinateString = null;
    private Point coordinatePoint = null;
    private final Font font = new Font("Sans Serif", Font.PLAIN, 12);

    @Override
    protected void paintLayer(Graphics2D g2, JXLayer<? extends T> jxl) {
        super.paintLayer(g2, jxl);

        if (coordinateString != null) {
            g2.setXORMode(Color.WHITE);
            g2.setFont(font);
            g2.drawString(coordinateString, coordinatePoint.x, coordinatePoint.y);
        }

        // Draw points
        g2.setPaintMode();           // Make sure XOR is turned off
        g2.setPaint(new Color(255, 0, 0, 180));
        g2.setStroke(new BasicStroke(3));
        ImageCanvas imageCanvas = jxl.getView();
        for (Point point : sourcePoints) {
            point = imageCanvas.convertToComponent(point);
            int x = point.x;
            int y = point.y;
            // Draw the annotation
            int armLength = 7;
            GeneralPath gp = new GeneralPath();
            gp.moveTo(x - armLength, y - armLength);
            gp.lineTo(x + armLength, y + armLength);
            gp.moveTo(x + armLength, y - armLength);
            gp.lineTo(x - armLength, y + armLength);
            g2.draw(gp);
        }

        g2.setPaintMode();

    }

    @Override
    protected void processMouseMotionEvent(MouseEvent me, JXLayer<? extends T> jxl) {
        super.processMouseMotionEvent(me, jxl);

        if (me.getID() == MouseEvent.MOUSE_MOVED || me.getID() == MouseEvent.MOUSE_DRAGGED) {
            
            Point point = SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), jxl);
            ImageCanvas imageCanvas = jxl.getView();

            if (imageCanvas.getImageRectangle().contains(point)) {
                Point imagePoint = imageCanvas.convertToImage(point);
                Point componentPoint = imageCanvas.convertToComponent(imagePoint);
                coordinateString = "(SRC[" + point.x + ", " + point.y +
                        "] Image[" + imagePoint.x + ", " + imagePoint.y +
                        "] DST[" + componentPoint.x + ", " + componentPoint.y + "])";
                coordinatePoint = point;
            }
            else {
                coordinateString = null;
                coordinatePoint = null;
            }

            // mark the ui as dirty and needed to be repainted
            setDirty(true);
        }

        
    }

    @Override
    protected void processMouseEvent(MouseEvent me, JXLayer<? extends T> jxl) {
        super.processMouseEvent(me, jxl);
        if (me.getID() == MouseEvent.MOUSE_RELEASED) {
                Point point = SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), jxl);
            ImageCanvas imageCanvas = jxl.getView();
                Point imagePoint = imageCanvas.convertToImage(point);
            sourcePoints.add(imagePoint);
            setDirty(true);
        }
    }




    

}
