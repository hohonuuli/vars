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
public class Fish02Button extends QuickConceptButton {

    @Inject
    public Fish02Button(ToolBelt toolBelt) {
        super("Fish 02", "/images/simple/Fish_02.jpg", toolBelt);
    }
    
}
