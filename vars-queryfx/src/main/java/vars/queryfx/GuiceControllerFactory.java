package vars.queryfx;

import com.google.inject.Injector;
import javafx.util.Callback;

import javax.inject.Inject;

/**
 * @author Brian Schlining
 * @since 2015-07-22T09:37:00
 */
public class GuiceControllerFactory implements Callback<Class, Object> {

    private final Injector injector;

    @Inject
    public GuiceControllerFactory(Injector injector) {
        this.injector = injector;
    }

    @Override
    public Object call(Class param) {
        return injector.getInstance(param);
    }
}
