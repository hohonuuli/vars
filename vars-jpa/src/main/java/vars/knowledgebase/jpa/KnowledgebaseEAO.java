package vars.knowledgebase.jpa;

import org.mbari.jpaxx.NonManagedEAO;
import org.mbari.jpaxx.NonManagedEAOImpl;
import com.google.inject.name.Named;
import com.google.inject.Inject;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 10, 2009
 * Time: 4:05:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class KnowledgebaseEAO implements NonManagedEAO {

    private final NonManagedEAO eao;

    @Inject
    public KnowledgebaseEAO(@Named("knowledgebasePersistenceUnit") String persistenceUnit) {
        this.eao = new NonManagedEAOImpl(persistenceUnit);
    }

    public void insert(Object entity, boolean endTransaction) {
        eao.insert(entity, endTransaction);
    }

    public <T> T update(T entity, boolean endTransaction) {
        return eao.update(entity, endTransaction);
    }

    public <T> T delete(T entity, boolean endTransaction) {
        return eao.delete(entity, endTransaction);
    }

    public <T> T insertOrUpdate(T entity, boolean endTransaction) {
        return eao.insertOrUpdate(entity, endTransaction);
    }

    public <T> T find(Class<T> clazz, Object primaryKey, boolean endTransaction) {
        return eao.find(clazz, primaryKey, endTransaction);
    }

    public List findByNamedQuery(String name, Map<String, Object> namedParameters, boolean endTransaction) {
        return eao.findByNamedQuery(name, namedParameters, endTransaction);
    }

    public <T> T loadLazyRelations(T entity) {
        return eao.loadLazyRelations(entity);
    }

    public <T> T loadLazyRelations(T entity, boolean endTransaction) {
        return eao.loadLazyRelations(entity, endTransaction);
    }

    public EntityManager startTransaction() {
        return eao.startTransaction();
    }

    public void endTransaction() {
        eao.endTransaction();
    }

    public void insert(Object entity) {
        eao.insert(entity);
    }

    public <T> T update(T entity) {
        return eao.update(entity);
    }

    public <T> T delete(T entity) {
        return eao.delete(entity);
    }

    public <T> T insertOrUpdate(T entity) {
        return eao.insertOrUpdate(entity);
    }

    public <T> T find(Class<T> clazz, Object primaryKey) {
        return eao.find(clazz, primaryKey);
    }

    public List findByNamedQuery(String name, Map<String, Object> namedParameters) {
        return eao.findByNamedQuery(name, namedParameters);
    }

    public EntityManager createEntityManager() {
        return eao.createEntityManager();
    }

    public boolean isManaged() {
        return eao.isManaged();
    }
}
