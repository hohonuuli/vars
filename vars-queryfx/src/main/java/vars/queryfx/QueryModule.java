package vars.queryfx;

import com.google.inject.Binder;
import com.google.inject.Module;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.util.BuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.jpa.VarsJpaModule;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptNameDAO;
import vars.knowledgebase.jpa.ConceptDAOImpl;
import vars.knowledgebase.jpa.ConceptNameDAOImpl;
import vars.queryfx.config.Resource;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;


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
        Resource resource = new Resource(StateLookup.getConfig());
        log.info(resource.getConfig().toString());
        annotationPersistenceUnit = resource.findByKey("vars.annotation.persistence.unit").get();
        knowledgebasePersistenceUnit = resource.findByKey("vars.knowledgebase.persistence.unit").get();
        miscPersistenceUnit = resource.findByKey("vars.misc.persistence.unit").get();
    }

    public void configure(Binder binder) {
        binder.install(new VarsJpaModule(annotationPersistenceUnit, knowledgebasePersistenceUnit, miscPersistenceUnit));
        binder.bind(BuilderFactory.class).to(JavaFXBuilderFactory.class);
        binder.bind(QueryService.class).to(QueryServiceImpl.class).asEagerSingleton();

        // Fork join pool causes problems in java web start
        //binder.bind(Executor.class).to(ForkJoinPool.class).asEagerSingleton();
        Executor executor = Executors.newCachedThreadPool();
        binder.bind(Executor.class).toInstance(executor);
    }
}
