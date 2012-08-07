package vars.annotation.ui.imagepanel;

import vars.annotation.ui.eventbus.UIChangeEvent;

/**
 * @author Brian Schlining
 * @since 2012-08-07
 */
public class IAFRepaintEvent extends UIChangeEvent<UIDataCoordinator> {

    public IAFRepaintEvent(Object changeSource, UIDataCoordinator refs) {
        super(changeSource, refs);
    }
}
