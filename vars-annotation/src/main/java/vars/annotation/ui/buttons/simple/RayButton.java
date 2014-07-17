/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui.buttons.simple;

import vars.annotation.ui.ToolBelt;

import javax.inject.Inject;

/**
 *
 * @author brian
 */
public class RayButton extends QuickConceptButton {

    @Inject
    public RayButton(ToolBelt toolBelt) {
        super("Ray", "/images/simple/Ray.jpg", toolBelt);
    }
    
}
