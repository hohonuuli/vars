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
public class SinkerButton extends QuickConceptButton {

    @Inject
    public SinkerButton(ToolBelt toolBelt) {
        super("Sinker", "/images/simple/sinker.jpg", toolBelt);
    }
    
}
