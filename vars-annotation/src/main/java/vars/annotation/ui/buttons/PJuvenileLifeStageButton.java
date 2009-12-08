/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui.buttons;

import javax.swing.ImageIcon;

import vars.annotation.ui.actions.AddPropertyAction;
import vars.annotation.ui.ToolBelt;

/**
 *
 * @author brian
 */
public class PJuvenileLifeStageButton extends PropButton {

    public PJuvenileLifeStageButton(ToolBelt toolBelt) {
        super();
        setAction(new AddJuvenileLifeStagePropAction(toolBelt));
        setToolTipText("juvenile life-stage");
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/juvenilebutton.png")));
        setEnabled(false);
    }
    
    private class AddJuvenileLifeStagePropAction extends AddPropertyAction {

        public AddJuvenileLifeStagePropAction(ToolBelt toolBelt) {
            super(toolBelt, "life-stage", "self", "juvenile");
        }

    }

}
