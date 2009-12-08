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
public class PChainArrangementButton extends PropButton {

    public PChainArrangementButton(ToolBelt toolBelt) {
        super();
        setAction(new AddChainArrangementPropAction(toolBelt));
        setToolTipText("chain arrangement");
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/chainbutton.png")));
        setEnabled(false);
    }
    
    private class AddChainArrangementPropAction extends AddPropertyAction {

        public AddChainArrangementPropAction(ToolBelt toolBelt) {
            super(toolBelt, "shape-arrangement", "self", "chain");
        }

    }

}
