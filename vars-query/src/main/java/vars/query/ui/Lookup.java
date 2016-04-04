/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.query.ui;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.mbari.util.Dispatcher;
import vars.query.QueryModule;
import vars.shared.ui.GlobalLookup;

/**
 *
 * @author brian
 * @deprecated
 */
public class Lookup extends GlobalLookup {

    protected static final Object KEY_DISPATCHER_APPLICATION_FRAME = QueryFrame.class;

    protected static final Object KEY_DISPATCHER_APPLICATION = App.class;

    public static final String RESOURCE_BUNDLE = "query-app";

    public static final Object KEY_DISPATCHER_GUICE_INJECTOR = Injector.class;


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
