package vars.annotation.ui.buttons.simple;

import vars.annotation.ui.ToolBelt;

import javax.inject.Inject;

/**
 * @author Brian Schlining
 * @since 2017-07-26T16:07:00
 */
public class WhaleCarcassButton extends QuickConceptButton {

    @Inject
    public WhaleCarcassButton(ToolBelt toolBelt) {
        super("whale carcass", "/images/simple/whale_carcass.png", toolBelt);
    }

}