/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mbari.vars.annotation.ui;

import javax.swing.ImageIcon;
import org.mbari.vars.annotation.ui.actions.AddRedSurfaceColorPropAction;

/**
 *
 * @author brian
 */
public class RedColorPropButton extends PropButton {

    public RedColorPropButton() {
        super();
        setAction(new AddRedSurfaceColorPropAction());
        setToolTipText("red surface color");
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/redbutton.png")));
        setEnabled(false);
    }
    
}
