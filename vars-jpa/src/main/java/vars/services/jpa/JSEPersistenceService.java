package vars.services.jpa;

import vars.services.VARSPersistenceService;
import vars.services.VARSPersistenceException;
import vars.IVARSObject;
import vars.jpa.JPAEntity;
import org.mbari.jpax.EAO;

/**
 * Persistence service implementation for use in Java SE environments
 */
public class JSEPersistenceService implements VARSPersistenceService {

    private final EAO eao;

    public JSEPersistenceService(EAO eao) {
        this.eao = eao;
    }

    public <T> T makePersistent(T object) {
        if (object instanceof JPAEntity) {
            if (((JPAEntity) object).getId() != null) {
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
}
