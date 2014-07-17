/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui.buttons.simple;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;

import vars.annotation.ui.actions.NewObservationUsingConceptNameAction;
import vars.annotation.ui.buttons.PropButton;
import vars.annotation.ui.ToolBelt;

/**
 *
 * @author brian
 */
public class QuickConceptButton extends PropButton {

    private ToolBelt toolBelt;
    
    
    /**
     *      Constructor
     */
    public QuickConceptButton(String concept, String iconResource, ToolBelt toolBelt) {
        super();
        if (toolBelt == null) {
            throw new IllegalStateException("The ToolBelt argument can not be null");
        }
        setAction(new NewObservationUsingConceptNameAction(toolBelt, concept));
        setIcon(new ImageIcon(getClass().getResource(iconResource)));
        setToolTipText(concept);
        setText(concept);
        setVerticalTextPosition(AbstractButton.BOTTOM);
        setHorizontalTextPosition(AbstractButton.CENTER);
    }


    
}


