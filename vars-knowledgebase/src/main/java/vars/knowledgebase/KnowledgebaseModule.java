package vars.knowledgebase;

import com.google.inject.Module;
import com.google.inject.Binder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ResourceBundle;

import vars.PersistenceCacheProvider;
import vars.jpa.VarsJpaModule;
import vars.knowledgebase.jpa.KnowledgbaseCacheProvider;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Sep 29, 2009
 * Time: 11:42:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class KnowledgebaseModule implements Module {

    public final Logger log = LoggerFactory.getLogger(getClass());

    private final String annotationPersistenceUnit;
    private final String knowledgebasePersistenceUnit;
    private final String miscPersistenceUnit;


    public KnowledgebaseModule() {
        ResourceBundle bundle = ResourceBundle.getBundle("knowledgebase-app");
        annotationPersistenceUnit = bundle.getString("annotation.persistence.unit");
        knowledgebasePersistenceUnit = bundle.getString("knowledgebase.persistence.unit");
        miscPersistenceUnit = bundle.getString("misc.persistence.unit");
    }

    public void configure(Binder binder) {
        binder.install(new VarsJpaModule(annotationPersistenceUnit, knowledgebasePersistenceUnit, miscPersistenceUnit));
        binder.bind(PersistenceCacheProvider.class).to(KnowledgbaseCacheProvider.class);
    }
}
