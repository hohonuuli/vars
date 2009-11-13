/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mbari.vars.annotation.ui;

import javax.swing.ImageIcon;
import org.mbari.vars.annotation.ui.actions.AddChainArrangementPropAction;

/**
 *
 * @author brian
 */
public class ChainArrangementPropButton extends PropButton {

    public ChainArrangementPropButton() {
        super();
        setAction(new AddChainArrangementPropAction());
        setToolTipText("chain arrangement");
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/chainbutton.png")));
        setEnabled(false);
    }

}
