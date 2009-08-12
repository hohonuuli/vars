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
import org.junit.Assert;
import org.mbari.jpax.NonManagedEAOImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 11, 2009
 * Time: 11:06:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class AnnoCrudTest {

    public final Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void bigTest() {
        Injector injector = Guice.createInjector(new VarsJpaTestModule());
        AnnotationFactory af = injector.getInstance(AnnotationFactory.class);
        AnnotationTestObjectFactory factory = new AnnotationTestObjectFactory(af);
        IVideoArchiveSet vas = factory.makeObjectGraph("BIG-TEST", 2);


        AnnotationDAOFactory adf = injector.getInstance(AnnotationDAOFactory.class);
        IVideoArchiveSetDAO dao = adf.newVideoArchiveSetDAO();

        EntityUtilities eu = new EntityUtilities(new NonManagedEAOImpl("test"));
        log.info("ANNOTATION TREE BEFORE TEST:\n" + eu.buildTextTree(vas));

        vas = dao.makePersistent(vas);
        Assert.assertNotNull("Primary Key [ID] was not set!", ((VideoArchiveSet) vas).getId());        

        vas = dao.findByPrimaryKey(vas.getClass(), ((VideoArchiveSet) vas).getId());

        log.info("ANNOTATION TREE AFTER TEST:\n" + eu.buildTextTree(vas));
        
    }

    
}
