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
public class KrillButton extends QuickConceptButton {

    @Inject
    public KrillButton(ToolBelt toolBelt) {
        super("Krill", "/images/simple/Krill.jpg", toolBelt);
    }
    
}
