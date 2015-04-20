/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.shared.ui;

import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;
import org.mbari.util.Dispatcher;
import vars.UserAccount;
import vars.shared.ui.event.LoggingTopicSubscriber;

/**
 * Central lookup for coordinating shared UI resources.
 * 
 * @author brian
 */
public class GlobalLookup {

    /**
     * Standard format for all Dates used in SIMPA. No timezone is displayed.
     * THe date will be formatted for the UTC timezone
     */
    public static final DateFormat DATE_FORMAT_UTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * Formats the dates using whatever the local timezone is. TImezone info
     * is displayed by this formatter
     */
    public static final DateFormat DATE_FORMAT_LOCAL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    
    protected static final Object KEY_DISPATCHER_SELECTED_FRAME = "vars.shared.ui.GlobalLookup-SelectedFrame";
    protected static final Object KEY_DISPATCHER_USERACCOUNT = "vars.shared.ui.GlobalLookup-UserAccount";

    /**
     * Subscribers to this topic will get a {@link String} as the data
     */
    public static final String TOPIC_NONFATAL_ERROR = "vars.shared.ui.GlobalLookup-TopicNonfatalError";

    public static final String TOPIC_EXIT = "vars.shared.ui.GlobalLookup-Exit";

    /**
     * Subscribers to this topic will get and {@link Exception} as the data
     */
    public static final String TOPIC_FATAL_ERROR = "vars.shared.ui.GlobalLookup-TopicFatalError";

    public static final String TOPIC_WARNING = "vars.shared.ui.GlobalLookup-TopicWarning";

    public static final String TOPIC_USERACCOUNT = "vars.shared.ui.GlobalLookup-UserAccount";

    @SuppressWarnings("unchecked")
	public static final EventTopicSubscriber LOGGING_SUBSCRIBER = new LoggingTopicSubscriber();
    
    private static File settingsDirectory;
    
    private static final EventTopicSubscriber userAccountSubscriber = new EventTopicSubscriber<UserAccount>() {
        public void onEvent(String topic, UserAccount data) {
            getUserAccountDispatcher().setValueObject(data);
        }
    };

    /*
     * Throw an exception if the wrong parameter type is set
     */
    static {


        DATE_FORMAT_UTC.setTimeZone(TimeZone.getTimeZone("UTC"));

        getSelectedFrameDispatcher().addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                Object obj = evt.getNewValue();
                if (!(obj instanceof Frame)) {
                    throw new IllegalArgumentException("Required: " +
                            Frame.class.getName() + ", Found: " + obj.getClass().getName());
                }
            }
        });

        getUserAccountDispatcher().addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                Object obj = evt.getNewValue();
                if (obj != null && !(obj instanceof UserAccount)) {
                    throw new IllegalArgumentException("Required: " +
                            UserAccount.class.getName() + ", Found: " + obj.getClass().getName());
                }
            }
        });

        /*
         * When a UserAccount is sent to this topic on event bus make sure it gets
         * relayed to the correct Dispatcher
         */
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

    /**
     * Reference to a Dispatcher that should be used to manage the currently selected frame
     * @return A Dispatcher referring to a java.awt.Frame object
     */
    public static Dispatcher getSelectedFrameDispatcher() {
        return Dispatcher.getDispatcher(KEY_DISPATCHER_SELECTED_FRAME);
    }

    /**
     *
     * @return A Dispatcher referring to a vars.UserAccount object. The intent is
     *      that this is the currently used UserAccount in a UI application
     */
    public static Dispatcher getUserAccountDispatcher() {
        return Dispatcher.getDispatcher(KEY_DISPATCHER_USERACCOUNT);
    }

    /**
     * The local directory used for storing preferences (e.g. ~/.vars)
     * @return
     */
    public static File getSettingsDirectory() {
    	return settingsDirectory;
    }



}
