package vars.annotation.ui.buttons;

import vars.ILink;
import vars.LinkBean;

import javax.swing.*;

/**
 * Created by brian on 11/3/15.
 */
public class PMegahabitatButton extends PConstrainedConceptButton {


    private static final ImageIcon icon = new ImageIcon(PHabitatButton.class.getResource("/images/vars/annotation/megahabitat.png"));

    /**
     * Constructs ...
     */
    public PMegahabitatButton() {
        super("Megahabitats",
                new LinkBean("megahabitat", "Megahabitats", ILink.VALUE_NIL),
                "add megahabitat description",
                icon);
    }

}