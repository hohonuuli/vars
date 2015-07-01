/*
 * @(#)ToolBelt.java   2009.12.03 at 11:28:15 PST
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
import vars.PersistenceCacheProvider;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.AnnotationFactory;
import vars.annotation.AnnotationPersistenceService;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.KnowledgebasePersistenceService;
import vars.query.QueryPersistenceService;

/**
 *
 */
public class ToolBelt extends vars.ToolBelt {

    private final PersistenceController persistenceController;

    /**
     * Constructs ...
     *
     * @param annotationDAOFactory
     * @param annotationFactory
     * @param knowledgebaseDAOFactory
     * @param knowledgebaseFactory
     * @param miscDAOFactory
     * @param miscFactory
     * @param persistenceCacheProvider
     * @param annotationPersistenceService
     * @param knowledgebasePersistenceService
     * @param queryPersistenceService
     */
    @Inject
    public ToolBelt(AnnotationDAOFactory annotationDAOFactory,
                    AnnotationFactory annotationFactory,
                    KnowledgebaseDAOFactory knowledgebaseDAOFactory,
                    KnowledgebaseFactory knowledgebaseFactory,
                    MiscDAOFactory miscDAOFactory,
                    MiscFactory miscFactory,
                    PersistenceCacheProvider persistenceCacheProvider,
                    AnnotationPersistenceService annotationPersistenceService,
                    KnowledgebasePersistenceService knowledgebasePersistenceService,
                    QueryPersistenceService queryPersistenceService) {
        super(annotationDAOFactory, annotationFactory, knowledgebaseDAOFactory, knowledgebaseFactory, miscDAOFactory,
              miscFactory, persistenceCacheProvider, annotationPersistenceService, knowledgebasePersistenceService,
              queryPersistenceService);
        this.persistenceController = new PersistenceController(this);
    }

    /**
     * @return
     */
    public PersistenceController getPersistenceController() {
        return persistenceController;
    }

}
