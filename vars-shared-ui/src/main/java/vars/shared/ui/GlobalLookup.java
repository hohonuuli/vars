/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.shared.ui;

import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.mbari.util.Dispatcher;
import vars.UserAccount;

/**
 *
 * @author brian
 */
public class GlobalLookup {
    
    protected static final Object KEY_DISPATCHER_SELECTED_FRAME = GlobalLookup.class.getName() + "-SelectedFrame";
    protected static final Object KEY_DISPATCHER_USERACCOUNT = GlobalLookup.class.getName() + "-UserAccount";

    /**
     * Subscribers to this topic will get a {@link String} as the data
     */
    public static final String TOPIC_NONFATAL_ERROR = NonFatalErrorSubscriber.TOPIC_NONFATAL_ERROR;

    /**
     * Subscribers to this topic will get and {@link Exception} as the data
     */
    public static final String TOPIC_FATAL_ERROR = FatalErrorSubscriber.TOPIC_FATAL_ERROR;

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
    }

    /**
     *
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
