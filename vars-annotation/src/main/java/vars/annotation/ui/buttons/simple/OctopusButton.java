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
public class OctopusButton extends QuickConceptButton {

    @Inject
    public OctopusButton(ToolBelt toolBelt) {
        super("Octopus", "/images/simple/octopus.jpg", toolBelt);
    }
    
}
