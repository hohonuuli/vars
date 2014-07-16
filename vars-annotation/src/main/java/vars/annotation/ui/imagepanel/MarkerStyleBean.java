package vars.annotation.ui.imagepanel;

import java.awt.*;

/**
 * Created by brian on 7/15/14.
 */
public class MarkerStyleBean implements IMarkerStyle {

    private final Color color;
    private final int armLength;
    private final Font font;
    private final Stroke stroke;

    public MarkerStyleBean(Color color, int armLength, Font font, Stroke stroke) {
        this.color = color;
        this.armLength = armLength;
        this.font = font;
        this.stroke = stroke;
    }

    public Color getColor() {
        return color;
    }

    public int getArmLength() {
        return armLength;
    }

    public Font getFont() {
        return font;
    }

    public Stroke getStroke() {
        return stroke;
    }
}
