package vars.annotation.jpa;

import vars.testing.AnnotationTestObjectFactory;
import vars.annotation.AnnotationFactory;
import vars.annotation.IVideoArchiveSet;
import vars.annotation.IVideoArchiveSetDAO;
import vars.annotation.AnnotationDAOFactory;
import vars.jpa.VarsJpaTestModule;
import vars.jpa.EntityUtilities;
import com.google.inject.Injector;
import com.google.inject.Guice;
import org.junit.Test;
import org.mbari.jpax.NonManagedEAOImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 11, 2009
 * Time: 11:06:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class CrudTest {

    public final Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void bigTest() {
        Injector injector = Guice.createInjector(new VarsJpaTestModule());
        AnnotationFactory af = injector.getInstance(AnnotationFactory.class);
        AnnotationTestObjectFactory factory = new AnnotationTestObjectFactory(af);
        IVideoArchiveSet vas = factory.makeObjectGraph("BIG-TEST", 3);


        AnnotationDAOFactory adf = injector.getInstance(AnnotationDAOFactory.class);
        IVideoArchiveSetDAO dao = adf.newVideoArchiveSetDAO();

        EntityUtilities eu = new EntityUtilities(new NonManagedEAOImpl("test"));
        log.info("\n" + eu.buildTextTree(vas));

        dao.makePersistent(vas);
        
    }

    
}
