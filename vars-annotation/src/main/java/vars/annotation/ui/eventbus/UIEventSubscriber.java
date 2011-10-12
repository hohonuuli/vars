package vars.annotation.ui.eventbus;

/**
 * @author Brian Schlining
 * @since 2011-10-11
 */
public interface UIEventSubscriber {

    void respondTo(ObservationAddedEvent event);

    void respondTo(ObservationsRemovedEvent event);

    void respondTo(ObservationsChangedEvent event);

    void respondTo(VideoArchiveChangedEvent event);

    void respondTo(ObservationsSelectedEvent event);

}
