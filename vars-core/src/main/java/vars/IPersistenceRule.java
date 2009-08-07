package vars;

/**
 * interface for defining rules that can used pre and post persistence
 */
public interface IPersistenceRule<T extends IVARSObject> {

    /**
     * Applies the rule to the object
     * @param object THe object of interest
     * @return
     */
    T apply(T object);

}
