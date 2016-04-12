package vars.queryfx;

import vars.MiscDAOFactory;
import vars.MiscFactory;
import vars.PersistenceCacheProvider;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.AnnotationFactory;
import vars.annotation.AnnotationPersistenceService;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.KnowledgebasePersistenceService;
import vars.query.QueryPersistenceService;

import com.google.inject.Inject;

import java.util.concurrent.Executor;

public class ToolBelt extends vars.ToolBelt {

    private final AsyncQueryService queryService;
    private final Executor executor;

    @Inject
    public ToolBelt(AnnotationDAOFactory annotationDAOFactory,
                    AnnotationFactory annotationFactory,
                    KnowledgebaseDAOFactory knowledgebaseDAOFactory,
                    KnowledgebaseFactory knowledgebaseFactory,
                    MiscDAOFactory miscDAOFactory, MiscFactory miscFactory,
                    PersistenceCacheProvider persistenceCacheProvider,
                    AnnotationPersistenceService annotationPersistenceService,
                    KnowledgebasePersistenceService knowledgebasePersistenceService,
                    QueryPersistenceService queryPersistenceService,
                    AsyncQueryService queryService,
                    Executor executor) {
        super(annotationDAOFactory, annotationFactory, knowledgebaseDAOFactory,
                knowledgebaseFactory, miscDAOFactory, miscFactory,
                persistenceCacheProvider, annotationPersistenceService,
                knowledgebasePersistenceService, queryPersistenceService);
        this.queryService = queryService;
        this.executor = executor;
    }

    public AsyncQueryService getQueryService() {
        return queryService;
    }

    public Executor getExecutor() {
        return executor;
    }
}