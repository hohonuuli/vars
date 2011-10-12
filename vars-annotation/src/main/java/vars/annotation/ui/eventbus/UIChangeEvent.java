package vars.annotation.ui.eventbus;

/**
 * Base class for update events
 * @author Brian Schlining
 * @since 2011-09-20
 */
public class UIChangeEvent<A> extends UIEvent<A> {

    public UIChangeEvent(Object changeSource, A refs) {
        super(changeSource, refs);
    }

}
