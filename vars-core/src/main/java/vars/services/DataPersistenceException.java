/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.services;

/**
 * Thrown when something bad happens in the DataPersistenceService
 * @author brian
 */
public class DataPersistenceException extends VARSException {

    public DataPersistenceException(Throwable throwable) {
        super(throwable);
    }

    public DataPersistenceException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public DataPersistenceException(String s) {
        super(s);
    }

    public DataPersistenceException() {
    }

}
