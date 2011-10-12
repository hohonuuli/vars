package vars.annotation.ui.commandqueue;

/**
 * @author Brian Schlining
 * @since 2011-09-21
 */
public class UndoCommandEvent extends CommandEvent {
    public UndoCommandEvent(Command command) {
        super(command, DoOrUndo.UNDO);
    }
}
