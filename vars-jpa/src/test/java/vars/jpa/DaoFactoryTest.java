package vars.jpa;

import com.google.inject.Injector;
import com.google.inject.Guice;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.annotation.AnnotationDAOFactory;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 11, 2009
 * Time: 9:56:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class DaoFactoryTest {

    @Test
    public void test01() {
        Injector injector = Guice.createInjector(new VarsJpaTestModule());
        KnowledgebaseDAOFactory kf = injector.getInstance(KnowledgebaseDAOFactory.class);
        assertNotNull(kf.newConceptDAO());
        assertNotNull(kf.newConceptMetadataDAO());
        assertNotNull(kf.newConceptNameDAO());
        assertNotNull(kf.newHistoryDAO());
        assertNotNull(kf.newLinkRealizationDAO());
        assertNotNull(kf.newLinkTemplateDAO());
        assertNotNull(kf.newMediaDAO());
        assertNotNull(kf.newUsageDAO());

        AnnotationDAOFactory af = injector.getInstance(AnnotationDAOFactory.class);
        assertNotNull(af.newAssociationDAO());
        assertNotNull(af.newCameraDataDAO());
        assertNotNull(af.newCameraDeploymentDAO());
        assertNotNull(af.newObservationDAO());
        assertNotNull(af.newPhysicalDataDAO());
        assertNotNull(af.newVideoArchiveDAO());
        assertNotNull(af.newVideoArchiveSetDAO());
    }

}
