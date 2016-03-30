package vars.query.ui;

import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import vars.query.QueryModule;
import vars.shared.ui.GlobalStateLookup;

/**
 * @author Brian Schlining
 * @since 2016-03-30T16:34:00
 */
public class StateLookup extends GlobalStateLookup {

    public static final String RESOURCE_BUNDLE = "query-app";
    public static final Injector GUICE_INJECTOR = Guice.createInjector(new QueryModule());
    private static final ObjectProperty<App> application = new SimpleObjectProperty<>();
    private static final ObjectProperty<QueryFrame> applicationFrame = new SimpleObjectProperty<>();

    public static App getApplication() {
        return application.get();
    }

    public static ObjectProperty<App> applicationProperty() {
        return application;
    }

    public static void setApplication(App application) {
        StateLookup.application.set(application);
    }

    public static QueryFrame getApplicationFrame() {
        return applicationFrame.get();
    }

    public static ObjectProperty<QueryFrame> applicationFrameProperty() {
        return applicationFrame;
    }

    public static void setApplicationFrame(QueryFrame applicationFrame) {
        StateLookup.applicationFrame.set(applicationFrame);
    }
}
