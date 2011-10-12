package vars.annotation.ui.eventbus;

/**
 * @author Brian Schlining
 * @since 2011-09-20
 */
public class UISelectionEvent<A> extends UIEvent<A> {

    public UISelectionEvent(Object selectionSource, A refs) {
        super(selectionSource, refs);
    }

    public Object getSelectionSource() {
        return eventSource;
    }
}