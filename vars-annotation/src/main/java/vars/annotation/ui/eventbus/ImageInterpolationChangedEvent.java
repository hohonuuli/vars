/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui.eventbus;

/**
 *
 * @author brian
 */
public class ImageInterpolationChangedEvent  extends UIChangeEvent<Object>{

    /**
     * @param changeSource Can be anything
     * @param refs Should be one of the VALUE_INTERPOLATION_* hints in RenderingHings
     */
    public ImageInterpolationChangedEvent(Object changeSource, Object refs) {
        super(changeSource, refs);
    }
    
}

