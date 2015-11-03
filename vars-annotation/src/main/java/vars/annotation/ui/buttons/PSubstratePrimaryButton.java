/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui.buttons;

import javax.swing.ImageIcon;

import vars.ILink;
import vars.LinkBean;


/**
 *
 * @author brian
 */
public class PSubstratePrimaryButton extends PConstrainedConceptButton {

    private static final ImageIcon icon =  new ImageIcon(PHabitatButton.class.getResource("/images/vars/annotation/s1button.png"));

    /**
     * Constructs ...
     */
    public PSubstratePrimaryButton() {
        super("Substrates",
                new LinkBean("s1", "Substrates", ILink.VALUE_NIL),
                "S1 - Primary Substrate",
                icon);
    }

}
