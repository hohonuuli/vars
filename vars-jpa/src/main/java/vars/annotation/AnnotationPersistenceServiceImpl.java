/*
 * @(#)AnnotationPersistenceServiceImpl.java   2009.11.18 at 08:57:37 PST
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



package vars.annotation;

import com.google.inject.Inject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import vars.CacheClearedEvent;
import vars.CacheClearedListener;
import vars.DAO;
import vars.PersistenceCache;
import vars.QueryableImpl;
import vars.annotation.jpa.AssociationDAOImpl;
import vars.annotation.jpa.ObservationDAOImpl;
import vars.annotation.jpa.VideoArchiveDAOImpl;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.LinkTemplate;
import vars.knowledgebase.LinkTemplateDAO;
import vars.knowledgebase.jpa.ConceptDAOImpl;
import vars.knowledgebase.jpa.LinkTemplateDAOImpl;

/**
 * All methods in this class are thread safe. They are backed by threadLocal EntityManagers OR
 * by a single use JDBC connection.
 * @author brian
 */
public class AnnotationPersistenceServiceImpl extends QueryableImpl implements AnnotationPersistenceService {

    private static final String jdbcDriver;
    private static final String jdbcPassword;
    private static final String jdbcUrl;
    private static final String jdbcUsername;

    static {
        ResourceBundle bundle = ResourceBundle.getBundle("annotation-jdbc");
        jdbcUrl = bundle.getString("jdbc.url");
        jdbcUsername = bundle.getString("jdbc.username");
        jdbcPassword = bundle.getString("jdbc.password");
        jdbcDriver = bundle.getString("jdbc.driver");
    }

    /** JPA is not thread safe, so once DAO per thread */
    private final ThreadLocal<EntityManager> readWriteEntityManagers = new ThreadLocal<EntityManager>();
    private final ThreadLocal<EntityManager> readOnlyEntityManagers = new ThreadLocal<EntityManager>();
    private final EntityManagerFactory entityManagerFactory;
    private final PersistenceCache persistenceCache;

    /**
     * Constructs ...
     *
     * @param entityManagerFactory
     * @param persistenceCache
     */
    @Inject
    public AnnotationPersistenceServiceImpl(EntityManagerFactory entityManagerFactory,
            PersistenceCache persistenceCache) {
        super(jdbcUrl, jdbcUsername, jdbcPassword, jdbcDriver);
        this.entityManagerFactory = entityManagerFactory;
        this.persistenceCache = persistenceCache;

    }

    /**
     *
     * @param conceptname
     * @return
     */
    public boolean doesConceptNameExist(String conceptname) {

        String sql = "SELECT count(*) FROM ConceptName WHERE ConceptName = '" + conceptname + "'";

        final QueryFunction queryFunction = new QueryFunction() {

            public Object apply(ResultSet resultSet) throws SQLException {
                Integer n = 0;

                while (resultSet.next()) {
                    n = resultSet.getInt(1);
                }

                return new Boolean(n > 0);
            }
        };

        return ((Boolean) executeQueryFunction(sql, queryFunction)).booleanValue();
    }

    /**
     * Yes this duplicates functionality in {@link ConceptDAO}. But this version keeps
     * a transaction open so that the L1 cache never gets cleared. This greatly speeds
     * up lookups!!
     *
     * @param name
     * @return
     */
    public Concept findConceptByName(String name) {
        Concept concept = getReadOnlyConceptDAO().findByName(name);

        // Let's load the children and grandchildren into our transaction
        for (Concept child : concept.getChildConcepts()) {
            child.getChildConcepts();
        }

        return concept;
    }

    /**
     * Fetches the root concept from the database. Also fetches the children and grandchildren
     * of the root concept.
     * @return
     */
    public Concept findRootConcept() {
        Concept concept = getReadOnlyConceptDAO().findRoot();

        // Let's load the children and grand-children into our transaction
        for (Concept child : concept.getChildConcepts()) {
            child.getChildConcepts();
        }

        return concept;
    }

    /**
     * @return
     */
    public ConceptDAO getReadOnlyConceptDAO() {
        EntityManager entityManager = getReadOnlyEntityManager();
        return new ConceptDAOImpl(entityManager);
    }

    private EntityManager getEntityManager() {
        EntityManager entityManager = readWriteEntityManagers.get();
        if (entityManager == null || !entityManager.isOpen()) {
            entityManager = entityManagerFactory.createEntityManager();
            readWriteEntityManagers.set(entityManager);
        }

        return entityManager;
    }
    
    /**
     * These entitymanagers are never closed (until garbage collected) They are for
     * queries since their L1 cache never gets cleared. This greatly speeds up
     * queries. The entitymanger returned already has a transaction running.
     * @return
     */
    private EntityManager getReadOnlyEntityManager() {
        EntityManager entityManager = readOnlyEntityManagers.get();
        if (entityManager == null || !entityManager.isOpen()) {
            entityManager = entityManagerFactory.createEntityManager();
            DAO dao = new vars.jpa.DAO(entityManager);
            dao.startTransaction();
            persistenceCache.addCacheClearedListener(new MyCacheClearedListener(dao));
            readOnlyEntityManagers.set(entityManager);
        }

        return entityManager;
    }

    /**
     * Thread-safe. Updates changes made to the observations in the database. Validates the
     * concept names used by the {@link Observation}s and their child {@link Association}
     * @param observations
     * @return
     */
    public Collection<Observation> updateAndValidate(Collection<Observation> observations) {
        Collection<Observation> updatedObservations = new ArrayList<Observation>(observations.size());
        EntityManager entityManager = getEntityManager();
        ObservationDAO dao = new ObservationDAOImpl(entityManager);
        AssociationDAO aDao = new AssociationDAOImpl(entityManager);
        dao.startTransaction();
        for (Observation observation : observations) {
            observation = dao.merge(observation);
            updatedObservations.add(observation);
            dao.validateName(observation);
            for (Association association : new ArrayList<Association>(observation.getAssociations())) {
                aDao.validateName(association);
            }
        }
        dao.endTransaction();
        return updatedObservations;
    }
    
    
    public Collection<LinkTemplate> findLinkTemplatesFor(Concept concept) {
        LinkTemplateDAO dao = new LinkTemplateDAOImpl(getReadOnlyEntityManager());
        return dao.findAllApplicableToConcept(concept);
    }
    
    /**
     * Looks up the 'identity-reference' values for a given concept within a {@link VideoArchiveSet}
     * These are used to tag an annotation as the same creature that's been seen before.
     */
    public Collection<Integer> findAllReferenceNumbers(VideoArchiveSet videoArchiveSet, Concept concept) {
        VideoArchiveDAO dao = new VideoArchiveDAOImpl(getReadOnlyEntityManager(), null);
        Collection<Integer> referenceNumbers = new TreeSet<Integer>();
        for (VideoArchive videoArchive : new ArrayList<VideoArchive>(videoArchiveSet.getVideoArchives())) {
            // TODO identity-reference is hard coded. It should be pulled out into a properties file
            Set<String> values = dao.findAllLinkValues(videoArchive, "identity-reference", concept);
            for (String string : values) {
                try {
                    Integer v = Integer.valueOf(string);
                    referenceNumbers.add(v);
                }
                catch (Exception e) {
                    log.info("Unable to parse integer from " + string);
                }
            }
        }
        return referenceNumbers;
    }
    

    private class MyCacheClearedListener implements CacheClearedListener {

        private final DAO dao;

        /**
         * Constructs ...
         *
         * @param dao
         */
        public MyCacheClearedListener(DAO dao) {
            this.dao = dao;
        }

        /**
         *
         * @param evt
         */
        public void afterClear(CacheClearedEvent evt) {
            dao.startTransaction();
        }

        /**
         *
         * @param evt
         */
        public void beforeClear(CacheClearedEvent evt) {
            dao.endTransaction();    // Close the transaction 
        }
    }


    
}
