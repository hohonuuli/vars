package vars.annotation.ui.imagepanel;

import vars.annotation.Observation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;

/**
 * @author Brian Schlining
 * @since 2011-08-30
 */
public enum MarkerStyle {
    SELECTED(new Color(0, 255, 0, 180), new Font("Sans Serif", Font.PLAIN, 14), 14, new BasicStroke(3)),
    NOTSELECTED(new Color(255, 0, 0, 180), new Font("Sans Serif", Font.PLAIN, 10), 7, new BasicStroke(3)),
    FAINT(new Color(126, 126, 126, 180), new Font("Sans Serif", Font.PLAIN, 10), 6, new BasicStroke(2));

    final int armLength;
    final Color color;
    final Font font;
    final Stroke stroke;

    private MarkerStyle(Color color, Font font, int armLength, Stroke stroke) {
        this.color = color;
        this.font = font;
        this.armLength = armLength;
        this.stroke = stroke;
    }
    
}
