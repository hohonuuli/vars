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
public class CrinoidButton extends QuickConceptButton {

    @Inject
    public CrinoidButton(ToolBelt toolBelt) {
        super("Crinoid", "/images/simple/Crinoid.jpg", toolBelt);
    }
    
}
