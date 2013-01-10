/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.query;


import com.google.inject.Binder;
import com.google.inject.Module;

import java.util.Locale;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.jpa.VarsJpaModule;


/**
 * Manages Guice dependency injection here. This looks up the guice.module
 * property in The simpa-annotation.properties file for the class name of the Guice module
 * to bind at runtime.
 *
 * @author brian
 */
public class QueryModule implements Module {

    public final Logger log = LoggerFactory.getLogger(getClass());

    private final String annotationPersistenceUnit;
    private final String knowledgebasePersistenceUnit;
    private final String miscPersistenceUnit;


    public QueryModule() {
        ResourceBundle bundle = ResourceBundle.getBundle("query-app", Locale.US);
        annotationPersistenceUnit = bundle.getString("annotation.persistence.unit");
        knowledgebasePersistenceUnit = bundle.getString("knowledgebase.persistence.unit");
        miscPersistenceUnit = bundle.getString("misc.persistence.unit");
    }


    public void configure(Binder binder) {
        binder.install(new VarsJpaModule(annotationPersistenceUnit, knowledgebasePersistenceUnit, miscPersistenceUnit));
    }

}

