/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.shared;

import com.google.inject.Binder;
import com.google.inject.Module;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.PersistenceCacheProvider;
import vars.VARSException;
import vars.jpa.VarsJpaModule;



/**
 *
 * @author brian
 */
public class InjectorModule implements Module {

    public final Logger log = LoggerFactory.getLogger(getClass());

    private final String annotationPersistenceUnit;
    private final String knowledgebasePersistenceUnit;
    private final String miscPersistenceUnit;
    //private final String cacheProvider;

    public InjectorModule(String bundleName) {
        this(ResourceBundle.getBundle(bundleName));
    }


    public InjectorModule(ResourceBundle bundle) {
        annotationPersistenceUnit = bundle.getString("annotation.persistence.unit");
        knowledgebasePersistenceUnit = bundle.getString("knowledgebase.persistence.unit");
        miscPersistenceUnit = bundle.getString("misc.persistence.unit");
        //cacheProvider = bundle.getString("cache.provider.class");
    }

    public void configure(Binder binder) {
        try {
            binder.install(new VarsJpaModule(annotationPersistenceUnit, knowledgebasePersistenceUnit, miscPersistenceUnit));
            //binder.bind(PersistenceCacheProvider.class).to((Class<PersistenceCacheProvider>) Class.forName(cacheProvider));
        } catch (Exception ex) {
            throw new VARSException("Failed to intialize dependency injection", ex);
        }
    }

}