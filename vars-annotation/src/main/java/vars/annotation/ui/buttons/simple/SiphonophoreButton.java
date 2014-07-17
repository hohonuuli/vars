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
public class SiphonophoreButton extends QuickConceptButton {

    @Inject
    public SiphonophoreButton(ToolBelt toolBelt) {
        super("Siphonophore", "/images/simple/siphonophore.jpg", toolBelt);
    }
    
}
