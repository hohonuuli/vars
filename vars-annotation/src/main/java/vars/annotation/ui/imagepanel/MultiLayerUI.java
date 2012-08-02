package vars.annotation.ui.imagepanel;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.AbstractLayerUI;

import javax.swing.JComponent;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Brian Schlining
 * @since 2012-08-02
 */
public class MultiLayerUI<T extends JComponent> extends AbstractLayerUI<T> {

    private Queue<JXPainter<T>> painters = new LinkedBlockingQueue<JXPainter<T>>();

    public void addPainter(JXPainter<T> painter) {
        painters.add(painter);
        setDirty(true);
    }

    public void removePainter(JXPainter<T> painter) {
        painters.remove(painter);
        setDirty(true);
    }

    public void clearPainters() {
        painters.clear();
        setDirty(true);
    }

    @Override
    protected void paintLayer(Graphics2D g2, JXLayer<? extends T> jxl) {
        super.paintLayer(g2, jxl);
        for (JXPainter<T> painter : painters) {
            painter.paintLayer(g2, jxl);
        }
    }

    @Override
    protected void processMouseMotionEvent(MouseEvent me, JXLayer<? extends T> jxl) {
        //System.out.println(getClass().getSimpleName() + ".processMouseMotionEvent -> MouseEvent: " + me.getID());
        super.processMouseMotionEvent(me, jxl);
        boolean dirty = false;
        for (JXPainter<T> painter : painters) {
            painter.processMouseMotionEvent(me, jxl);
            if (painter.isDirty()) {
                dirty = true;
            }
        }
        if (dirty) {
            setDirty(dirty);
        }
    }

    @Override
    protected void processMouseEvent(MouseEvent me, JXLayer<? extends T> jxl) {
        //System.out.println(getClass().getSimpleName() + ".processMouseEvent -> MouseEvent: " + me.getID());
        super.processMouseEvent(me, jxl);
        boolean dirty = false;
        for (JXPainter<T> painter : painters) {
            painter.processMouseEvent(me, jxl);
            if (painter.isDirty()) {
                dirty = true;
            }
        }
        if (dirty) {
            setDirty(dirty);
        }
    }
}
