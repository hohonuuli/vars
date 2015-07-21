package vars.queryfx;

import com.google.inject.Binder;
import com.google.inject.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.jpa.VarsJpaModule;
import vars.queryfx.config.Resource;


/**
 * @author Brian Schlining
 * @since 2015-07-19T13:05:00
 */
public class QueryModule implements Module {

    public final Logger log = LoggerFactory.getLogger(getClass());

    private final String annotationPersistenceUnit;
    private final String knowledgebasePersistenceUnit;
    private final String miscPersistenceUnit;


    public QueryModule() {
        Resource resource = new Resource(Lookup.getConfig());
        annotationPersistenceUnit = resource.findByKey("vars.annotation.persistence.unit").get();
        knowledgebasePersistenceUnit = resource.findByKey("vars.knowledgebase.persistence.unit").get();
        miscPersistenceUnit = resource.findByKey("vars.misc.persistence.unit").get();
    }

    public void configure(Binder binder) {
        binder.install(new VarsJpaModule(annotationPersistenceUnit, knowledgebasePersistenceUnit, miscPersistenceUnit));
    }
}
