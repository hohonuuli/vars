package vars.annotation.ui;

import com.google.inject.Binder;
import com.google.inject.Module;
import vars.VARSException;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Brian Schlining
 * @since 2015-10-01T14:26:00
 */
public class InjectorModule implements Module {

    private static ResourceBundle bundle;  // HACK! Must be static

    /**
     * Constructs ...
     *
     * @param bundle
     */
    public InjectorModule(ResourceBundle bundle) {

    }

    /**
     * Constructs ...
     *
     * @param bundleName
     */
    public InjectorModule(String bundleName) {
        this(ResourceBundle.getBundle(bundleName, Locale.US));
    }

    /**
     *
     * @param binder
     */
    public void configure(Binder binder) {
        try {
            binder.install(new vars.shared.InjectorModule(bundle));
        }
        catch (Exception ex) {
            throw new VARSException("Failed to initialize VARS shared dependency injection", ex);
        }

        try {
            binder.install(new vars.avplayer.InjectorModule(bundle));
        }
        catch (Exception e)  {
            throw new VARSException("Failed to intialize VARS avplayer dependency injection", e);
        }

    }

}
