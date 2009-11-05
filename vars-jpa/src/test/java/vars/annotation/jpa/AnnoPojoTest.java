package vars.annotation.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;
import com.google.inject.Injector;
import com.google.inject.Guice;
import vars.jpa.VarsJpaTestModule;
import vars.jpa.EntityUtilities;
import vars.annotation.AnnotationFactory;
import vars.annotation.VideoArchiveSet;
import vars.testing.AnnotationTestObjectFactory;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 12, 2009
 * Time: 11:10:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class AnnoPojoTest {

        public final Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void bigTest() {
        Injector injector = Guice.createInjector(new VarsJpaTestModule());
        AnnotationFactory af = injector.getInstance(AnnotationFactory.class);
        AnnotationTestObjectFactory factory = new AnnotationTestObjectFactory(af);
        VideoArchiveSet vas = factory.makeObjectGraph("BIG-TEST", 2);

        EntityUtilities eu = new EntityUtilities();
        log.info("ANNOTATION TREE FOR toString TEST:\n" + eu.buildTextTree(vas));


    }
}
