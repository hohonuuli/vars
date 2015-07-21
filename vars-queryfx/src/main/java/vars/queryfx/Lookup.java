package vars.queryfx;

import com.google.common.base.Preconditions;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import vars.queryfx.ui.App;
import vars.shared.ui.GlobalLookup;

/**
 * @author Brian Schlining
 * @since 2015-07-17T16:47:00
 */
public class Lookup extends GlobalLookup {


    private static final String appConfig = "vars/queryfx/app";


    private static Injector injector;
    private static App app;
    private static Config config;
    private static Object injectorLock = new Object() {};
    private static Object configLock = new Object() {};

    public static Injector getInjector() {
        synchronized (injectorLock) {
            if (injector == null) {
                injector = Guice.createInjector(new QueryModule());
            }
        }
        return injector;
    }


    public static App getApp() {
        return app;
    }

    protected static void setApp(App app) {
        Preconditions.checkArgument(app != null);
        Lookup.app = app;
    }

    public static Config getConfig() {
        synchronized (configLock) {
            if (config == null) {
                config = ConfigFactory.load(appConfig);
            }
        }
        return config;
    }


}
