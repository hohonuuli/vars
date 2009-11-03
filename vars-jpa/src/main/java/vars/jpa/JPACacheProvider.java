/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.jpa;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import vars.PersistenceCacheProvider;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.util.Set;
import javax.persistence.EntityManagerFactory;
import vars.annotation.jpa.GAssociation;
import vars.annotation.jpa.GCameraData;
import vars.annotation.jpa.GCameraDeployment;
import vars.annotation.jpa.GObservation;
import vars.annotation.jpa.GPhysicalData;
import vars.annotation.jpa.GVideoArchive;
import vars.annotation.jpa.GVideoArchiveSet;
import vars.annotation.jpa.GVideoFrame;
import vars.knowledgebase.jpa.ConceptImpl;
import vars.knowledgebase.jpa.ConceptMetadataImpl;
import vars.knowledgebase.jpa.GConceptName;
import vars.knowledgebase.jpa.GHistory;
import vars.knowledgebase.jpa.GLinkRealization;
import vars.knowledgebase.jpa.GLinkTemplate;
import vars.knowledgebase.jpa.GMedia;
import vars.knowledgebase.jpa.GUsage;

/**
 * Provides a method to clear the 2nd level cache used by Hibernate JPA. This
 * is specific to Hibernate and will need to be rewritten if using s different
 * JPA provider. In the future we can modify it to use JPA 2.0 cache API.
 *
 * @author brian
 */
public class JPACacheProvider implements PersistenceCacheProvider {

    private final EntityManagerFactory entityManagerFactory;

    private final Set<?> persistentClasses = ImmutableSet.of(
            GUserAccount.class,
            GAssociation.class,
            GCameraData.class,
            GCameraDeployment.class,
            GObservation.class,
            GPhysicalData.class,
            GVideoArchive.class,
            GVideoArchiveSet.class,
            GVideoFrame.class,
            ConceptImpl.class,
            ConceptMetadataImpl.class,
            GConceptName.class,
            GHistory.class,
            GLinkRealization.class,
            GLinkTemplate.class,
            GMedia.class,
            GUsage.class);

    @Inject
    public JPACacheProvider(@Named("miscEAO") EntityManagerFactory entityManagerFactory) {
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

        Session session = (Session) entityManagerFactory.createEntityManager().getDelegate();
        SessionFactory sessionFactory = session.getSessionFactory();
        for (Object clazz : persistentClasses) {
            sessionFactory.evict((Class) clazz);
        }
        
    }

}
