/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.jpa;

import vars.PersistenceCacheProvider;

import javax.persistence.Cache;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Provides a method to clear the 2nd level cache used by Hibernate JPA. This
 * is specific to Hibernate and will need to be rewritten if using s different
 * JPA provider. In the future we can modify it to use JPA 2.0 cache API.
 *
 * @author brian
 */
public class JPACacheProvider implements PersistenceCacheProvider {

    private final EntityManagerFactory kbEmf;
    private final EntityManagerFactory annoEmf;
    private final EntityManagerFactory miscEmf;



    @Inject
    public JPACacheProvider(@Named("annotationPersistenceUnit") EntityManagerFactory annoEmf,
            @Named("knowledgebasePersistenceUnit") EntityManagerFactory kbEmf,
            @Named("miscPersistenceUnit") EntityManagerFactory miscEmf) {

        this.kbEmf = kbEmf;
        this.annoEmf = annoEmf;
        this.miscEmf = miscEmf;
    }

    /**
     * Clear the second level cache
     */
    public void clear() {
        
        // TODO Hack until JPA 2.0 is finalized
        EntityManagerFactoryImpl emf = (EntityManagerFactoryImpl) kbEmf;
        Cache cache = emf.getCache();
        cache.evictAll();

        emf = (EntityManagerFactoryImpl) annoEmf;
        cache = emf.getCache();
        cache.evictAll();

        emf = (EntityManagerFactoryImpl) miscEmf;
        cache = emf.getCache();
        cache.evictAll();
        
    }

}
