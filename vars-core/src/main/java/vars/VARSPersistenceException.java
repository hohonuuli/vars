/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars;

import vars.VARSException;

/**
 * Thrown when something bad happens in the DataPersistenceService
 * @author brian
 */
public class VARSPersistenceException extends VARSException {

    public VARSPersistenceException(Throwable throwable) {
        super(throwable);
    }

    public VARSPersistenceException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public VARSPersistenceException(String s) {
        super(s);
    }

    public VARSPersistenceException() {
    }

}
