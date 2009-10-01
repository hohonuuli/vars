/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.query.ui;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.mbari.util.Dispatcher;
import vars.query.QueryModule;
import vars.shared.ui.FatalErrorSubscriber;
import vars.shared.ui.NonFatalErrorSubscriber;

/**
 *
 * @author brian
 */
public class Lookup {

    protected static final Object KEY_DISPATCHER_APPLICATION_FRAME = QueryFrame.class;

    protected static final Object KEY_DISPATCHER_APPLICATION = QueryApp.class;

    public static final String RESOURCE_BUNDLE = "query-app";

    public static final Object KEY_DISPATCHER_GUICE_INJECTOR = Injector.class;

    /**
     * Subscribers to this topic will get a {@link String} as the data
     */
    public static final String TOPIC_NONFATAL_ERROR = NonFatalErrorSubscriber.TOPIC_NONFATAL_ERROR;

    /**
     * Subscribers to this topic will get and {@link Exception} as the data
     */
    public static final String TOPIC_FATAL_ERROR = FatalErrorSubscriber.TOPIC_FATAL_ERROR;

    protected static Dispatcher getApplicationFrameDispatcher() {
        return Dispatcher.getDispatcher(KEY_DISPATCHER_APPLICATION_FRAME);
    }

    protected static Dispatcher getApplicationDispatcher() {
        return Dispatcher.getDispatcher(KEY_DISPATCHER_APPLICATION);
    }

    public static Dispatcher getGuiceInjectorDispatcher() {
        final Dispatcher dispatcher = Dispatcher.getDispatcher(KEY_DISPATCHER_GUICE_INJECTOR);
        Injector injector = (Injector) dispatcher.getValueObject();
        if (injector == null) {
            injector = Guice.createInjector(new QueryModule());
            dispatcher.setValueObject(injector);
        }
        return Dispatcher.getDispatcher(KEY_DISPATCHER_GUICE_INJECTOR);
    }

}
