package vars.annotation.ui.eventbus;

/**
 * @author Brian Schlining
 * @since 2011-09-20
 */
public class UIEvent<A> {
    protected final Object eventSource;
    protected final A refs;

    public UIEvent(Object source, A refs) {
        this.eventSource = source;
        this.refs = refs;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{source=" + eventSource + ", refs=" + refs + "}";
    }

    public A get() {
        return refs;
    }

    public Object getEventSource() {
        return eventSource;
    }

}
