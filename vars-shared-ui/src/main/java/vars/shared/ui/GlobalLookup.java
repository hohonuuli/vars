/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.shared.ui;

import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;
import org.mbari.util.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.UserAccount;

/**
 * Central lookup for coordinating shared UI resources.
 * 
 * @author brian
 */
public class GlobalLookup {
    
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

    public static final EventTopicSubscriber<Object> LOGGING_SUBSCRIBER = new LoggingSubscriber();

    /*
     * Throw an exception if the wrong parameter type is set
     */
    static {
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
                if (!(obj instanceof UserAccount)) {
                    throw new IllegalArgumentException("Required: " +
                            UserAccount.class.getName() + ", Found: " + obj.getClass().getName());
                }
            }
        });

        /*
         * When a UserAccount is sent to this topic on event bus make sure it gets
         * relayed to the correct Dispatcher
         */
        EventBus.subscribe(TOPIC_USERACCOUNT, new EventTopicSubscriber<UserAccount>() {
            public void onEvent(String topic, UserAccount data) {
                getUserAccountDispatcher().setValueObject(data);
            }
        });
        


        EventBus.subscribe(TOPIC_EXIT, LOGGING_SUBSCRIBER);
        EventBus.subscribe(TOPIC_USERACCOUNT, LOGGING_SUBSCRIBER);
        EventBus.subscribe(TOPIC_FATAL_ERROR, LOGGING_SUBSCRIBER);
        EventBus.subscribe(TOPIC_WARNING, LOGGING_SUBSCRIBER);
        EventBus.subscribe(TOPIC_NONFATAL_ERROR, LOGGING_SUBSCRIBER);
    }

    /**
     * Reference to a Dispatcher that should be used to manage the currently selected frame
     * @return A Dispatcher refering to a java.awt.Frame object
     */
    public static Dispatcher getSelectedFrameDispatcher() {
        return Dispatcher.getDispatcher(KEY_DISPATCHER_SELECTED_FRAME);
    }

    /**
     *
     * @return A Dispatcher refering to a vars.UserAccount object. The intent is
     *      that this is the currently used UserAccount in a UI application
     */
    public static Dispatcher getUserAccountDispatcher() {
        return Dispatcher.getDispatcher(KEY_DISPATCHER_USERACCOUNT);
    }

    /**
     * Log events in debug mode
     */
    static class LoggingSubscriber implements EventTopicSubscriber {

        private final Logger log = LoggerFactory.getLogger(getClass());

        public void onEvent(String topic, Object data) {
            if (log.isDebugEnabled()) {
                log.debug("Event Published:\n\tTOPIC: " + topic +  "\n\tDATA: " + data);
            }
        }

    }

}
