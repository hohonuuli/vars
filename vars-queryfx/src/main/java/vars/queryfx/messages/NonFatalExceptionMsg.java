package vars.queryfx.messages;

/**
 * @author Brian Schlining
 * @since 2015-07-19T13:39:00
 */
public class NonFatalExceptionMsg extends AbstractExceptionMsg {

    public NonFatalExceptionMsg(String msg, Exception exception) {
        super(msg, exception);
    }
}
