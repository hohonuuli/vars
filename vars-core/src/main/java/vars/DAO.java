/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars;

import java.util.List;
import java.util.Map;

/**
 *
 * @author brian
 */
/**
 *
 * @author brian
 */
public interface DAO {

    /**
     * TODO: Add JavaDoc
     *
     * @param object
     */
    <T> T persist(T object);

    /**
     * TODO: Add JavaDoc
     *
     * @param object
     */
    <T> T remove(T object);

    <T> T merge(T object);


    <T> T findByPrimaryKey(Class<T> clazz, Object primaryKey);

    /**
     * Compares 2 object tho see if they represent the same entity in the database.
     * Normally, this would return true if both objects have the same primary key.
     * 
     * @param obj1
     * @param obj2
     * @return
     */
    boolean equalInDatastore(Object obj1, Object obj2);

    /**
     * Looks up the object in the data store and retrieves the latest and greatest
     * copy of it.
     *
     * @param object The object to lookup
     * @return The object retrived from the datastore
     */
    <T> T findInDatastore(T object);

    /**
     * Many one-to-many relations are lazy loaded in JPA. For convience, this
     * method will load all lazy relations of an IEntity object. This method has
     * no effect on objects that are not persistant
     *
     * @param entity
     *            The persistent object whos children will be loaded from the
     *            database.
     */
    <T> T loadLazyRelations(T entity);

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
    List findByNamedQuery(String name, Map<String, Object> namedParameters);


    void startTransaction();
    void endTransaction();


}
