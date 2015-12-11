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
public class LobateButton extends QuickConceptButton {

    @Inject
    public LobateButton(ToolBelt toolBelt) {
        super("Lobate", "/images/simple/Lobate.jpg", toolBelt);
    }
    
}
