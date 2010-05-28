/*
 * @(#)KnowledgebaseDAOFactoryImpl.java   2009.11.23 at 04:22:36 PST
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



package vars.knowledgebase.jpa;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import vars.DAO;
import vars.jpa.EntityManagerFactoryAspect;
import vars.knowledgebase.*;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 11, 2009
 * Time: 9:37:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class KnowledgebaseDAOFactoryImpl implements KnowledgebaseDAOFactory, EntityManagerFactoryAspect {

    private final EntityManagerFactory entityManagerFactory;

    /**
     * Constructs ...
     *
     * @param entityManagerFactory
     */
    @Inject
    public KnowledgebaseDAOFactoryImpl(
            @Named("knowledgebasePersistenceUnit") EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * @return
     */
    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public ArtifactDAO newArtifactDAO() {
        return new ArtifactDAOImpl((entityManagerFactory.createEntityManager()));
    }

    public ArtifactDAO newArtifactDAO(EntityManager entityManager) {
        return new ArtifactDAOImpl(entityManager);
    }

    /**
     * @return
     */
    public ConceptDAO newConceptDAO() {
        return new ConceptDAOImpl(entityManagerFactory.createEntityManager());
    }

    /**
     *
     * @param entityManager
     * @return
     */
    public ConceptDAO newConceptDAO(EntityManager entityManager) {
        return new ConceptDAOImpl(entityManager);
    }

    /**
     * @return
     */
    public ConceptMetadataDAO newConceptMetadataDAO() {
        return new ConceptMetadataDAOImpl(entityManagerFactory.createEntityManager());
    }

    /**
     *
     * @param entityManager
     * @return
     */
    public ConceptMetadataDAO newConceptMetadataDAO(EntityManager entityManager) {
        return new ConceptMetadataDAOImpl(entityManager);
    }

    /**
     * @return
     */
    public ConceptNameDAO newConceptNameDAO() {
        return new ConceptNameDAOImpl(entityManagerFactory.createEntityManager());
    }

    /**
     *
     * @param entityManager
     * @return
     */
    public ConceptNameDAO newConceptNameDAO(EntityManager entityManager) {
        return new ConceptNameDAOImpl(entityManager);
    }

    /**
     * @return
     */
    public DAO newDAO() {
        return new vars.jpa.DAO(entityManagerFactory.createEntityManager());
    }

    /**
     *
     * @param entityManager
     * @return
     */
    public DAO newDAO(EntityManager entityManager) {
        return new vars.jpa.DAO(entityManager);
    }

    /**
     * @return
     */
    public HistoryDAO newHistoryDAO() {
        return new HistoryDAOImpl(entityManagerFactory.createEntityManager());
    }

    /**
     *
     * @param entityManager
     * @return
     */
    public HistoryDAO newHistoryDAO(EntityManager entityManager) {
        return new HistoryDAOImpl(entityManager);
    }

    /**
     * @return
     */
    public LinkRealizationDAO newLinkRealizationDAO() {
        return new LinkRealizationDAOImpl(entityManagerFactory.createEntityManager());
    }

    /**
     *
     * @param entityManager
     * @return
     */
    public LinkRealizationDAO newLinkRealizationDAO(EntityManager entityManager) {
        return new LinkRealizationDAOImpl(entityManager);
    }

    /**
     * @return
     */
    public LinkTemplateDAO newLinkTemplateDAO() {
        return new LinkTemplateDAOImpl(entityManagerFactory.createEntityManager());
    }

    /**
     *
     * @param entityManager
     * @return
     */
    public LinkTemplateDAO newLinkTemplateDAO(EntityManager entityManager) {
        return new LinkTemplateDAOImpl(entityManager);
    }

    /**
     * @return
     */
    public MediaDAO newMediaDAO() {
        return new MediaDAOImpl(entityManagerFactory.createEntityManager());
    }

    /**
     *
     * @param entityManager
     * @return
     */
    public MediaDAO newMediaDAO(EntityManager entityManager) {
        return new MediaDAOImpl(entityManager);
    }

    /**
     * @return
     */
    public UsageDAO newUsageDAO() {
        return new UsageDAOImpl(entityManagerFactory.createEntityManager());
    }

    /**
     *
     * @param entityManager
     * @return
     */
    public UsageDAO newUsageDAO(EntityManager entityManager) {
        return new UsageDAOImpl(entityManager);
    }
}
