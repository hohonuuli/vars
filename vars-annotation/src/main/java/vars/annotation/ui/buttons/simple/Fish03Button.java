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
public class Fish03Button extends QuickConceptButton {

    @Inject
    public Fish03Button(ToolBelt toolBelt) {
        super("Fish 03", "/images/simple/Fish_03.jpg", toolBelt);
    }
    
}