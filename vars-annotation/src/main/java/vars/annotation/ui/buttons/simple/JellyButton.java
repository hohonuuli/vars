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
public class JellyButton extends QuickConceptButton {

    @Inject
    public JellyButton(ToolBelt toolBelt) {
        super("Jelly", "/images/simple/jelly.jpg", toolBelt);
    }
    
}
