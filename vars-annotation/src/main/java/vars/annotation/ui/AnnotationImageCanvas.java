/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui;

import java.awt.Image;
import java.net.MalformedURLException;
import java.net.URL;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import vars.annotation.ui.eventbus.ImageInterpolationChangedEvent;
import vars.shared.ui.VARSImageCanvas;

/**
 *
 * @author brian
 */
public class AnnotationImageCanvas extends VARSImageCanvas {

    public AnnotationImageCanvas() {
        AnnotationProcessor.process(this);
    }

    public AnnotationImageCanvas(Image image) {
        super(image);
        AnnotationProcessor.process(this);
    }

    public AnnotationImageCanvas(String strImageURL) throws MalformedURLException {
        super(strImageURL);
        AnnotationProcessor.process(this);
    }

    public AnnotationImageCanvas(URL url) {
        super(url);
        AnnotationProcessor.process(this);
    }
    
    @EventSubscriber(eventClass = ImageInterpolationChangedEvent.class)
    public void respondTo(ImageInterpolationChangedEvent event) {
        setImageInterpolation(event.get());
    }
    
}
