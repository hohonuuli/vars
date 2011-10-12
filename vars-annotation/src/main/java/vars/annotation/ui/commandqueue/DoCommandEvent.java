package vars.annotation.ui.commandqueue;

/**
 * @author Brian Schlining
 * @since 2011-09-21
 */
public class DoCommandEvent extends CommandEvent {
    public DoCommandEvent(Command command) {
        super(command, DoOrUndo.DO);
    }


}
