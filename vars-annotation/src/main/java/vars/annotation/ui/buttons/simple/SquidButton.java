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
public class SquidButton extends QuickConceptButton {

    @Inject
    public SquidButton(ToolBelt toolBelt) {
        super("squid", "/images/simple/squid.jpg", toolBelt);
    }
    
}
