package vars.annotation.ui;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import org.mbari.util.SystemUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.VARSException;
import vars.avfoundation.AVFImageCaptureServiceImpl;
import vars.avplayer.FakeImageCaptureServiceImpl;
import vars.avplayer.ImageCaptureService;

import java.util.*;

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

        binder.bind(ImageCaptureService.class).to(AVFImageCaptureServiceImpl.class).in(Scopes.SINGLETON);

    }


}
