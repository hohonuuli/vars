/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars;

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
    <T> T makePersistent(T object);

    /**
     * TODO: Add JavaDoc
     *
     * @param object
     */
    <T> T makeTransient(T object);

    <T> T update(T object);


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

}
