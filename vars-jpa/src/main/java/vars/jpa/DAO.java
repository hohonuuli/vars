/*
 * @(#)DAO.java   2010.02.17 at 03:00:10 PST
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



package vars.jpa;

import com.google.inject.Inject;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Persistence service implementation for use in Java SE environments. Basically,
 * it's a decorator for EntityManager
 * Use as:
 * <code>
 * DAO dao = new DAO(entityManager);
 * dao.startTransaction();
 * // Call whatever dao methods you need
 * dao.endTransaction();
 * </code>
 */
public class DAO implements vars.DAO, EntityManagerAspect {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    private final EntityManager entityManager;
    private final String JDBC_URL_KEY = "javax.persistence.jdbc.url";

    /**
     *
     */
    public enum TransactionType {
        REMOVE, FIND, PERSIST, LOAD_LAZY_RELATIONS,    // <-- Load those lazy relationships
        MERGE
    }

    /**
     * Constructs ...
     *
     * @param entityManager
     */
    @Inject
    public DAO(final EntityManager entityManager) {
        this.entityManager = entityManager;
        if (log.isInfoEnabled() && entityManager != null) {
            Map<String, Object> m = entityManager.getProperties();
            if (m.containsKey(JDBC_URL_KEY)) {
                log.info("Connecting to {}", m.get(JDBC_URL_KEY));
            }
        }
    }

    /**
     * Closes the associated entityManger if it's open.
     */
    public void close() {
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }

    /**
     */
    public void commit() {
        if (entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().commit();
        }
    }

    /**
     * Close any open transaction used on the current thread.
     */
    public void endTransaction() {
        EntityTransaction entityTransaction = entityManager.getTransaction();
        if (entityTransaction.isActive()) {
            try {
                if (entityTransaction.getRollbackOnly()) {
                    entityTransaction.rollback();
                }
                else {
                    entityTransaction.commit();
                }
            }
            catch (PersistenceException e) {
                if (entityTransaction.isActive()) {
                    entityTransaction.rollback();
                }

                throw e;
            }
            finally {
                log.debug("JPA Transaction Ended");
            }
        }
    }

    /**
     *
     * @param thisObj
     * @param thatObj
     * @return
     */
    public boolean equalInDatastore(Object thisObj, Object thatObj) {
        final JPAEntity thisEntity = (JPAEntity) thisObj;
        final JPAEntity thatEntity = (JPAEntity) thatObj;
        return thisEntity.getId().equals(thatEntity.getId());
    }

    /**
     * Retrieves the object from the datastore. This ignores all state changes
     * in the provided object and returns the copy as found in the data store.
     *
     * @param object
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T find(T object) {
        log.debug("Executing FIND on {}", object);
        final JPAEntity jpaEntity = (JPAEntity) object;
        return (T) entityManager.find(jpaEntity.getClass(), jpaEntity.getId());
    }

    /**
    * Executes a named query using a map of named parameters
    *
    * @param name
    *            The name of the query to execute
    * @param namedParameters
    *            A Map<String, Object> of the 'named' parameters to assign in
    *            the query
    * @return A list of objects returned by the query.
    */
    public List findByNamedQuery(String name, Map<String, Object> namedParameters) {
        if (log.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder("Executing FIND using named query '");
            sb.append(name).append("'");

            if (namedParameters.size() > 0) {
                sb.append(" with parameters:\n");

                for (String string : namedParameters.keySet()) {
                    sb.append("\t").append(string).append(" = ").append(namedParameters.get(string));
                }
            }

            log.debug(sb.toString());
        }

        List resultList = null;
        Query query = entityManager.createNamedQuery(name);
        for (String key : namedParameters.keySet()) {
            query.setParameter(key, namedParameters.get(key));
        }

        resultList = query.getResultList();

        return resultList;
    }

    /**
     * Executes a named query that does not take any parameters
     * 
     * @param name The name of the JPL query
     * @return A list of objects returned by the query
     */
    public List findByNamedQuery(String name) {
        Map<String, Object> params = new HashMap<String, Object>();
        return findByNamedQuery(name, params);
    }

    /**
     *
     * @param clazz
     * @param primaryKey
     * @param <T>
     * @return
     */
    public <T> T findByPrimaryKey(Class<T> clazz, Object primaryKey) {
        T value = null;
        log.debug("Executing FIND for {} using primary key = {}", clazz, primaryKey);

        value = entityManager.find(clazz, primaryKey);

        return value;
    }

    /**
     * @return
     */
    public EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Checks to see if the given object is persisted in the database
     * @param entity The object of interest
     * @return true if it's in the database. False if it is not
     */
    public boolean isPersistent(Object entity) {
        boolean hasNullPK = (entity instanceof JPAEntity) ? ((JPAEntity) entity).getId() == null : false;
        return (hasNullPK) ? false : find(entity) != null;
    }

    /**
     * Many one-to-many relations are lazy loaded in JPA. For convience, this
     * method will load all lazy relations of an IEntity object. This method has
     * no effect on objects that are not persistant
     *
     * @param entity
     *            The persistent object whos children will be loaded from the
     *            database.
     */
    public void loadLazyRelations(Object entity) {

        log.debug("Executing {} on {}", TransactionType.LOAD_LAZY_RELATIONS, entity);

        try {
            Method method = entity.getClass().getMethod("invokeLazyGetters", new Class[0]);
            method.invoke(entity, new Object[0]);
        }
        catch (NoSuchMethodException e) {
            log.warn("Attempted to invoke 'invokeLazyGetters' method on " + entity + " but no such method exists", e);
        }
        catch (Exception e) {
            log.error("Failed to call 'invokeLazyGetters on " + entity, e);
        }
    }

    /**
     * Update an object in the database and bring it into the current transaction
     *
     * @param entity
     *            The entity object whos fields are being updated in the
     *            database.
     * @param <T>
     * @return
     */
    public <T> T merge(T entity) {
        log.debug("Executing {} on {}", TransactionType.MERGE, entity);
        return entityManager.merge(entity);
    }

    /**
     * Insert an object into the database
     *
     * @param entity
     *            The entity object to persist in the database
     */
    public void persist(Object entity) {
        log.debug("Executing {} on {}", TransactionType.PERSIST, entity);
        entityManager.persist(entity);
    }

    /**
     *
     * @param entity
     */
    public void remove(Object entity) {
        log.debug("Executing {} on {}", TransactionType.REMOVE, entity);
        entityManager.remove(entity);
    }

    /**
     * Start a database transaction. Also initializes the EntityManager and
     * returns an instance of it.
     *
     */
    public void startTransaction() {
        EntityTransaction entityTransaction = entityManager.getTransaction();
        if (!entityTransaction.isActive()) {
            entityTransaction.begin();
            log.debug("JPA Transaction Started");
        }
    }

    /**
     * The findByNamedQuery method needs a Map of parameters. This method
     * generates the map for you. For example, instead of using:
     * {@code
     *  Map<String, Object> map = new HashMap<String, Object>();
     *  map.put("groupId", "vaap.annotation");
     *  map.put("artifactId", "VARS-Histogram");
     * }
     * You could use
     * {@code
     *  Map<String, Object> map = toParameterMap("groupId", "vaap-annotation", "artifactId", "VARS-Histogram")
     *
     * }
     * @param args An even numbered set of args. The first value in each pair is the parameter name
     *      as a string, the 2nd value is the parameter value
     * @return A Map containing the key-value pairs
     */
    public static Map<String, Object> toParameterMap(Object... args) {
        Map<String, Object> params = new HashMap<String, Object>();
        for (int i = 0; i < args.length; i += 2) {
            params.put((String) args[i], args[i + 1]);
        }
        return params;
    }
}
