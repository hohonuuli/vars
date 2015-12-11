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
public class OtherButton extends QuickConceptButton {

    @Inject
    public OtherButton(ToolBelt toolBelt) {
        super("Other", "/images/simple/other.jpg", toolBelt);
    }
    
}
