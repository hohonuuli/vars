package vars.shared.rx.messages;

/**
 * @author Brian Schlining
 * @since 2015-07-19T13:41:00
 */
public abstract class AbstractExceptionMsg implements StatusMsg {

    private final String message;
    private final Throwable exception;

    public AbstractExceptionMsg(String message, Throwable exception) {
        this.exception = exception;
        this.message = message;
    }

    public Throwable getException() {
        return exception;
    }

    public String getMessage() {
        return message;
    }
}
