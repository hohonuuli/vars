/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.knowledgebase.jpa;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import javax.persistence.Persistence;
import vars.jpa.JPACacheProvider;

/**
 *
 * @author brian
 */
public class KnowledgbaseCacheProvider extends JPACacheProvider {

    @Inject
    public KnowledgbaseCacheProvider(@Named("knowledgebasePersistenceUnit") String persistenceUnit) {
        super(Persistence.createEntityManagerFactory(persistenceUnit));
    }

}
