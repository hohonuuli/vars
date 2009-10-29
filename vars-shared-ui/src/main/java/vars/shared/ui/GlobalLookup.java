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

    /**
     * Subscribers to this topic will get and {@link Exception} as the data
     */
    public static final String TOPIC_FATAL_ERROR = "vars.shared.ui.GlobalLookup-TopicFatalError";

    public static final String TOPIC_WARNING = "vars.shared.ui.GlobalLookup-TopicWarning";

    public static final String TOPIC_USERACCOUNT = "vars.shared.ui.GlobalLookup-UserAccount";

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

}
