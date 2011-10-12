package vars.annotation.ui.commandqueue;

import ij.Undo;

/**
 * EventBus Event class. Wraps a command that will be stored in the CommandQueue. It
 * has a direction, DO or UNDO. In general, only DO commands should be used by developers.
 * @author Brian Schlining
 * @since 2011-09-21
 */
public class CommandEvent {

    public static enum DoOrUndo {
        DO,
        UNDO;

        public DoOrUndo inverse() {
            return (this == DO) ? UNDO : DO;
        }
    }

    private final Command command;
    private final DoOrUndo doOrUndo;

    public CommandEvent(Command command) {
        this(command, DoOrUndo.DO);
    }

    public CommandEvent(Command command, DoOrUndo doOrUndo) {
        if (command == null || doOrUndo == null) {
            throw new IllegalArgumentException("null arguments are NOT allowed in the constructor");
        }
        this.command = command;
        this.doOrUndo = doOrUndo;
    }

    public Command getCommand() {
        return command;
    }

    public DoOrUndo getDoOrUndo() {
        return doOrUndo;
    }

}
