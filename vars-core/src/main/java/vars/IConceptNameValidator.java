package vars;

/**
 * Tag interface for DAO objects that can validate concept names used by
 * the entity objects. This is to ensure that they are persisted using a
 * primary name.
 */
public interface IConceptNameValidator<T> {

    void validateName(T object);

}
