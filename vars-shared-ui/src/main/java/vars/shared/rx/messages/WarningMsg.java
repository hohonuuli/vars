package vars.shared.rx.messages;

/**
 * @author Brian Schlining
 * @since 2015-07-19T13:39:00
 */
public class WarningMsg implements StatusMsg {

    private final String message;

    public WarningMsg(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
