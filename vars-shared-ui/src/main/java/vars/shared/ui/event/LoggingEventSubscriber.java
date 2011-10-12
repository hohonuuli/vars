package vars.shared.ui.event;

import org.bushe.swing.event.EventSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Brian Schlining
 * @since 2011-10-10
 */
public class LoggingEventSubscriber implements EventSubscriber {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void onEvent(Object event) {
        log.debug("Event Published:\n\tEVENT: " + event);
    }
}
