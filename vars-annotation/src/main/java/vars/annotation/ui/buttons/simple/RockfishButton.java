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
public class RockfishButton extends QuickConceptButton {

    @Inject
    public RockfishButton(ToolBelt toolBelt) {
        super("rockfish", "/images/simple/rockfish.jpg", toolBelt);
    }
    
}
