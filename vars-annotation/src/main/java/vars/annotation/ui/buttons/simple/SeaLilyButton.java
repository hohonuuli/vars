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
public class SeaLilyButton extends QuickConceptButton {

    @Inject
    public SeaLilyButton(ToolBelt toolBelt) {
        super("Sea Lily", "/images/simple/sea_lily.jpg", toolBelt);
    }
    
}
