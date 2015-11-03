package vars.annotation.ui.buttons;

import vars.ILink;
import vars.LinkBean;

import javax.swing.*;

/**
 * Created by rachelorange on 11/3/15.
 */
public class PSlopeButton extends PConstrainedConceptButton {

    private static final ImageIcon icon =  new ImageIcon(PHabitatButton.class.getResource("/images/vars/annotation/slope.png"));

    /**
     * Constructs ...
     */
    public PSlopeButton() {
        super("Slope",
                new LinkBean("slope", "Slope", ILink.VALUE_NIL),
                "add slope description",
                icon);

    }
}
