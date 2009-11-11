/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.jpa;

import vars.PersistenceCacheProvider;
import javax.persistence.EntityManagerFactory;

/**
 * Provides a method to clear the 2nd level cache used by Hibernate JPA. This
 * is specific to Hibernate and will need to be rewritten if using s different
 * JPA provider. In the future we can modify it to use JPA 2.0 cache API.
 *
 * @author brian
 */
public class HibernateCacheProvider implements PersistenceCacheProvider {

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

    
    public HibernateCacheProvider(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * Clear the second level cache
     */
    public void clear() {
        /*
         * http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/cache_api
         * http://weblogs.java.net/blog/archive/2009/08/21/jpa-caching
         * http://jcp.org/en/jsr/detail?id=317
         * https://forums.hibernate.org/viewtopic.php?f=1&t=958289&view=next
         * https://www.hibernate.org/hib_docs/v3/api/org/hibernate/cache/EhCache.html
         * http://bill.burkecentral.com/2007/07/06/co-existence-with-hibernate-jpa-and-ejb3/
         */

//        Session session = (Session) entityManagerFactory.createEntityManager().getDelegate();
//        SessionFactory sessionFactory = session.getSessionFactory();
//        for (Object clazz : persistentClasses) {
//            sessionFactory.evict((Class) clazz);
//        }
        
    }

}
