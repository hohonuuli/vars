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
public class PLargeSizeButton extends PropButton {

    public PLargeSizeButton() {
        super();
        setAction(new AddLargeSizePropAction(getToolBelt()));
        setToolTipText("relatively large size");
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/largebutton.png")));
        setEnabled(false);
    }
    
    private class AddLargeSizePropAction extends AddPropertyAction {

        public AddLargeSizePropAction(ToolBelt toolBelt) {
            super(toolBelt, "relative-size", "self", "large");
        }

    }

}
