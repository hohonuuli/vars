/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.services;

/**
 *
 * @author brian
 */
/**
 *
 * @author brian
 */
public interface VARSPersistenceService {

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
}
