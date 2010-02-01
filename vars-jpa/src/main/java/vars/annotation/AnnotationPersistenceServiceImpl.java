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

import org.mbari.sql.QueryableImpl;
import org.mbari.sql.QueryFunction;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.mbari.text.IgnoreCaseToStringComparator;
import vars.*;
import vars.annotation.jpa.VideoArchiveDAOImpl;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptName;
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
    private final ThreadLocal<EntityManager> readOnlyEntityManagers = new ThreadLocal<EntityManager>();
    private final EntityManagerFactory kbEntityManagerFactory;
    private final AnnotationDAOFactory annotationDAOFactory;
    private final PersistenceCache persistenceCache;

    private final Map<Concept, List<String>> descendantNameCache = Collections.synchronizedMap(new HashMap<Concept, List<String>>());
    /**
     * Constructs ...
     *
     * @param kbEntityManagerFactory
     * @param persistenceCache
     */
    @Inject
    public AnnotationPersistenceServiceImpl(AnnotationDAOFactory annotationDAOFactory,
            @Named("knowledgebasePersistenceUnit") EntityManagerFactory kbEntityManagerFactory,
            PersistenceCache persistenceCache) {
        super(jdbcUrl, jdbcUsername, jdbcPassword, jdbcDriver);
        this.annotationDAOFactory = annotationDAOFactory;
        this.kbEntityManagerFactory = kbEntityManagerFactory;
        this.persistenceCache = persistenceCache;

    }

    public List<String> findDescendantNamesFor(Concept concept) {
        List<String> desendantNames = descendantNameCache.get(concept);
        if (desendantNames == null && concept != null) {
            Collection<ConceptName> names = getReadOnlyConceptDAO().findDescendentNames(concept);
            Collection<String> namesAsStrings = Collections2.transform(names, new Function<ConceptName, String>() {
                public String apply(ConceptName from) {
                    return from.getName();
                }
            });
            desendantNames = new ArrayList<String>(namesAsStrings);
            Collections.sort(desendantNames, new IgnoreCaseToStringComparator());
            descendantNameCache.put(concept, desendantNames);
        }

        // Don't return null. Alwasy return at least an empty list
        if (desendantNames == null) {
            desendantNames = new ArrayList<String>();
        }
        
        return desendantNames;
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

    
    /**
     * These entitymanagers are never closed (until garbage collected) They are for
     * queries since their L1 cache never gets cleared. This greatly speeds up
     * queries. The entitymanger returned already has a transaction running.
     * @return
     */
    private EntityManager getReadOnlyEntityManager() {
        EntityManager entityManager = readOnlyEntityManagers.get();
        if (entityManager == null || !entityManager.isOpen()) {
            entityManager = kbEntityManagerFactory.createEntityManager();
            DAO dao = new vars.jpa.DAO(entityManager);
            dao.startTransaction();
            persistenceCache.addCacheClearedListener(new MyCacheClearedListener(dao));
            readOnlyEntityManagers.set(entityManager);
        }

        return entityManager;
    }
    
    public Collection<LinkTemplate> findLinkTemplatesFor(Concept concept) {
        LinkTemplateDAO dao = new LinkTemplateDAOImpl(getReadOnlyEntityManager());
        concept = dao.find(concept);
        Collection<LinkTemplate> linkTemplates = dao.findAllApplicableToConcept(concept);
        return linkTemplates;
    }
    
    /**
     * Looks up the 'identity-reference' values for a given concept within a {@link VideoArchiveSet}
     * These are used to tag an annotation as the same creature that's been seen before.
     */
    public Collection<Integer> findAllReferenceNumbers(VideoArchiveSet videoArchiveSet, Concept concept) {
        VideoArchiveDAO dao = annotationDAOFactory.newVideoArchiveDAO();
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

    public List<String> findAllVideoArchiveNames() {
        String sql = "SELECT VideoArchiveName FROM VideoArchive ORDER BY VideoArchiveName";
        QueryFunction<List<String>> queryFunction = new QueryFunction<List<String>>() {
            public List<String> apply(ResultSet resultSet) throws SQLException {
                List<String> names = new ArrayList<String>();
                while (resultSet.next()) {
                    names.add(resultSet.getString(1));
                }
                return names;
            }
        };

        return executeQueryFunction(sql, queryFunction);
    }

    /**
     * Updates all {@link Observation}s, {@link Association}s, and {@link LinkTemplate}s
     * in the database so that any that use a non-primary name for the given
     * concept are changed so that they use the primary name.
     *
     * @param concept
     */
    public void updateConceptNameUsedByAnnotations(Concept concept) {

        String primaryName = concept.getPrimaryConceptName().getName();

        /*
         * Update the Observation table
         */
        Collection<ConceptName> conceptNames = new ArrayList<ConceptName>(concept.getConceptNames());
        conceptNames.remove(concept.getPrimaryConceptName());

        for (ConceptName conceptName : conceptNames) {

            // Update Observations
            String sql = "UPDATE Observation SET ConceptName = '" +
                primaryName + "' WHERE ConceptName = '" +
                conceptName.getName() + "'";
            executeUpdate(sql);

            // Update Associations
            sql = "UPDATE Association SET ToConcept = '" +
                primaryName + "' WHERE ToConcept = '" +
                conceptName.getName() + "'";
            executeUpdate(sql);

        }

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
            descendantNameCache.clear();
        }
    }


    
}
