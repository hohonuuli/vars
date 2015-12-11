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
public class FlatFishButton extends QuickConceptButton {

    @Inject
    public FlatFishButton(ToolBelt toolBelt) {
        super("Flatfish", "/images/simple/flatfish.jpg", toolBelt);
    }
    
}