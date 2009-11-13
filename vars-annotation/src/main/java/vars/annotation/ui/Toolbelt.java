/*
 * @(#)Toolbelt.java   2009.11.13 at 09:32:51 PST
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
import vars.knowledgebase.HistoryFactory;
import vars.knowledgebase.KnowledgebaseDAO;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.query.QueryDAO;

/**
 *
 */
public class Toolbelt {

    private final AnnotationDAOFactory annotationDAOFactory;
    private final AnnotationFactory annotationFactory;
    private final HistoryFactory historyFactory;
    private final KnowledgebaseDAO knowledgebaseDAO;
    private final KnowledgebaseDAOFactory knowledgebaseDAOFactory;
    private final KnowledgebaseFactory knowledgebaseFactory;
    private final MiscDAOFactory miscDAOFactory;
    private final MiscFactory miscFactory;
    private final PersistenceCache persistenceCache;
    private final QueryDAO queryDAO;

    /**
     * Constructs ...
     *
     * @param annotationDAOFactory
     * @param annotationFactory
     * @param knowledgebaseDAO
     * @param knowledgebaseDAOFactory
     * @param knowledgebaseFactory
     * @param miscDAOFactory
     * @param miscFactory
     * @param persistenceCacheProvider
     * @param queryDAO
     */
    @Inject
    public Toolbelt(AnnotationDAOFactory annotationDAOFactory, AnnotationFactory annotationFactory,
                    KnowledgebaseDAO knowledgebaseDAO, KnowledgebaseDAOFactory knowledgebaseDAOFactory,
                    KnowledgebaseFactory knowledgebaseFactory, MiscDAOFactory miscDAOFactory, MiscFactory miscFactory,
                    PersistenceCacheProvider persistenceCacheProvider, QueryDAO queryDAO) {
        this.annotationDAOFactory = annotationDAOFactory;
        this.annotationFactory = annotationFactory;
        this.knowledgebaseDAO = knowledgebaseDAO;
        this.knowledgebaseDAOFactory = knowledgebaseDAOFactory;
        this.knowledgebaseFactory = knowledgebaseFactory;
        this.miscDAOFactory = miscDAOFactory;
        this.miscFactory = miscFactory;
        this.persistenceCache = new PersistenceCache(persistenceCacheProvider);
        this.queryDAO = queryDAO;
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
    public KnowledgebaseDAO getKnowledgebaseDAO() {
        return knowledgebaseDAO;
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
    public QueryDAO getQueryDAO() {
        return queryDAO;
    }

}
