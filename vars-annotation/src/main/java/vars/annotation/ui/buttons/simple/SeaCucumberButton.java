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
public class SeaCucumberButton extends QuickConceptButton {

    @Inject
    public SeaCucumberButton(ToolBelt toolBelt) {
        super("Sea Cucumber", "/images/simple/sea_cucumber.jpg", toolBelt);
    }
    
}
