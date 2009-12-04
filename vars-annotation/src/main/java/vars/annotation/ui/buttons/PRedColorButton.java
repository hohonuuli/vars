/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui.buttons;

import javax.swing.ImageIcon;


import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.actions.AddPropertyAction;

/**
 *
 * @author brian
 */
public class PRedColorButton extends PropButton {

    public PRedColorButton(ToolBelt toolBelt) {
        super();
        setAction(new AddRedSurfaceColorPropAction(toolBelt));
        setToolTipText("red surface color");
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/redbutton.png")));
        setEnabled(false);
    }
    
    class AddRedSurfaceColorPropAction extends AddPropertyAction {

        public AddRedSurfaceColorPropAction(ToolBelt toolBelt) {
            super(toolBelt, "surface-color", "self", "red");
        }

    }
    
}
