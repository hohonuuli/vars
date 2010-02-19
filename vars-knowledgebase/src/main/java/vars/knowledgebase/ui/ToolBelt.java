/*
 * @(#)ToolBelt.java   2009.12.03 at 11:24:13 PST
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



package vars.knowledgebase.ui;

import com.google.inject.Inject;
import vars.MiscDAOFactory;
import vars.MiscFactory;
import vars.PersistenceCacheProvider;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.AnnotationFactory;
import vars.annotation.AnnotationPersistenceService;
import vars.knowledgebase.HistoryFactory;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.KnowledgebasePersistenceService;
import vars.knowledgebase.ui.actions.ApproveHistoryTask;
import vars.knowledgebase.ui.actions.RejectHistoryTask;
import vars.query.QueryPersistenceService;

/**
 * Container that holds on to a ton of shared objects that need to be widely
 * used across this application
 */
public class ToolBelt extends vars.ToolBelt {

    private final ApproveHistoryTask approveHistoryTask;
    private final HistoryFactory historyFactory;
    private final RejectHistoryTask rejectHistoryTask;

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
    public ToolBelt(AnnotationDAOFactory annotationDAOFactory, AnnotationFactory annotationFactory,
                    KnowledgebaseDAOFactory knowledgebaseDAOFactory, KnowledgebaseFactory knowledgebaseFactory,
                    MiscDAOFactory miscDAOFactory, MiscFactory miscFactory,
                    PersistenceCacheProvider persistenceCacheProvider,
                    AnnotationPersistenceService annotationPersistenceService,
                    KnowledgebasePersistenceService knowledgebasePersistenceService,
                    QueryPersistenceService queryPersistenceService) {
        super(annotationDAOFactory, annotationFactory, knowledgebaseDAOFactory, knowledgebaseFactory, miscDAOFactory,
              miscFactory, persistenceCacheProvider, annotationPersistenceService, knowledgebasePersistenceService,
              queryPersistenceService);
        historyFactory = new HistoryFactory(knowledgebaseFactory);
        approveHistoryTask = new ApproveHistoryTask(this);
        rejectHistoryTask = new RejectHistoryTask(this);
    }


    /**
     * @return
     */
    public ApproveHistoryTask getApproveHistoryTask() {
        return approveHistoryTask;
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
    public RejectHistoryTask getRejectHistoryTask() {
        return rejectHistoryTask;
    }
}
