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
public class TrapButton extends QuickConceptButton {

    @Inject
    public TrapButton(ToolBelt toolBelt) {
        super("trap", "/images/simple/trap.jpg", toolBelt);
    }
    
}
