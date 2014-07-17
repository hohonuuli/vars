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
public class CoralButton extends QuickConceptButton {

    @Inject
    public CoralButton(ToolBelt toolBelt) {
        super("Soft Coral", "/images/simple/coral.jpg", toolBelt);
    }
    
}
