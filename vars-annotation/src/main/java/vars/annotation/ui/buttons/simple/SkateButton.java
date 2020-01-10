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
public class SkateButton extends QuickConceptButton {

    @Inject
    public SkateButton(ToolBelt toolBelt) {
        super("skate", "/images/simple/skate.jpg", toolBelt);
    }
    
}
