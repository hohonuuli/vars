/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.services;

/**
 *
 * @author brian
 */
public class AnnotationLookupException extends VARSException {

    public AnnotationLookupException(Throwable throwable) {
        super(throwable);
    }

    public AnnotationLookupException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public AnnotationLookupException(String s) {
        super(s);
    }

    public AnnotationLookupException() {
    }

}
