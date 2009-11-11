/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.jpa;

import vars.PersistenceCacheProvider;

import javax.persistence.Cache;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;

/**
 * Provides a method to clear the 2nd level cache used by Hibernate JPA. This
 * is specific to Hibernate and will need to be rewritten if using s different
 * JPA provider. In the future we can modify it to use JPA 2.0 cache API.
 *
 * @author brian
 */
public class JPACacheProvider implements PersistenceCacheProvider {

    private final EntityManagerFactory entityManagerFactory;

//    private final Set<?> persistentClasses = ImmutableSet.of(
//            GUserAccount.class,
//            GAssociation.class,
//            GCameraData.class,
//            GCameraDeployment.class,
//            GObservation.class,
//            GPhysicalData.class,
//            GVideoArchive.class,
//            GVideoArchiveSet.class,
//            GVideoFrame.class,
//            ConceptImpl.class,
//            ConceptMetadataImpl.class,
//            GConceptName.class,
//            GHistory.class,
//            GLinkRealization.class,
//            LinkTemplateImpl.class,
//            GMedia.class,
//            GUsage.class);

    
    public JPACacheProvider(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * Clear the second level cache
     */
    public void clear() {
        
        // TODO Hack until JPA 2.0 is finalized
        EntityManagerFactoryImpl emf = (EntityManagerFactoryImpl) entityManagerFactory;
        Cache cache = emf.getCache();
        cache.evictAll();
        
    }

}
