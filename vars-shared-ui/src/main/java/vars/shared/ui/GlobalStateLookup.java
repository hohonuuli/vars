package vars.shared.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;
import vars.UserAccount;
import vars.shared.ui.event.LoggingTopicSubscriber;

import java.awt.Frame;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * @author Brian Schlining
 * @since 2016-03-30T09:58:00
 */
public class GlobalStateLookup {

    /**
     * Subscribers to this topic will get a {@link String} as the data
     */
    public static final String TOPIC_NONFATAL_ERROR = "vars.shared.ui.GlobalLookup-TopicNonfatalError";

    /**
     * Subscribers to this topic will get and {@link Exception} as the data
     */
    public static final String TOPIC_FATAL_ERROR = "vars.shared.ui.GlobalLookup-TopicFatalError";

    public static final String TOPIC_WARNING = "vars.shared.ui.GlobalLookup-TopicWarning";

    public static final String TOPIC_USERACCOUNT = "vars.shared.ui.GlobalLookup-UserAccount";

    public static final String TOPIC_EXIT = "vars.shared.ui.GlobalLookup-Exit";

    public static final ObjectProperty<Frame> selectedFrame = new SimpleObjectProperty<>();
    private static final ObjectProperty<UserAccount> userAccount = new SimpleObjectProperty<>();
    private static final EventTopicSubscriber<UserAccount> userAccountSubscriber = (s, userAccount) -> setUserAccount(userAccount);
    private static File settingsDirectory;

    @SuppressWarnings("unchecked")
    public static final EventTopicSubscriber LOGGING_SUBSCRIBER = new LoggingTopicSubscriber();

    static {
        EventBus.subscribe(TOPIC_USERACCOUNT, userAccountSubscriber);

         /*
         * Create an application settings directory if needed and create the log directory
         */
        String home = System.getProperty("user.home");
        settingsDirectory = new File(home, ".vars");

        if (!settingsDirectory.exists()) {
            settingsDirectory.mkdir();
        }

        File logDirectory = new File(settingsDirectory, "logs");

        if (!logDirectory.exists()) {
            logDirectory.mkdir();
        }

        EventBus.subscribe(TOPIC_EXIT, LOGGING_SUBSCRIBER);
        EventBus.subscribe(TOPIC_USERACCOUNT, LOGGING_SUBSCRIBER);
        EventBus.subscribe(TOPIC_FATAL_ERROR, LOGGING_SUBSCRIBER);
        EventBus.subscribe(TOPIC_WARNING, LOGGING_SUBSCRIBER);
        EventBus.subscribe(TOPIC_NONFATAL_ERROR, LOGGING_SUBSCRIBER);
    }

    public static DateFormat getUTCDateFormat() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat;
    }

    public static Frame getSelectedFrame() {
        return selectedFrame.get();
    }

    public static ObjectProperty<Frame> selectedFrameProperty() {
        return selectedFrame;
    }

    public static void setSelectedFrame(Frame selectedFrame) {
        GlobalStateLookup.selectedFrame.set(selectedFrame);
    }

    public static UserAccount getUserAccount() {
        return userAccount.get();
    }

    public static ObjectProperty<UserAccount> userAccountProperty() {
        return userAccount;
    }

    public static void setUserAccount(UserAccount userAccount) {
        GlobalStateLookup.userAccount.set(userAccount);
    }

    /**
     * The local directory used for storing preferences (e.g. ~/.vars)
     * @return
     */
    public static File getSettingsDirectory() {
        return settingsDirectory;
    }
}
