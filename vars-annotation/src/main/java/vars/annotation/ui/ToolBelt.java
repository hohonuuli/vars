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
import vars.annotation.SpecialAnnotationDAO;
import vars.knowledgebase.HistoryFactory;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.SpecialKnowledgebaseDAO;
import vars.query.SpecialQueryDAO;

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
    private final PersistenceService persistenceService;
    private final SpecialAnnotationDAO specialAnnotationDAO;
    private final SpecialKnowledgebaseDAO specialKnowledgebaseDAO;
    private final SpecialQueryDAO specialQueryDAO;

    /**
     * Constructs ...
     *
     * @param annotationDAOFactory
     * @param annotationFactory
     * @param annotationDAO
     * @param knowledgebaseDAO
     * @param knowledgebaseDAOFactory
     * @param knowledgebaseFactory
     * @param miscDAOFactory
     * @param miscFactory
     * @param persistenceCacheProvider
     * @param queryDAO
     */
    @Inject
    public ToolBelt(AnnotationDAOFactory annotationDAOFactory, 
            AnnotationFactory annotationFactory,
            KnowledgebaseDAOFactory knowledgebaseDAOFactory, 
            KnowledgebaseFactory knowledgebaseFactory,
            MiscDAOFactory miscDAOFactory, MiscFactory miscFactory,
            PersistenceCacheProvider persistenceCacheProvider, 
            PersistenceService persistenceService, 
            SpecialAnnotationDAO annotationDAO,
            SpecialKnowledgebaseDAO knowledgebaseDAO, SpecialQueryDAO queryDAO) {
        this.annotationDAOFactory = annotationDAOFactory;
        this.specialAnnotationDAO = annotationDAO;
        this.annotationFactory = annotationFactory;
        this.specialKnowledgebaseDAO = knowledgebaseDAO;
        this.knowledgebaseDAOFactory = knowledgebaseDAOFactory;
        this.knowledgebaseFactory = knowledgebaseFactory;
        this.miscDAOFactory = miscDAOFactory;
        this.miscFactory = miscFactory;
        this.persistenceService = new PersistenceService(annotationDAOFactory, annotationFactory);
        this.persistenceCache = new PersistenceCache(persistenceCacheProvider);
        this.specialQueryDAO = queryDAO;
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
    public SpecialAnnotationDAO getSpecialAnnotationDAO() {
        return specialAnnotationDAO;
    }

    /**
     * @return
     */
    public SpecialKnowledgebaseDAO getSpecialKnowledgebaseDAO() {
        return specialKnowledgebaseDAO;
    }

    /**
     * @return
     */
    public SpecialQueryDAO getSpecialQueryDAO() {
        return specialQueryDAO;
    }

    public PersistenceService getPersistenceService() {
        return persistenceService;
    }
    
}
