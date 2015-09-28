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
public class RedJellyButton extends QuickConceptButton {

    @Inject
    public RedJellyButton(ToolBelt toolBelt) {
        super("Red Jelly", "/images/simple/Jelly_Red.jpg", toolBelt);
    }
    
}
