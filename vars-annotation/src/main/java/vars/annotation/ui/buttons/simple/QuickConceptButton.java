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
    
    /** This is static just for convenience, set once use everwhere ;-) */
    private static ToolBelt toolBelt;
    
    
    /**
     *      Constructor
     */
    public QuickConceptButton(String concept, String iconResource) {
        super();
        if (toolBelt == null) {
            throw new IllegalStateException("A ToolBelt object has not been set with the static method 'setToolBelt'");
        }
        setAction(new NewObservationUsingConceptNameAction(toolBelt, concept));
        setIcon(new ImageIcon(getClass().getResource(iconResource)));
        setToolTipText(concept);
        setText(concept);
        setVerticalTextPosition(AbstractButton.BOTTOM);
        setHorizontalTextPosition(AbstractButton.CENTER);
    }


    public static ToolBelt getToolBelt() {
        return toolBelt;
    }


    public static void setToolBelt(ToolBelt toolBelt) {
        QuickConceptButton.toolBelt = toolBelt;
    }
    
}


