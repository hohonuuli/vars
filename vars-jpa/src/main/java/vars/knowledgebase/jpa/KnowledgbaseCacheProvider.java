/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.knowledgebase.jpa;

import javax.persistence.EntityManagerFactory;

import vars.jpa.JPACacheProvider;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 *
 * @author brian
 */
public class KnowledgbaseCacheProvider extends JPACacheProvider {

    @Inject
    public KnowledgbaseCacheProvider(@Named("knowledgebasePersistenceUnit") EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }

}
