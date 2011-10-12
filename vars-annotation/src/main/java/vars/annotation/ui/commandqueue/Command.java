package vars.annotation.ui.commandqueue;

import vars.annotation.ui.ToolBelt;

/**
 * Wrapper for commands that change the data model (i.e. JPA managed objects).
 * The apply method is used to execute the command. The unapply method is used
 * to do the inverse operation so that undo and redo can be implemented. It's important
 * that the commands fire the events needed to trigger redraw events such as
 * {@link vars.annotation.ui.eventbus.ObservationsChangedEvent} so that the UI
 * components can sync state.
 * 
 * @author Brian Schlining
 * @since 2011-09-21
 */
public interface Command {

    /**
     * Apply/execute the command
     */
    void apply(ToolBelt toolBelt);

    /**
     * The inverse of apply, essentially an undo
     */
    void unapply(ToolBelt toolBelt);

    String getDescription();
   

}
