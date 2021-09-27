package vars.annotation.ui.buttons;

import mbarix4j.awt.event.ActionAdapter;
import vars.ILink;
import vars.LinkBean;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.actions.AddPropertyAction;
import vars.annotation.ui.dialogs.ToConceptSelectionDialog;

import javax.swing.*;

/**
 * @author Brian Schlining
 * @since 2015-11-03T07:44:00
 */
public class PRugosityButton extends PConstrainedConceptButton {

    private static final ImageIcon icon =  new ImageIcon(PHabitatButton.class.getResource("/images/vars/annotation/rugosity.png"));

    /**
     * Constructs ...
     */
    public PRugosityButton() {
        super("Rugosity",
                new LinkBean("rugosity", "Rugosity", ILink.VALUE_NIL),
                "add rugosity description",
                icon);

    }
}
