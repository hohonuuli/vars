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
public class CydippidButton extends QuickConceptButton {

    @Inject
    public CydippidButton(ToolBelt toolBelt) {
        super("Cydippid", "/images/simple/Cydippid.jpg", toolBelt);
    }
    
}
