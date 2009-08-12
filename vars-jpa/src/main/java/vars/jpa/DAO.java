package vars.jpa;

import vars.IDAO;
import vars.VARSPersistenceException;
import org.mbari.jpax.EAO;
import com.google.inject.Inject;

/**
 * Persistence service implementation for use in Java SE environments
 */
public class DAO implements IDAO {

    private final EAO eao;

    @Inject
    public DAO(EAO eao) {
        this.eao = eao;
    }

    public <T> T makePersistent(T object) {
        if (object instanceof JPAEntity) {
            if (((JPAEntity) object).getId() == null) {
                eao.insert(object);
            }
            else {
                object = eao.update(object);
            }
            return object;
        }
        else {
            throw new VARSPersistenceException(object + " is not an instance of IVARSObject");
        }
    }

    public <T> T makeTransient(T object) {
        if (object instanceof JPAEntity) {
            return eao.delete(object);
        }
        else {
            throw new VARSPersistenceException(object + " is not an instance of IVARSObject");
        }
    }

    public <T> T findByPrimaryKey(Class<T> clazz, Object primaryKey) {
        return eao.find(clazz, primaryKey);
    }

    protected EAO getEAO() {
        return eao;
    }
}
