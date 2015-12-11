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
public class SeaSlugButton extends QuickConceptButton {

    @Inject
    public SeaSlugButton(ToolBelt toolBelt) {
        super("Sea Slug", "/images/simple/sea_slug.jpg", toolBelt);
    }
    
}
