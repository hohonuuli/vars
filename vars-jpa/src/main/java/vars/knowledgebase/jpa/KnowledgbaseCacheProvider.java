/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.knowledgebase.jpa;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import javax.persistence.Persistence;
import vars.jpa.HibernateCacheProvider;

/**
 *
 * @author brian
 */
public class KnowledgbaseCacheProvider extends HibernateCacheProvider {

    @Inject
    public KnowledgbaseCacheProvider(@Named("knowledgebasePersistenceUnit") String persistenceUnit) {
        super(Persistence.createEntityManagerFactory(persistenceUnit));
    }

}
