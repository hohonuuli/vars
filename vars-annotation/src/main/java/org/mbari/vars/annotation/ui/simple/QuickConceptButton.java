/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mbari.vars.annotation.ui.simple;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import org.mbari.vars.annotation.ui.PropButton;
import org.mbari.vars.annotation.ui.actions.NewObservationUsingConceptNameAction;

import vars.annotation.ui.ToolBelt;

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
        setAction(new NewObservationUsingConceptNameAction(toolBelt, concept));
        setIcon(new ImageIcon(getClass().getResource(iconResource)));
        setToolTipText(concept);
        setText(concept);
        setVerticalTextPosition(AbstractButton.BOTTOM);
        setHorizontalTextPosition(AbstractButton.CENTER);
    }
}


