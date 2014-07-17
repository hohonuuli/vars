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
public class ShrimpButton extends QuickConceptButton {

    @Inject
    public ShrimpButton(ToolBelt toolBelt) {
        super("Shrimp", "/images/simple/shrimp.jpg", toolBelt);
    }
    
}
