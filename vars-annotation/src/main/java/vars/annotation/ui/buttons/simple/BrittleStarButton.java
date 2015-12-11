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
public class BrittleStarButton extends QuickConceptButton {

    @Inject
    public BrittleStarButton(ToolBelt toolBelt) {
        super("Brittlestar", "/images/simple/Brittlestar.jpg", toolBelt);
    }
    
    

}
