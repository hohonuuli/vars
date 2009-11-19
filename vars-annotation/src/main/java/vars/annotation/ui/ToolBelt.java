/*
 * @(#)Toolbelt.java   2009.11.15 at 07:47:13 PST
 *
 * Copyright 2009 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.annotation.ui;

import com.google.inject.Inject;
import vars.MiscDAOFactory;
import vars.MiscFactory;
import vars.PersistenceCache;
import vars.PersistenceCacheProvider;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.AnnotationFactory;
import vars.annotation.AnnotationPersistenceService;
import vars.knowledgebase.HistoryFactory;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.KnowledgebasePersistenceService;
import vars.query.QueryPersistenceService;

/**
 *
 */
public class ToolBelt {

    private final AnnotationDAOFactory annotationDAOFactory;
    private final AnnotationFactory annotationFactory;
    private final HistoryFactory historyFactory;
    private final KnowledgebaseDAOFactory knowledgebaseDAOFactory;
    private final KnowledgebaseFactory knowledgebaseFactory;
    private final MiscDAOFactory miscDAOFactory;
    private final MiscFactory miscFactory;
    private final PersistenceCache persistenceCache;
    private final PersistenceController persistenceController;
    private final AnnotationPersistenceService annotationPersistenceService;
    private final KnowledgebasePersistenceService knowledgebasePersistenceService;
    private final QueryPersistenceService queryPersistenceService;

    /**
     * Constructs ...
     *
     * @param annotationDAOFactory
     * @param annotationFactory
     * @param annotationPersistenceService
     * @param knowledgebasePersistenceService
     * @param knowledgebaseDAOFactory
     * @param knowledgebaseFactory
     * @param miscDAOFactory
     * @param miscFactory
     * @param persistenceCacheProvider
     * @param queryPersistenceService
     */
    @Inject
    public ToolBelt(AnnotationDAOFactory annotationDAOFactory, 
            AnnotationFactory annotationFactory,
            KnowledgebaseDAOFactory knowledgebaseDAOFactory, 
            KnowledgebaseFactory knowledgebaseFactory,
            MiscDAOFactory miscDAOFactory, MiscFactory miscFactory,
            PersistenceCacheProvider persistenceCacheProvider, 
            PersistenceController persistenceService, 
            AnnotationPersistenceService annotationPersistenceService,
            KnowledgebasePersistenceService knowledgebasePersistenceService, QueryPersistenceService queryPersistenceService) {
        this.annotationDAOFactory = annotationDAOFactory;
        this.annotationPersistenceService = annotationPersistenceService;
        this.annotationFactory = annotationFactory;
        this.knowledgebasePersistenceService = knowledgebasePersistenceService;
        this.knowledgebaseDAOFactory = knowledgebaseDAOFactory;
        this.knowledgebaseFactory = knowledgebaseFactory;
        this.miscDAOFactory = miscDAOFactory;
        this.miscFactory = miscFactory;
        this.persistenceController = new PersistenceController(this);
        this.persistenceCache = new PersistenceCache(persistenceCacheProvider);
        this.queryPersistenceService = queryPersistenceService;
        historyFactory = new HistoryFactory(knowledgebaseFactory);
    }

    /**
     * @return
     */
    public AnnotationDAOFactory getAnnotationDAOFactory() {
        return annotationDAOFactory;
    }

    /**
     * @return
     */
    public AnnotationFactory getAnnotationFactory() {
        return annotationFactory;
    }

    /**
     * @return
     */
    public HistoryFactory getHistoryFactory() {
        return historyFactory;
    }

    /**
     * @return
     */
    public KnowledgebaseDAOFactory getKnowledgebaseDAOFactory() {
        return knowledgebaseDAOFactory;
    }

    /**
     * @return
     */
    public KnowledgebaseFactory getKnowledgebaseFactory() {
        return knowledgebaseFactory;
    }

    /**
     * @return
     */
    public MiscDAOFactory getMiscDAOFactory() {
        return miscDAOFactory;
    }

    /**
     * @return
     */
    public MiscFactory getMiscFactory() {
        return miscFactory;
    }

    /**
     * @return
     */
    public PersistenceCache getPersistenceCache() {
        return persistenceCache;
    }

    /**
     * @return
     */
    public AnnotationPersistenceService getAnnotationPersistenceService() {
        return annotationPersistenceService;
    }

    /**
     * @return
     */
    public KnowledgebasePersistenceService getKnowledgebasePersistenceService() {
        return knowledgebasePersistenceService;
    }

    /**
     * @return
     */
    public QueryPersistenceService getQueryPersistenceService() {
        return queryPersistenceService;
    }

    public PersistenceController getPersistenceController() {
        return persistenceController;
    }
    
}
