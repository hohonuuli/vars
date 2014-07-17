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
public class SeapenButton extends QuickConceptButton {

    @Inject
    public SeapenButton(ToolBelt toolBelt) {
        super("Seapen", "/images/simple/Seapen.jpg", toolBelt);
    }
    
}
