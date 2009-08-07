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
public interface IDAO {

    /**
     * TODO: Add JavaDoc
     *
     * @param object
     */
    public <T> T makePersistent(T object);

    /**
     * TODO: Add JavaDoc
     *
     * @param object
     */
    public <T> T makeTransient(T object);


    public <T> T findByPrimaryKey(Class<T> clazz, Object primaryKey);

}
