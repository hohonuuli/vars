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
public class KelpButton extends QuickConceptButton {

    @Inject
    public KelpButton(ToolBelt toolBelt) {
        super("Kelp", "/images/simple/Kelp.jpg", toolBelt);
    }
    
}