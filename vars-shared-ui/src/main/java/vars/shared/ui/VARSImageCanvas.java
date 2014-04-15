/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.shared.ui;

import com.google.common.collect.ImmutableMap;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import org.mbari.swing.JImageUrlCanvas;
import vars.shared.awt.AWTUtilities;

/**
 * This is a simple subclass of JImageUrlCanas. The only difference is that this
 * canvas allows you to easily set the type of interpolation used to draw images.
 * 
 * @author brian
 */
public class VARSImageCanvas extends JImageUrlCanvas {
    
    public static final Map<String, Object> IMAGE_INTERPOLATION_MAP = AWTUtilities.IMAGE_INTERPOLATION_MAP;
    
    private Object interpolationHint;

    public VARSImageCanvas() {
    }

    public VARSImageCanvas(Image image) {
        super(image);
    }

    public VARSImageCanvas(String strImageURL) throws MalformedURLException {
        super(strImageURL);
    }

    public VARSImageCanvas(URL url) {
        super(url);
    }

    @Override
    public void paint(Graphics g) {
        // We want to anti-alias the images. Otherwise, HD images look crappy
        if (interpolationHint != null) {
            Graphics2D g2 = (Graphics2D) g;
            g2.addRenderingHints(ImmutableMap.of(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON,
            RenderingHints.KEY_INTERPOLATION, interpolationHint));
        }
                    
        super.paint(g);
    }
    
    public void setImageInterpolation(Object interpolationHint) {
        if (IMAGE_INTERPOLATION_MAP.containsValue(interpolationHint)) {
            this.interpolationHint = interpolationHint;
            repaint();
        }
    }
    
    
    
}
