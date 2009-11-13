/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mbari.vars.annotation.ui;

import javax.swing.ImageIcon;
import org.mbari.vars.annotation.ui.actions.AddLargeSizePropAction;

/**
 *
 * @author brian
 */
public class LargeSizePropButton extends PropButton {

    public LargeSizePropButton() {
        super();
        setAction(new AddLargeSizePropAction());
        setToolTipText("relatively large size");
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/largebutton.png")));
        setEnabled(false);
    }

}
