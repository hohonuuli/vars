package vars.jpa;

import com.google.inject.Inject;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Persistence service implementation for use in Java SE environments.
 * Use as:
 * <code>
 * DAO dao = new DAO(entityManager);
 * dao.startTransaction();
 * // Call whatever dao methods you need
 * dao.endTransaction();
 * </code>
 */
public class DAO implements vars.DAO, EntityManagerAspect {

    private final EntityManager entityManager;
    protected final Logger log = LoggerFactory.getLogger(getClass());

    public enum TransactionType {
        REMOVE,
        FIND,
        PERSIST,
        LOAD_LAZY_RELATIONS, // <-- Load those lazy relationships
        MERGE
    }

    @Inject
    public DAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void commit() {
        if (entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().commit();
        }
    }

    private <T> T processTransaction(T entity, TransactionType transactionType) {
        if (log.isDebugEnabled()) {
            log.debug("Executing " + transactionType + " on " + entity);
        }
        try {
            switch (transactionType) {
                case REMOVE:
                    if (!entityManager.contains(entity)) {
                        entity = entityManager.merge(entity);
                    }
                    entityManager.remove(entity);
                    break;
                case MERGE:
                    /*
                     * If this fails due to versioning (i.e. optimistic locking) a
                     * javax.persistence.OptimisticLockException will be thrown.
                     */
                    entity = entityManager.merge(entity);
                    break;
                case PERSIST:
                    entityManager.persist(entity);
                    break;
                case LOAD_LAZY_RELATIONS:
                    if (!entityManager.contains(entity)) {
                        entity = entityManager.merge(entity);
                    }
                    try {
                        Method method = entity.getClass().getMethod("invokeLazyGetters", new Class[0]);
                        method.invoke(entity, new Object[0]);
                    }
                    catch (NoSuchMethodException e) {
                        log.warn("Attempted to invoke 'invokeLazyGetters' method on " +
                                entity + " but no such method exists", e);
                    }
                    catch (Exception e) {
                        log.error("Failed to call 'invokeLazyGetters on " + entity, e);
                    }
                    break;
                /*
                 * I tried to implement refresh but it actually updates the object
                 * instead. case REFRESH:
                 * entityManager.refresh(entityManager.merge(entity)); break;
                 */
                default:
            }
        }
        catch (PersistenceException e) {
            log.error("An error occurred while executing a JPA transaction", e);
            throw e;
        }
        return entity;
    }

    /**
     * Update an object in the database and bring it into the current transaction
     *
     * @param entity
     *            The entity object whos fields are being updated in the
     *            database.
     */
    public <T> T merge(T entity) {
        return processTransaction(entity, TransactionType.MERGE);
    }

    /**
     * Insert an object into the database
     *
     * @param entity
     *            The entity object to persist in the database
     */
    public <T> T persist(T entity) {
        return processTransaction(entity, TransactionType.PERSIST);
    }



    public <T> T remove(T object) {
        return processTransaction(object, TransactionType.REMOVE);
    }


    public <T> T findByPrimaryKey(Class<T> clazz, Object primaryKey) {
        T value = null;
        if (log.isDebugEnabled()) {
            log.debug("Executing FIND for " + clazz + " using primary key = " + primaryKey);
        }
        value = entityManager.find(clazz, primaryKey);

        return value;
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
    public <T> T loadLazyRelations(T entity) {
        return processTransaction(entity, TransactionType.LOAD_LAZY_RELATIONS);
    }

     /**
     * Executes a named query using a map of named parameters
     *
     * @param name
     *            The name of the query to execute
     * @param namedParameters
     *            A Map<String, Object> of the 'named' parameters to assign in
     *            the query
     * @param endTransaction if true the transaction wll be ended when the method exits. If
     *     false then the transaction will be kept open and can be reused by the current thread.
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

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public boolean equalInDatastore(Object obj1, Object obj2) {
        boolean isSame = false;
        if ((obj1 != null) && (obj2 != null) && (obj1.getClass().equals(obj2.getClass()))) {
            isSame = ((JPAEntity) obj1).getId().equals(((JPAEntity) obj2).getId());
        }
        return isSame;
    }

    public <T> T findInDatastore(T object) {
        return (T) findByPrimaryKey(object.getClass(), ((JPAEntity) object).getId());
    }

    /**
     * Start a database transaction. Also initializes the EntityManager and
     * returns an instance of it.
     *
     * @return An EntityManager for interacting with the database. DO NOT close
     *         this as it may be shared by other methods on the same thread. If
     *         you need to force commit your changes to the database use
     *         <i>entityManager.flush()</i> <b>NOT</b>
     *         <i>entityManager.close()</i>
     */
    public void startTransaction() {
            EntityTransaction entityTransaction = entityManager.getTransaction();
            if (!entityTransaction.isActive()) {
                entityTransaction.begin();
                log.debug("JPA Transaction Started");
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

    @Override
    protected void finalize() throws Throwable {
        if (entityManager != null && entityManager.isOpen()) {
            if (log.isDebugEnabled()) {
                log.debug("Closing " + entityManager);
            }
            entityManager.close();
        }
        super.finalize();
    }

}




