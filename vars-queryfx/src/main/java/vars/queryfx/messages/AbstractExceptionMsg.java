package vars.queryfx.messages;

/**
 * @author Brian Schlining
 * @since 2015-07-19T13:41:00
 */
public abstract class AbstractExceptionMsg implements StatusMsg {

    private final String msg;
    private final Throwable exception;

    public AbstractExceptionMsg(String msg, Throwable exception) {
        this.exception = exception;
        this.msg = msg;
    }

    public Throwable getException() {
        return exception;
    }

    public String getMsg() {
        return msg;
    }
}
