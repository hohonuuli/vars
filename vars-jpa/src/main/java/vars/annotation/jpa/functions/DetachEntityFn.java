package vars.annotation.jpa.functions;

import vars.jpa.EntityManagerAspect;

import javax.persistence.EntityManager;
import java.util.function.Function;

/**
 * Filter that detaches a JPA entity, and all associated objects, from the current EntityManager.
 * This allows us to make changes to a data set for data processing without actually changing
 * the values stored in the database.
 *
 * @author Brian Schlining
 * @since 2015-09-09T15:19:00
 */
public class DetachEntityFn<T> implements EntityManagerAspect, Function<T, T> {

    private final EntityManager entityManager;

    public DetachEntityFn(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public T apply(T t) {
        entityManager.detach(t);
        return t;
    }
}
