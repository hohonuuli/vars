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
public class SeastarButton extends QuickConceptButton {

    @Inject
    public SeastarButton(ToolBelt toolBelt) {
        super("sea star", "/images/simple/sea_star.png", toolBelt);
    }
    
}
