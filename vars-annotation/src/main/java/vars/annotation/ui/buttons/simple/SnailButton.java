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
public class SnailButton extends QuickConceptButton {

    @Inject
    public SnailButton(ToolBelt toolBelt) {
        super("Snail", "/images/simple/snail.jpg", toolBelt);
    }
    
}