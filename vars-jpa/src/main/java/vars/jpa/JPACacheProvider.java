/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.jpa;

import vars.PersistenceCacheProvider;

import javax.persistence.Cache;
import javax.persistence.EntityManagerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.VARSObject;
import vars.annotation.AnnotationObject;
import vars.knowledgebase.KnowledgebaseObject;

/**
 * Provides a method to clear the 2nd level cache used by JPA.
 *
 * @author brian
 */
public class JPACacheProvider implements PersistenceCacheProvider {

    private final EntityManagerFactory kbEmf;
    private final EntityManagerFactory annoEmf;
    private final EntityManagerFactory miscEmf;
    private final Logger log = LoggerFactory.getLogger(getClass());



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
        
        Cache cache = kbEmf.getCache();
        cache.evictAll();

        cache = annoEmf.getCache();
        cache.evictAll();

        cache = miscEmf.getCache();
        cache.evictAll();
        
    }

    public void evict(AnnotationObject entity) {
        evict(annoEmf.getCache(), entity);
    }

    public void evict(KnowledgebaseObject entity) {
        evict(kbEmf.getCache(), entity);
    }

    private void evict(Cache cache, VARSObject entity) {
        try {
            cache.evict(entity.getClass(), entity.getPrimaryKey());
        }
        catch (Exception e) {
            log.info("Failed to evict " + entity + " from cache", e);
        }
    }

}
