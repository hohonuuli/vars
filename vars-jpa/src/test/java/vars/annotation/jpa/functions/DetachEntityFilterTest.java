package vars.annotation.jpa.functions;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.AnnotationFactory;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveSet;
import vars.annotation.VideoArchiveSetDAO;
import vars.jpa.VarsJpaTestModule;
import vars.testing.AnnotationTestObjectFactory;

import javax.persistence.EntityManager;
import java.util.function.Function;

/**
 * @author Brian Schlining
 * @since 2015-09-09T15:24:00
 */
public class DetachEntityFilterTest {

    public final Logger log = LoggerFactory.getLogger(getClass());
    AnnotationFactory annotationFactory;
    AnnotationTestObjectFactory testObjectFactory;
    AnnotationDAOFactory daoFactory;

    @Before
    public void setup() {
        Injector injector = Guice.createInjector(new VarsJpaTestModule());
        annotationFactory = injector.getInstance(AnnotationFactory.class);
        testObjectFactory = new AnnotationTestObjectFactory(annotationFactory);
        daoFactory = injector.getInstance(AnnotationDAOFactory.class);
    }

    @Test
    public void testDetachObservation() {
        String platformName = "XYZ__";
        VideoArchiveSet vas = testObjectFactory.makeObjectGraph("BIG-TEST", 2);
        VideoArchiveSetDAO dao = daoFactory.newVideoArchiveSetDAO();
        EntityManager em = dao.getEntityManager();
        dao.startTransaction();
        dao.persist(vas);
        dao.endTransaction();

        // Can't close entitymanager as it will shutdown the database
        Function<VideoArchiveSet, VideoArchiveSet> detachFn = new DetachEntityFn<>(em);
        VideoArchiveSet detachedVas = detachFn.apply(vas);
        assertTrue("Failed to detach VideoArchiveSet", !em.contains(detachedVas));
        assertTrue("Where did the VideoArchives go?", !detachedVas.getVideoArchives().isEmpty());
        for (VideoArchive va : detachedVas.getVideoArchives()) {
            assertTrue("Failed to detach VideoArchive", !em.contains(va));
        }

        // Change name
        detachedVas.setPlatformName(platformName);

        // Make sure it didn't change in database
        VideoArchiveSet storedVas = dao.findByPrimaryKey(vas.getPrimaryKey());
        assertNotEquals(storedVas.getPlatformName(), platformName);

    }
}
