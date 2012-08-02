package vars.annotation.ui.imagepanel;

import org.jdesktop.jxlayer.JXLayer;

import javax.swing.JComponent;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

/**
 * @author Brian Schlining
 * @since 2012-08-02
 */
public abstract class AbstractJXPainter<A extends JComponent> implements JXPainter<A> {

    private boolean dirty;

    @Override
    public void paintLayer(Graphics2D g2, JXLayer<? extends A> jxl) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void processMouseMotionEvent(MouseEvent me, JXLayer<? extends A> jxl) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void processMouseEvent(MouseEvent me, JXLayer<? extends A> jxl) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
