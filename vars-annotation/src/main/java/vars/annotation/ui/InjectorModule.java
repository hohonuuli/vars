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

    private final vars.shared.InjectorModule varsIJ;

    public InjectorModule(String annotationPersistenceUnit,
            String knowledgebasePersistenceUnit,
            String miscPersistenceUnit) {
        varsIJ = new vars.shared.InjectorModule(annotationPersistenceUnit,
                knowledgebasePersistenceUnit,
                miscPersistenceUnit);


    }

    /**
     *
     * @param binder
     */
    public void configure(Binder binder) {
        try {
            binder.install(varsIJ);
        }
        catch (Exception ex) {
            throw new VARSException("Failed to initialize VARS shared dependency injection", ex);
        }

        try {
            binder.install(new vars.avplayer.InjectorModule());
        }
        catch (Exception e)  {
            throw new VARSException("Failed to intialize VARS avplayer dependency injection", e);
        }

    }

}
