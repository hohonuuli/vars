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
public class CrabButton extends QuickConceptButton {

    @Inject
    public CrabButton(ToolBelt toolBelt) {
        super("crab", "/images/simple/crab.png", toolBelt);
    }

    
}
