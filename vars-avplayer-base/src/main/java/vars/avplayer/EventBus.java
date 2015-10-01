package vars.avplayer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.shared.rx.RXEventBus;

/**
 * @author Brian Schlining
 * @since 2015-10-01T16:00:00
 */
public class EventBus {

    private static RXEventBus eventBus;
    private static final Logger log = LoggerFactory.getLogger(EventBus.class);

    protected void setEventBus(RXEventBus eventBus) {
        EventBus.eventBus = eventBus;
    }

    public static void send(Object obj) {
        if (eventBus != null) {
            eventBus.send(obj);
        }
        else {
            log.warn("No RXEventBus exists. Logging message: " + obj);
        }
    }

}
