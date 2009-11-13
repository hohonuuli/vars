/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mbari.vars.annotation.ui;

import javax.swing.ImageIcon;
import org.mbari.vars.annotation.ui.actions.AddJuvenileLifeStagePropAction;

/**
 *
 * @author brian
 */
public class JuvenileLifeStagePropButton extends PropButton {

    public JuvenileLifeStagePropButton() {
        super();
        setAction(new AddJuvenileLifeStagePropAction());
        setToolTipText("juvenile life-stage");
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/juvenilebutton.png")));
        setEnabled(false);
    }
    
    

}
