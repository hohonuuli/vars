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
public class Fish01Button extends QuickConceptButton {

    @Inject
    public Fish01Button(ToolBelt toolBelt) {
        super("ray finned fish", "/images/simple/fish.png", toolBelt);
    }
    
}
