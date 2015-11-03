package vars.annotation.ui.buttons;

import vars.ILink;
import vars.LinkBean;

import javax.swing.*;

/**
 * Created by brian on 11/2/15.
 */
public class PHabitatButton extends PConstrainedConceptButton {

    private static final ImageIcon icon =  new ImageIcon(PHabitatButton.class.getResource("/images/vars/annotation/habitat.png"));

    /**
     * Constructs ...
     */
    public PHabitatButton() {
        super("Habitats",
                new LinkBean("habitat", "Habitats", ILink.VALUE_NIL),
                "add habitat description",
                icon);
    }


}
