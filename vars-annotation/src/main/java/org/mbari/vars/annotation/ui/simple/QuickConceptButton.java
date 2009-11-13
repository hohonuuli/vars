/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mbari.vars.annotation.ui.simple;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import org.mbari.vars.annotation.ui.PropButton;
import org.mbari.vars.annotation.ui.actions.NewObservationUsingConceptNameAction;

/**
 *
 * @author brian
 */
public class QuickConceptButton extends PropButton {
    
    /**
     *      Constructor
     */
    public QuickConceptButton(String concept, String iconResource) {
        super();
        setAction(new NewObservationUsingConceptNameAction(concept));
        setIcon(new ImageIcon(getClass().getResource(iconResource)));
        setToolTipText(concept);
        setText(concept);
        setVerticalTextPosition(AbstractButton.BOTTOM);
        setHorizontalTextPosition(AbstractButton.CENTER);
    }
}


