package vars.annotation.ui.buttons;

import vars.ILink;
import vars.LinkBean;

import javax.swing.*;

/**
 * @author Brian Schlining
 * @since 2015-11-05T12:17:00
 */
public class PSizeButton extends PConstrainedConceptButton {


    private static final ImageIcon icon = new ImageIcon(PHabitatButton.class.getResource("/images/vars/annotation/size.png"));

    /**
     * Constructs ...
     */
    public PSizeButton() {
        super("Size categories",
                new LinkBean("size", "Size categories", ILink.VALUE_NIL),
                "add size",
                icon);
    }

}