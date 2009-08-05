/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.services;

/**
 *
 * @author brian
 */
public class KnowledgebaseLookupException extends VARSException {

    public KnowledgebaseLookupException(Throwable throwable) {
        super(throwable);
    }

    public KnowledgebaseLookupException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public KnowledgebaseLookupException(String s) {
        super(s);
    }

    public KnowledgebaseLookupException() {
    }

}
