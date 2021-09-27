/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui.buttons;

import javax.swing.ImageIcon;
import mbarix4j.awt.event.ActionAdapter;
import vars.ILink;
import vars.LinkBean;
import vars.annotation.ui.dialogs.ToConceptSelectionDialog;

import vars.annotation.ui.actions.AddPropertyAction;
import vars.annotation.ui.ToolBelt;

/**
 *
 * @author brian
 */
public class PSubstrateSecondaryButton extends PConstrainedConceptButton {

    private static final ImageIcon icon =  new ImageIcon(PHabitatButton.class.getResource("/images/vars/annotation/s2button.png"));

    /**
     * Constructs ...
     */
    public PSubstrateSecondaryButton() {
        super("Substrates",
                new LinkBean("s2", "Substrates", ILink.VALUE_NIL),
                "S2 - Secondary Substrate",
                icon);
    }

}


