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
public class AnemoneButton extends QuickConceptButton {

    @Inject
    public AnemoneButton(ToolBelt toolBelt) {
        super("anemone", "/images/simple/anemone.jpg", toolBelt);
    }
    
}
